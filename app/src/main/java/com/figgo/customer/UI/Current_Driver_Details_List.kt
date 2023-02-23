package com.figgo.customer.UI

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.figgo.customer.pearlLib.BaseClass
import com.figgo.customer.R
import com.figgo.customer.UI.CurrentCityCabActivity.EmergencyRoutedraweActivity
import com.figgo.customer.Util.MapUtility
import com.figgo.customer.pearlLib.PrefManager
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class Current_Driver_Details_List : BaseClass(), PaymentResultListener {
    var transaction_id :String ?= ""
    lateinit var pref: PrefManager
    var  driver_id:String? = null
    var  ride_id:String? = null
    var toLat:String = ""
    var toLong:String = ""
    var fromLat:String = ""
    var fromLong:String = ""
    var activavehiclenumber:TextView? = null
    var dl_number:TextView? = null
    var drivername:TextView? = null
    var mobilenumber:TextView? = null
    var activaimg:ImageView? = null
    var  driverimg:ImageView? = null

    var paymentMethodActivity = PaymentMethodActivity()

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
        setContentView(R.layout.activity_current_driver_details_list)

        var tv_accept = findViewById<TextView>(R.id.tv_accept)
        var shareimg = findViewById<ImageView>(R.id.shareimg)
        var backimg = findViewById<ImageView>(R.id.backimg)
        var activaimg = findViewById<ImageView>(R.id.activaimg)
        var activavehiclenumber = findViewById<TextView>(R.id.activavehiclenumber)
        var driverimg = findViewById<CircleImageView>(R.id.driverimg)
        var drivername = findViewById<TextView>(R.id.drivername)

        var ride_service_rating = findViewById<RatingBar>(R.id.ride_service_rating)
        var dl_number = findViewById<TextView>(R.id.dl_number)
        driver_id = intent.getStringExtra("driver_id")
        ride_id = intent.getStringExtra("ride_id")
        pref = PrefManager(this)
        var iv_bellicon = findViewById<ImageView>(R.id.iv_bellicon)
        shareimg()
        onBackPress()

        iv_bellicon.setOnClickListener {
            startActivity(Intent(this, NotificationBellIconActivity::class.java))
        }

       /* getcurrentdriverdetails()*/

        tv_accept.setOnClickListener {
            startActivity(Intent(this, EmergencyRoutedraweActivity::class.java))
            val amt = "1"
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
                Toast.makeText(this, "Error in payment: " + e.message, Toast.LENGTH_SHORT).show();
                e.printStackTrace()
            }
        }
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
        val progressDialog = ProgressDialog(this)
        progressDialog.show()
        val URL ="https://test.pearl-developer.com/figo/api/ride/update-city-ride-payment-status"
        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        json.put("transaction_id", transaction_id.toString())
        json.put("payment_type", "card")
        json.put("ride_id", pref.getRideId())
        Log.d("transac",transaction_id.toString())
        Log.d("rides",pref.getride_id())
        val jsonOblect: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, URL, json, object :
                Response.Listener<JSONObject?>               {
                @SuppressLint("SuspiciousIndentation")
                override fun onResponse(response: JSONObject?) {

                    Log.d("SendData", "response===" + response)
                    if (response != null) {
                        progressDialog.hide()

                        try {


                            val booking_no = response.getJSONObject("ride").getString("booking_id")
                            val otp = response.getInt("otp")

                            pref.setOtp(otp.toString())
                            pref.setBookingNo(booking_no)
                        }catch (e:Exception){
                            MapUtility.showDialog(e.toString(),this@Current_Driver_Details_List)

                        }
                      /*  supportFragmentManager.beginTransaction().apply {
                            replace(R.id.nav_controller, thankyouScreenFragment)
                            commit()
                        }*/
                    }

                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    progressDialog.hide()
                    Log.d("SendData", "error===" + error)
                   // Toast.makeText(this@Current_Driver_Details_List, "Something went wrong!", Toast.LENGTH_LONG).show()

                    MapUtility.showDialog(error.toString(),this@Current_Driver_Details_List)
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


   /* private fun getcurrentdriverdetails(){
        val progressDialog = ProgressDialog(this)
        progressDialog.show()
        val URL ="https://test.pearl-developer.com/figo/api/ride/get-driver"
        Log.d("SendData", "URL===" + URL)
        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        json.put("driver_id", driver_id)
        json.put("ride_id", ride_id)
        Log.d("SendData", "json===" + json)
        val jsonOblect: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URL, json, object : Response.Listener<JSONObject?>{
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(response: JSONObject?) {
                Log.d("SendData", "response===" + response)

                if (response != null) {
                    progressDialog.hide()
                    val dataobject = response.getJSONObject("data")
                    val driverObject = dataobject.getJSONObject("driver")
                    val cab_detailObject = dataobject.getJSONObject("cab_detail")
                    val rideObject = response.getJSONObject("ride")
                    val to_locationObject = rideObject.getJSONObject("to_location")
                    val from_locationObject = rideObject.getJSONObject("from_location")
                    toLat = to_locationObject.getString("lat")
                    toLong = to_locationObject.getString("lng")
                    fromLat = from_locationObject.getString("lat")
                    fromLong = from_locationObject.getString("lng")
                    val driverName = driverObject.getString("name")
                    val dlNumber = driverObject.getString("dl_number")
                    val rating = driverObject.getString("rating_avg")
                    val vNumber = dataobject.getString("v_number")
                    val docObject = driverObject.getJSONObject("documents")
                    val driver_image = docObject.getString("driver_image")
                    val taxi_image = docObject.getString("taxi_image")

                    // activavehiclenumber?.setText(cab_detailObject.getString("name")+" "+vNumber)
                    activavehiclenumber?.setText(vNumber)
                    drivername?.setText(driverName)
                    dl_number?.setText(dlNumber)
                   *//* ride_service_rating?.rating = rating.toFloat()*//*

                    if(!driver_image.equals("")){
                        Picasso.get().load(driver_image).placeholder(R.drawable.girl_img).into(driverimg)
                    }

                    if(!taxi_image.equals("")){
                        Picasso.get().load(taxi_image).placeholder(R.drawable.blueactiva_img).into(activaimg)
                    }

                }

            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("SendData", "error===" + error)
                Toast.makeText(this@Current_Driver_Details_List, "Something went wrong!", Toast.LENGTH_LONG).show()

            }
        }) {
            @SuppressLint("SuspiciousIndentation")
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers.put("Content-Type", "application/json; charset=UTF-8")
                headers.put("Authorization", "Bearer " + pref.getToken())
                headers.put("Accept", "application/vnd.api+json");
                return headers
            }
        }

        queue.add(jsonOblect)

    }*/

}