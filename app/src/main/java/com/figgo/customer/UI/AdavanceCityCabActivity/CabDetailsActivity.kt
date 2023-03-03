package com.figgo.customer.UI.AdavanceCityCabActivity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.figgo.customer.pearlLib.PrefManager
import com.figgo.customer.pearlLib.BaseClass
import com.figgo.customer.R
import com.figgo.customer.Util.MapUtility
import com.figgo.customer.pearlLib.Helper
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.util.*


class CabDetailsActivity : BaseClass(), PaymentResultListener {
    lateinit var nav_controller: NavController
    lateinit var mainConst: ConstraintLayout
    var transaction_id :String ?= ""
    var thankyouScreenFragment = ThankyouScreen()
    lateinit var pref: PrefManager
    override fun setLayoutXml() {
        TODO("Not yet implemented")
    }

    override fun initializeViews() {
        TODO("Not yet implemented")
    }

    override fun initializeClickListners() {
        TODO("Not yet implemented")
    }

    override fun initializeInputs() {
        TODO("Not yet implemented")
    }

    override fun initializeLabels() {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cab_details)
        var window=window
        window.setStatusBarColor(Color.parseColor("#000F3B"))

        mainConst = findViewById<ConstraintLayout>(R.id.mainConst)

        var nav_host_fragment=supportFragmentManager.findFragmentById(R.id.nav_controller) as NavHostFragment
        nav_controller=nav_host_fragment.navController
        pref = PrefManager(this@CabDetailsActivity)
//        var book_now=findViewById<TextView>(R.id.book_now)
//        book_now.setOnClickListener {
//            Toast.makeText(this,"your cab is book successfully",Toast.LENGTH_LONG)
//
//        }

        mainConst.isVisible = true

    }

    override fun onPaymentSuccess(s: String?) {
        mainConst.isVisible = false
        Toast.makeText(this@CabDetailsActivity, "payment successful", Toast.LENGTH_SHORT).show()

              try {
                 transaction_id = s
                  getOtp()
              } catch (e: Exception) {
                  Log.e(TAG, "Exception in onPaymentSuccess", e)
              }

        }

    override fun onPaymentError(i: Int, s: String?) {
        Toast.makeText(this@CabDetailsActivity, "Payment failed$i", Toast.LENGTH_SHORT).show()

    }
    private fun getOtp() {
        val progressDialog = ProgressDialog(this@CabDetailsActivity)
        progressDialog.show()
        val URL = Helper.ADVANCE_UPDATE_CITY_RIDE_PAYMENT_STATUS
        val queue = Volley.newRequestQueue(this@CabDetailsActivity)
        val json = JSONObject()
        json.put("transaction_id", transaction_id.toString())
        json.put("payment_type", "card")
        json.put("ride_id", pref.getRideId())
      //  json.put("ride_request_id", pref.getReqRideId())
        Log.d("transac",transaction_id.toString())
    //    Log.d("rides",pref.getride_id())
        val jsonOblect: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, URL, json, object :
                Response.Listener<JSONObject?>               {
                @SuppressLint("SuspiciousIndentation")
                override fun onResponse(response: JSONObject?) {

                    Log.d("SendData", "response===" + response)
                    if (response != null) {
                        try {

                            progressDialog.hide()
                            val booking_no = response.getJSONObject("ride").getString("booking_id")
                           // val otp = response.getInt("otp")

                          //  pref.setOtp(otp.toString())
                            pref.setBookingNo(booking_no)
                            startActivity(Intent(this@CabDetailsActivity, ThankyouScreen::class.java))

                           /* supportFragmentManager.beginTransaction().apply {
                                replace(R.id.cabdetailsframe, thankyouScreenFragment)
                                commit()
                                mainConst.isVisible = true

                            }*/
                        }catch (e:Exception){
                            MapUtility.showDialog(e.toString(),this@CabDetailsActivity)

                            }
                    }

                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("SendData", "error===" + error)
                    progressDialog.hide()
                    //Toast.makeText(this@CabDetailsActivity, "Something went wrong!", Toast.LENGTH_LONG).show()
                    MapUtility.showDialog(error.toString(),this@CabDetailsActivity)
                }
            }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> = HashMap()
                    headers.put("Content-Type", "application/json; charset=UTF-8");
                    headers.put("Authorization", "Bearer " + pref.getToken());
                    headers.put("Accept", "application/vnd.api+json");
                    return headers
                }
            }

        queue.add(jsonOblect)

    }


}