package com.figgo.customer.UI

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.figgo.customer.pearlLib.PrefManager
import com.figgo.customer.R
import com.figgo.customer.Util.MapUtility
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class PaymentPayActivity : AppCompatActivity(), PaymentResultListener {
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_pay)

        var pay_now = findViewById<Button>(R.id.pay_now)

        psg_name = findViewById<EditText>(R.id.psg_name)
        psg_contact = findViewById<EditText>(R.id.psg_contact_no)
        pick_address = findViewById<EditText>(R.id.pick_address)
        landmark = findViewById<EditText>(R.id.landmark)
        pick_location = findViewById<TextView>(R.id.pick_location)
        msg = findViewById<EditText>(R.id.msg)
        pref = PrefManager(this@PaymentPayActivity)
        val apiKey = getString(R.string.api_key)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        pick_location?.setOnClickListener {


            val field = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field)
                .build(this@PaymentPayActivity)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }



        pay_now.setOnClickListener {



            if (psg_name?.text.toString().equals("")){
                Toast.makeText(this@PaymentPayActivity, "Please type Passenger Name", Toast.LENGTH_LONG).show()

            }else if (psg_contact?.text.toString().equals("")){
                Toast.makeText(this@PaymentPayActivity, "Please type Passenger Contact", Toast.LENGTH_LONG).show()

            }else if (pick_address?.text.toString().equals("")){
                Toast.makeText(this@PaymentPayActivity, "Please type Pick Up Address", Toast.LENGTH_LONG).show()

            }else if (landmark?.text.toString().equals("")){
                Toast.makeText(this@PaymentPayActivity, "Please type Landmark", Toast.LENGTH_LONG).show()

            }else if (lat.equals("")){
                Toast.makeText(this@PaymentPayActivity, "Please select Pick Up Location", Toast.LENGTH_LONG).show()

            }else {
                getPayNow();
            }

        }

    }

    private fun getPayNow() {
        val progressDialog = ProgressDialog(this@PaymentPayActivity)
        progressDialog.show()
        val URL ="https://test.pearl-developer.com/figo/api/ride/enter-passenger-details"
        val queue = Volley.newRequestQueue(this@PaymentPayActivity)
        val json = JSONObject()
        json.put("name", psg_name?.text.toString())
        json.put("contact", psg_contact?.text.toString())
        json.put("lat", lat)
        json.put("lng", lng)
        json.put("pickup_address", pick_address?.text.toString())
        json.put("landmark", landmark?.text.toString())
        json.put("ride_id", pref.getRideId())
        if (msg?.text.toString().equals("")){
            msg?.text = null
        }
        json.put("additional_message ", msg?.text.toString())




        val jsonOblect: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, URL, json, object :
                Response.Listener<JSONObject?>               {
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
                            obj.put("currency", "INR")
                            obj.put("amount", amount)
                            val preFill = JSONObject()
                            preFill.put("email", "support@figgocabs.com")
                            preFill.put("contact", "91" + "9715597855")
                            obj.put("prefill", preFill)
                            checkout.open(this@PaymentPayActivity, obj)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        //  view?.let { Navigation.findNavController(it).navigate(R.id.action_payFragment_to_paymentWayFragment) }



                    }
                    // Get your json response and convert it to whatever you want.
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("SendData", "error===" + error)
                   // Toast.makeText(this@PaymentPayActivity, "Something went wrong!", Toast.LENGTH_LONG).show()
                    progressDialog.hide()
                    MapUtility.showDialog(error.toString(),this@PaymentPayActivity)

                }
            }) {
                @SuppressLint("SuspiciousIndentation")
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)

                pick_location!!.setText(place.address)
                lat = place.latLng.latitude.toString()
                lng = place.latLng.longitude.toString()

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(
                    data!!
                )
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
            }
        }
    }
    override fun onPaymentSuccess(s: String?) {
        Toast.makeText(this@PaymentPayActivity, "Payment is successfull : " + s, Toast.LENGTH_SHORT).show()
    }
    override fun onPaymentError(p0: Int, s: String?) {
        Toast.makeText(this@PaymentPayActivity, "Payment Failed due to error : " + s, Toast.LENGTH_SHORT).show()
    }

}