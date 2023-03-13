package com.figgo.customer.UI

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.figgo.customer.R
import com.figgo.customer.UI.AdavanceCityCabActivity.ThankyouScreenAdvProceed
import com.figgo.customer.Util.MapUtility
import com.figgo.customer.pearlLib.BaseClass
import com.figgo.customer.pearlLib.Helper
import com.figgo.customer.pearlLib.PrefManager
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class HistoryMapsActivity : BaseClass() , PaymentResultListener {
    var transaction_id :String ?= ""
    lateinit var pref: PrefManager
    lateinit var cTimer : CountDownTimer
    lateinit var booking_idtxt : TextView
    lateinit var to_loctxt : TextView
    lateinit var from_loctxt : TextView
    lateinit var statusTxt : TextView
    lateinit var timeTxt : TextView
    lateinit var dateTxt : TextView
    lateinit var payRel : RelativeLayout
    lateinit var searchRel : LinearLayout
    var count :Int = 0
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
        setContentView(R.layout.activity_history_maps)
        var shareimg = findViewById<ImageView>(R.id.shareimg)
        var backimg = findViewById<ImageView>(R.id.backimg)
        booking_idtxt = findViewById<TextView>(R.id.booking_id)
         to_loctxt = findViewById<TextView>(R.id.to_loc)
         from_loctxt = findViewById<TextView>(R.id.from_loc)
         statusTxt = findViewById<TextView>(R.id.status)
        var cancel = findViewById<TextView>(R.id.cancel)
        var pay = findViewById<Button>(R.id.continu)
         payRel = findViewById<RelativeLayout>(R.id.payRel)
         searchRel = findViewById<LinearLayout>(R.id.searchRel)

         dateTxt = findViewById<TextView>(R.id.date)
        var search = findViewById<TextView>(R.id.search)
       // var reject = findViewById<TextView>(R.id.reject)
         timeTxt = findViewById<TextView>(R.id.time)
        var iv_bellicon = findViewById<ImageView>(R.id.iv_bellicon)
        pref = PrefManager(this@HistoryMapsActivity)

        shareimg()
        onBackPress()

        startTimer()

        val ride_id = intent.getStringExtra("ride_id")!!

    /*   val hashmap = MapUtility.paramMap.get(pos-1)


       val booking_id = hashmap?.get("booking_id")
        val to_loc = hashmap?.get("to_loc")
        val from_loc = hashmap?.get("from_loc")
        val status = hashmap?.get("status")
        val date = hashmap?.get("date")
        val time = hashmap?.get("time")
                val ride_id = hashmap?.get("ride_id")
       */



            pref.setRideId(ride_id)

        Log.d("rite_id",pref.getRideId())



        iv_bellicon.setOnClickListener {
            startActivity(Intent(this, NotificationBellIconActivity::class.java))
        }
        cancel.setOnClickListener {
          //  startActivity(Intent(this, DashBoard::class.java))
            finish()
        }

        search.setOnClickListener {
            pref.setNotify("false")
            startActivity(Intent(this, SearchDriverHistory::class.java))
        }

        pay.setOnClickListener {
            val amt = 1
            val amount = Math.round(amt.toFloat() * 100).toInt()
            val checkout = Checkout()
            checkout.setKeyID("rzp_test_9N5qfy5gXIQ81Y")
            checkout.setImage(R.drawable.appicon)
            val obj = JSONObject()
            try {
                obj.put("name", "Figgo")
                obj.put("description", "Payment")
                obj.put("theme.color", "")
                obj.put("currency", "INR")
                obj.put("amount", amount)
                val preFill = JSONObject()
                preFill.put("email", "support@figgocabs.com")
                preFill.put("contact", "91" + "9715597855")
                obj.put("prefill", preFill)
                checkout.open(this@HistoryMapsActivity, obj)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

    }
    fun startTimer() {

        cTimer = object : CountDownTimer(300000, 1000) {
            override fun onTick(millisUntilFinished: Long) {//300000
                if (count % 10 ==  0) {
                    getDetails()

                    // Toast.makeText(this@SearchDriver, "fetching driver...", Toast.LENGTH_LONG).show()

                }
                count++
            }

            override fun onFinish() {
                startTimer()

            }
        }
        cTimer.start()
    }

    override fun onPaymentSuccess(s: String?) {

        Toast.makeText(this@HistoryMapsActivity, "payment successful", Toast.LENGTH_SHORT).show()

        try {
            transaction_id = s
            getOtp()
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Exception in onPaymentSuccess", e)
        }

    }

    override fun onPaymentError(i: Int, s: String?) {
        Toast.makeText(this@HistoryMapsActivity, "Payment failed$i", Toast.LENGTH_SHORT).show()

    }


    private fun getDetails() {

        val URL = Helper.ADVANCE_DETAILS_PROCEED
        val queue = Volley.newRequestQueue(this@HistoryMapsActivity)
        val json = JSONObject()

        json.put("ride_id", pref.getRideId())
        //  json.put("ride_request_id", pref.getReqRideId())
        // Log.d("transac",transaction_id.toString())
        //  Log.d("rides",pref.getride_id())
        val jsonOblect: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, URL, json, object :
                Response.Listener<JSONObject?>               {
                @SuppressLint("SuspiciousIndentation")
                override fun onResponse(response: JSONObject?) {

                    Log.d("SendData", "response===" + response)
                    if (response != null) {
                        try {


                          //  val booking_no = response.getJSONObject("ride").getString("booking_id")
                            // val otp = response.getInt("otp")

                            //  pref.setOtp(otp.toString())
                            val booking_id = response.getJSONObject("ride").getString("booking_id")
                            val to_loc = response.getJSONObject("ride").getJSONObject("to_location").getString("name")
                            val from_loc = response.getJSONObject("ride").getJSONObject("from_location").getString("name")
                            val status = response.getJSONObject("ride").getString("status")
                            val date = response.getJSONObject("ride").getString("date_only")
                            val time = response.getJSONObject("ride").getString("time_only")
                            val type = response.getJSONObject("ride").getString("type")
                            val price = response.getJSONObject("ride").getString("price")
                            pref.setToLatL(response.getJSONObject("ride")
                                .getJSONObject("to_location").getString("lat"))
                            pref.setToLngL(response.getJSONObject("ride")
                                .getJSONObject("to_location").getString("lng"))

                            pref.setToLatM(response.getJSONObject("ride")
                                .getJSONObject("from_location").getString("lat"))
                            pref.setToLngM(response.getJSONObject("ride")
                                .getJSONObject("from_location").getString("lng"))
                            booking_idtxt.setText(booking_id)
                            to_loctxt.setText(to_loc)
                            from_loctxt.setText(from_loc)
                            statusTxt.setText(status)
                            dateTxt.setText(date)
                            timeTxt.setText(time)
                            pref.setTime(time)
                            pref.setPrice(price)



                            Log.d("type",type)
                            Log.d("status",status)
                            if (type.equals("advance_booking")){

                                if (status.equals("accepted")){

                                    payRel.isVisible = true
                                    searchRel.isVisible = false
                                }else if (status.equals("pending")){
                                    payRel.isVisible = false
                                    searchRel.isVisible = true
                                }

                            }else{
                                payRel.isVisible = false
                                searchRel.isVisible = false
                            }

                        }catch (e:Exception){
                            MapUtility.showDialog(e.toString(),this@HistoryMapsActivity)

                        }
                    }

                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("SendData", "error===" + error)

                    //Toast.makeText(this@CabDetailsActivity, "Something went wrong!", Toast.LENGTH_LONG).show()
                    MapUtility.showDialog(error.toString(),this@HistoryMapsActivity)
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



    private fun getOtp() {
        val progressDialog = ProgressDialog(this@HistoryMapsActivity)
        progressDialog.show()
        val URL = Helper.ADVANCE_UPDATE_CITY_RIDE_PAYMENT_STATUS
        val queue = Volley.newRequestQueue(this@HistoryMapsActivity)
        val json = JSONObject()
        json.put("transaction_id", transaction_id.toString())
        json.put("payment_type", "card")
        json.put("ride_id", pref.getRideId())
        //  json.put("ride_request_id", pref.getReqRideId())
       // Log.d("transac",transaction_id.toString())
      //  Log.d("rides",pref.getride_id())
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
                            startActivity(Intent(this@HistoryMapsActivity, ThankyouScreenAdvProceed::class.java))

                            /* supportFragmentManager.beginTransaction().apply {
                                 replace(R.id.cabdetailsframe, thankyouScreenFragment)
                                 commit()
                                 mainConst.isVisible = true

                             }*/
                        }catch (e:Exception){
                            MapUtility.showDialog(e.toString(),this@HistoryMapsActivity)

                        }
                    }

                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("SendData", "error===" + error)
                    progressDialog.hide()
                    //Toast.makeText(this@CabDetailsActivity, "Something went wrong!", Toast.LENGTH_LONG).show()
                    MapUtility.showDialog(error.toString(),this@HistoryMapsActivity)
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
    override fun onBackPressed() {
        finish()
    }
}