package com.figgo.customer.UI.OneWayOutstationActivity

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.figgo.customer.R
import com.figgo.customer.Util.MapUtility
import com.figgo.customer.pearlLib.Helper
import com.figgo.customer.pearlLib.PrefManager
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class PayActivity : AppCompatActivity() , PaymentResultListener {
    var psg_name: EditText? = null
    var psg_contact: EditText? = null
    var pick_address: EditText? = null
    var landmark: EditText? = null
    var pick_location: TextView? = null
    var msg: EditText? = null
    var AUTOCOMPLETE_REQUEST_CODE = -1
    var lat :String ?= ""
    var lng :String ?= ""
    lateinit var pref: PrefManager
    var transaction_id :String ?= ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_pay)

        var pay_now = findViewById<Button>(R.id.pay_now)

        psg_name = findViewById<EditText>(R.id.psg_name)
        psg_contact = findViewById<EditText>(R.id.psg_contact_no)
        pick_address = findViewById<EditText>(R.id.pick_address)
        landmark = findViewById<EditText>(R.id.landmark)
        pick_location = findViewById<TextView>(R.id.pick_location)
        msg = findViewById<EditText>(R.id.msg)
        pref = PrefManager(this@PayActivity)

        var backtxt =findViewById<TextView>(R.id.backtxt)
        var backimg =findViewById<ImageView>(R.id.backimg)
        var shareimg = findViewById<ImageView>(R.id.shareimg)

        backimg.setOnClickListener {
        }

        backtxt.setOnClickListener {
        }

        shareimg.setOnClickListener {
            var intent= Intent()
            intent.action= Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT,"I am Inviting you to join  Figgo App for better experience to book cabs")
            intent.setType("text/plain")
            startActivity(Intent.createChooser(intent, "Invite Friends"))
        }

/*
        val apiKey = getString(R.string.api_key)
        if (!Places.isInitialized()) {
            Places.initialize(this@PayActivity, apiKey)
        }

        pick_location?.setOnClickListener {


            val field = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field)
                .build(requireActivity())
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }*/



        pay_now.setOnClickListener {

            if (psg_name?.text.toString().equals("")){
                Toast.makeText(this@PayActivity, "Please type Passenger Name", Toast.LENGTH_LONG).show()

            }else if (psg_contact?.text.toString().equals("")){
                Toast.makeText(this@PayActivity, "Please type Passenger Name", Toast.LENGTH_LONG).show()

            }else {

               // getPayUNow()
                getPayNow();
            }

        }

    }

    private fun getPayNow() {
        val progressDialog = ProgressDialog(this@PayActivity)
        progressDialog.show()
        val URL =Helper.ENTER_PASSENGER_DETAILS_ONEWAY
        val queue = Volley.newRequestQueue(this@PayActivity)
        val json = JSONObject()
        json.put("name", psg_name?.text.toString())
        json.put("contact", psg_contact?.text.toString())
       // json.put("lat", "")
       // json.put("lng", "")
       // json.put("pickup_address", "")
      //  json.put("landmark", "")
        json.put("ride_id", pref.getride_id())
        if (msg?.text.toString().equals("")){
            msg?.text = null
        }
        json.put("additional_message ", msg?.text.toString())

        Log.d("rif",pref.getride_id())
        val jsonOblect: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URL, json, object : Response.Listener<JSONObject?>               {
                @SuppressLint("SuspiciousIndentation")
                override fun onResponse(response: JSONObject?) {

                    Log.d("SendData", "response===" + response)
                    if (response != null) {

                        progressDialog.hide()
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
                            checkout.open(this@PayActivity, obj)
                        } catch (e: JSONException) {
                            Toast.makeText(getApplicationContext(), "Error in payment: " + e.message, Toast.LENGTH_SHORT).show();
                            e.printStackTrace()
                        }
                        //  view?.let { Navigation.findNavController(it).navigate(R.id.action_payFragment_to_paymentWayFragment) }



                    }
                    // Get your json response and convert it to whatever you want.
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("SendData", "error===" + error)
                  //  Toast.makeText(requireActivity(), "Something went wrong!", Toast.LENGTH_LONG).show()
                    progressDialog.hide()
                    MapUtility.showDialog(error.toString(),this@PayActivity)
                }
            }) {
                @SuppressLint("SuspiciousIndentation")
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> = HashMap()
                    headers.put("Content-Type", "application/json; charset=UTF-8");
                    headers.put("Authorization", "Bearer " + pref.getToken())
                    headers.put("Accept", "application/vnd.api+json");
                    return headers
                }
            }

        queue.add(jsonOblect)

    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)

                pick_location!!.setText(place.address)
                    lat = place.latLng.latitude.toString()
                   lng = place.latLng.longitude.toString()

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
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
        val progressDialog = ProgressDialog(this@PayActivity)
        progressDialog.show()
        val URL = Helper.ONEWAY_UPDATE_CITY_RIDE_PAYMENT_STATUS
        val queue = Volley.newRequestQueue(this@PayActivity)
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
                            startActivity(Intent(this@PayActivity, ThankyouScreenOneway::class.java))

                            /* supportFragmentManager.beginTransaction().apply {
                                 replace(R.id.cabdetailsframe, thankyouScreenFragment)
                                 commit()
                                 mainConst.isVisible = true

                             }*/
                        }catch (e:Exception){
                            MapUtility.showDialog(e.toString(),this@PayActivity)

                        }
                    }

                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("SendData", "error===" + error)
                    progressDialog.hide()
                    //Toast.makeText(this@CabDetailsActivity, "Something went wrong!", Toast.LENGTH_LONG).show()
                    MapUtility.showDialog(error.toString(),this@PayActivity)
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


