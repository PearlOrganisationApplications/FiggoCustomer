package com.figgo.customer.Fragments.AdvanceCityCab_Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
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
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class PayFragment : Fragment()  {
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pay, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var pay_now = view.findViewById<Button>(R.id.pay_now)

        psg_name = view.findViewById<EditText>(R.id.psg_name)
        psg_contact = view.findViewById<EditText>(R.id.psg_contact_no)
        pick_address = view.findViewById<EditText>(R.id.pick_address)
        landmark = view.findViewById<EditText>(R.id.landmark)
        pick_location = view.findViewById<TextView>(R.id.pick_location)
        msg = view.findViewById<EditText>(R.id.msg)
        pref = PrefManager(requireActivity())

        var backtxt =view.findViewById<TextView>(R.id.backtxt)
        var backimg =view.findViewById<ImageView>(R.id.backimg)
        var shareimg = view.findViewById<ImageView>(R.id.shareimg)

        backimg.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_payFragment_to_cabBookFragment)
        }

        backtxt.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_payFragment_to_cabBookFragment)
        }

        shareimg.setOnClickListener {
            var intent= Intent()
            intent.action= Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT,"I am Inviting you to join  Figgo App for better experience to book cabs")
            intent.setType("text/plain")
            startActivity(Intent.createChooser(intent, "Invite Friends"))
        }


        val apiKey = getString(R.string.api_key)
        if (!Places.isInitialized()) {
            Places.initialize(requireActivity(), apiKey)
        }

        pick_location?.setOnClickListener {


            val field = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field)
                .build(requireActivity())
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }



        pay_now.setOnClickListener {

            if (psg_name?.text.toString().equals("")){
                Toast.makeText(requireActivity(), "Please type Passenger Name", Toast.LENGTH_LONG).show()

            }else if (psg_contact?.text.toString().equals("")){
                Toast.makeText(requireActivity(), "Please type Passenger Name", Toast.LENGTH_LONG).show()

            }else {

               // getPayUNow()
                getPayNow();
            }

        }

    }

    private fun getPayNow() {
        val progressDialog = ProgressDialog(requireActivity())
        progressDialog.show()
        val URL =Helper.ENTER_PASSENGER_DETAILS
        val queue = Volley.newRequestQueue(requireContext())
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
        val jsonOblect: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URL, json, object :
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
                            obj.put("send_sms_hash", true)
                            obj.put("allow_rotation", true)
                            obj.put("currency", "INR")
                            obj.put("amount", amount)
                            val preFill = JSONObject()
                            preFill.put("email", "support@figgocabs.com")
                            preFill.put("contact", "91" + "9715597855")
                            obj.put("prefill", preFill)
                            checkout.open(requireActivity(), obj)
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
                    MapUtility.showDialog(error.toString(),requireActivity())
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



   private fun getPayUNow() {

        val URL ="https://test.pearl-developer.com/figo/api/ride/enter-passenger-details"
        val queue = Volley.newRequestQueue(requireContext())
        val json = JSONObject()
        json.put("name", psg_name?.text.toString())
        json.put("contact", psg_contact?.text.toString())
        json.put("lat", "")
        json.put("lng", "")
        json.put("pickup_address","")
        json.put("landmark", "")
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


                        if (ContextCompat.checkSelfPermission(
                                requireActivity(),
                                Manifest.permission.READ_SMS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(
                                    Manifest.permission.READ_SMS,
                                    Manifest.permission.RECEIVE_SMS
                                ),
                                101
                            )
                        }
                        val Service = PaytmPGService.getProductionService()
                        val paramMap = HashMap<String, String>()
                        //these are mandatory parameters
                        //these are mandatory parameters
                        paramMap.put( "MID" , "rxazcv89315285244163");
// Key in your staging and production MID available in your dashboard
                        paramMap.put( "ORDER_ID" , "order1");
                        paramMap.put( "CUST_ID" , "cust123");
                        paramMap.put( "MOBILE_NO" , "7777777777");
                        paramMap.put( "EMAIL" , "username@emailprovider.com");
                        paramMap.put( "CHANNEL_ID" , "WAP");
                        paramMap.put( "TXN_AMOUNT" , "100.12");
                        paramMap.put( "WEBSITE" , "WEBSTAGING");
// This is the staging value. Production value is available in your dashboard
                        paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
// This is the staging value. Production value is available in your dashboard
                        paramMap.put( "CALLBACK_URL", "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=order1");
                        paramMap.put( "CHECKSUMHASH" , "w2QDRMgp1234567JEAPCIOmNgQvsi+BhpqijfM9KvFfRiPmGSt3Ddzw+oTaGCLneJwxFFq5mqTMwJXdQE2EzK4px2xruDqKZjHupz9yXev4=")
                        val Order = PaytmOrder(paramMap);
                       // val Certificate = PaytmClientCertificate( inPassword:String, inFileName:String);
                        Service.initialize(Order, null);
                        Service.startPaymentTransaction(
                            requireActivity(),
                            true,
                            true,
                            object : PaytmPaymentTransactionCallback {
                                /*Call Backs*/
                                override fun someUIErrorOccurred(inErrorMessage: String) {}
                                override fun onTransactionResponse(inResponse: Bundle?) {}
                                override fun networkNotAvailable() {}
                                override fun onErrorProceed(p0: String?) {
                                    TODO("Not yet implemented")
                                }

                                override fun clientAuthenticationFailed(inErrorMessage: String) {}
                                override fun onErrorLoadingWebPage(
                                    iniErrorCode: Int,
                                    inErrorMessage: String,
                                    inFailingUrl: String
                                ) {
                                }

                                override fun onBackPressedCancelTransaction() {}
                                override fun onTransactionCancel(
                                    inErrorMessage: String,
                                    inResponse: Bundle
                                ) {
                                }
                            })
// inPassword is the password for client side certificate
//inFileName is the file name of client side certificate

                        /*  val payUPaymentParams = PayUPaymentParams.Builder()
                            .setAmount("1.0")
                        .setIsProduction(true)
                        .setKey("0MQaQP")
                        .setProductInfo("Test")
                        .setPhone("9999999999")
                        .setTransactionId(System.currentTimeMillis().toString())
                        .setFirstName("John")
                        .setEmail("John@gmail.com")
                       // .setSurl("https://payu.response.firebaseapp.com/success")
                      //  .setFurl("https://payu.response.firebaseapp.com/failure")
                        //Optional, can contain any additional PG params
                        .build()
                        PayUCheckoutPro.open(
                            requireActivity(), payUPaymentParams,
                            object : PayUCheckoutProListener {


                                override fun onPaymentSuccess(response: Any) {
                                    response as HashMap<*, *>
                                    val payUResponse = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                                    val merchantResponse = response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]
                                }


                                override fun onPaymentFailure(response: Any) {
                                    response as HashMap<*, *>
                                    val payUResponse = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                                    val merchantResponse = response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]
                                }


                                override fun onPaymentCancel(isTxnInitiated:Boolean) {
                                }


                                override fun onError(errorResponse: ErrorResponse) {
                                    val errorMessage: String
                                    if (errorResponse != null && errorResponse.errorMessage != null && errorResponse.errorMessage!!.isNotEmpty())
                                        errorMessage = errorResponse.errorMessage!!
                                    else {
                                        //   errorMessage = resources.getString(R.string.some_error_occurred)
                                    }
                                }

                                override fun setWebViewProperties(webView: WebView?, bank: Any?) {
                                    //For setting webview properties, if any. Check Customized Integration section for more details on this
                                }

                                override fun generateHash(
                                    valueMap: HashMap<String, String?>,
                                    hashGenerationListener: PayUHashGenerationListener
                                ) {
                                    if ( valueMap.containsKey(CP_HASH_STRING)
                                        && valueMap.containsKey(CP_HASH_STRING) != null
                                        && valueMap.containsKey(CP_HASH_NAME)
                                        && valueMap.containsKey(CP_HASH_NAME) != null) {

                                        val hashData = valueMap[CP_HASH_STRING]
                                        val hashName = valueMap[CP_HASH_NAME]

                                        //Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.
                                       /* val hash: String? = hashString
                                        if (!TextUtils.isEmpty(hash)) {
                                            val dataMap: HashMap<String, String?> = HashMap()
                                            dataMap[hashName!!] = hash!!
                                            hashGenerationListener.onHashGenerated(dataMap)
                                        }*/
                                    }
                                }
                            }
                        )*/

                    }
                    // Get your json response and convert it to whatever you want.
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("SendData", "error===" + error)
                    Toast.makeText(requireActivity(), "Something went wrong!", Toast.LENGTH_LONG).show()

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
                val status = Autocomplete.getStatusFromIntent(data!!)
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
            }
        }
    }



}


