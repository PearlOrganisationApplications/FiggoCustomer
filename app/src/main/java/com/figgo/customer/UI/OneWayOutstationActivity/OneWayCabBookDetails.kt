package com.figgo.customer.UI.OneWayOutstationActivity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.figgo.customer.R
import com.figgo.customer.UI.AdavanceCityCabActivity.ThankyouScreen
import com.figgo.customer.UI.NotificationBellIconActivity
import com.figgo.customer.Util.MapUtility
import com.figgo.customer.pearlLib.BaseClass
import com.figgo.customer.pearlLib.Helper
import com.figgo.customer.pearlLib.PrefManager
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class OneWayCabBookDetails : BaseClass(), PaymentResultListener {
    lateinit var pref: PrefManager
    var tvdis_outtstation:TextView ? = null
    var tvdis2_outsttaion:TextView ? = null
    var tv_createdat:TextView ? = null
    var tv_updatedat:TextView ? = null
    var tv_toloc:TextView ? = null
    var tv_fromloc:TextView ? = null
    var tv_fare:TextView ? = null
    var transaction_id :String ?= ""



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
        setContentView(R.layout.activity_one_way_cab_book_details)
        onBackPress()
        shareimg()
        pref = PrefManager(this)
        tvdis_outtstation = findViewById<TextView>(R.id.tvdis_outtstation)
        tvdis2_outsttaion = findViewById<TextView>(R.id.tvdis2_outsttaion)
        tv_createdat = findViewById<TextView>(R.id.tv_createdat)
         tv_updatedat = findViewById<TextView>(R.id.tv_updatedat)
         tv_toloc = findViewById<TextView>(R.id.tv_toloc)
        tv_fromloc = findViewById<TextView>(R.id.tv_fromloc)
        tv_fare = findViewById<TextView>(R.id.tv_fare)
        var tv_bookother = findViewById<TextView>(R.id.tv_bookother)
        var tv_bookself = findViewById<TextView>(R.id.tv_bookforself)

        var  iv_bellicon = findViewById<ImageView>(R.id. iv_bellicon)
        getCabBookData()


        iv_bellicon.setOnClickListener {
            startActivity(Intent(this,NotificationBellIconActivity::class.java))
        }
        tv_bookself.setOnClickListener {
            val amt = pref.getPrice()
            val amount = Math.round(amt.toFloat() * 100).toInt()
            val checkout = Checkout()
            checkout.setKeyID("rzp_test_9N5qfy5gXIQ81Y")
            checkout.setImage(R.drawable.appicon)
            val obj = JSONObject()
            try {
                obj.put("name", "Figgo")
                obj.put("description", "Payment")
                obj.put("theme.color", "")
                obj.put("send_sms_hash", true)
                obj.put("allow_rotation", true)
                obj.put("currency", "INR")
                obj.put("amount", amount)
                val preFill = JSONObject()
                preFill.put("email", "support@figgocabs.com")
                preFill.put("contact", "91" + "9715597855")
                obj.put("prefill", preFill)
                checkout.open(this, obj)
            } catch (e: JSONException) {
                Toast.makeText(this, "Error in payment: " + e.message, Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }


        tv_bookother.setOnClickListener {
            startActivity(Intent(this@OneWayCabBookDetails, PayActivity::class.java))

        }




    }
    private fun getCabBookData() {
        val progressDialog = ProgressDialog(this@OneWayCabBookDetails)
        progressDialog.show()
        val URL =Helper.Vehicle_OneWay
        val queue = Volley.newRequestQueue(this@OneWayCabBookDetails)
        val json = JSONObject()
        json.put("ride_id", pref.getRideId())
        json.put("vehicle_type_id", pref.getVehicleId())
        val jsonOblect: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, URL, json, object :
                Response.Listener<JSONObject?>               {
                @SuppressLint("SuspiciousIndentation")
                override fun onResponse(response: JSONObject?) {

                    Log.d("SendData", "response===" + response)
                    if (response != null) {

                        progressDialog.hide()
                        try {

                            val distance = response.getJSONObject("data").getString("distance")
                            val createdAt = response.getJSONObject("data").getJSONObject("ride")
                                .getString("created_at")
                            val updatedAt = response.getJSONObject("data").getJSONObject("ride")
                                .getString("updated_at")
                            val time_only = response.getJSONObject("data").getJSONObject("vehicle")
                                .getString("time_only")
                            val max_price = response.getJSONObject("data").getJSONObject("vehicle")
                                .getString("max_price")
                            val min_price = response.getJSONObject("data").getJSONObject("vehicle")
                                .getString("min_price")
                            val to_location = response.getJSONObject("data").getJSONObject("ride")
                                .getJSONObject("to_location").getString("name")
                            val from_location = response.getJSONObject("data").getJSONObject("ride")
                                .getJSONObject("from_location").getString("name")


                            //  Picasso.get().load(full_image).into(image)
                            tv_createdat?.setText(createdAt)
                            tv_updatedat?.setText(updatedAt)
                            tvdis_outtstation?.setText(distance)
                            tvdis2_outsttaion?.setText(time_only)
                            tv_toloc?.setText(to_location)
                            tv_fromloc?.setText(from_location)
                            pref.setPrice(max_price)
                            tv_fare?.setText(
                                "Approx.. fare Rs. " + min_price + " to " + max_price + ",\n                for this ride\nwithout waiting  parking charge\nFinal Price is differ from Approx. "

                            )
                        }catch (e:Exception){
                            MapUtility.showDialog(e.toString(),this@OneWayCabBookDetails)

                        }
                    }
                    // Get your json response and convert it to whatever you want.
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("SendData", "error===" + error)
                    progressDialog.hide()

                    MapUtility.showDialog(error.toString(),this@OneWayCabBookDetails)
                    //  Toast.makeText(requireActivity(), "Something went wrong!", Toast.LENGTH_LONG).show()

                }
            }) {
                @SuppressLint("SuspiciousIndentation")
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> = HashMap()
                    headers.put("Content-Type", "application/json; charset=UTF-8");
                    headers.put("Authorization", "Bearer " + pref.getToken());
                    return headers
                }
            }

        queue.add(jsonOblect)

    }


    override fun onPaymentSuccess(s: String?) {

        Toast.makeText(this, "payment successful", Toast.LENGTH_SHORT).show()

        try {
            transaction_id = s


            getOtp()

        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Exception in onPaymentSuccess", e)
        }

    }

    override fun onPaymentError(i: Int, s: String?) {
        Toast.makeText(this, "Payment failed$i", Toast.LENGTH_SHORT).show()

    }
    private fun getOtp() {
        val progressDialog = ProgressDialog(this@OneWayCabBookDetails)
        progressDialog.show()
        val URL = Helper.ONEWAY_UPDATE_CITY_RIDE_PAYMENT_STATUS
        val queue = Volley.newRequestQueue(this@OneWayCabBookDetails)
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
                            startActivity(Intent(this@OneWayCabBookDetails, ThankyouScreenOneway::class.java))

                            /* supportFragmentManager.beginTransaction().apply {
                                 replace(R.id.cabdetailsframe, thankyouScreenFragment)
                                 commit()
                                 mainConst.isVisible = true

                             }*/
                        }catch (e:Exception){
                            MapUtility.showDialog(e.toString(),this@OneWayCabBookDetails)

                        }
                    }

                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("SendData", "error===" + error)
                    progressDialog.hide()
                    //Toast.makeText(this@CabDetailsActivity, "Something went wrong!", Toast.LENGTH_LONG).show()
                    MapUtility.showDialog(error.toString(),this@OneWayCabBookDetails)
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