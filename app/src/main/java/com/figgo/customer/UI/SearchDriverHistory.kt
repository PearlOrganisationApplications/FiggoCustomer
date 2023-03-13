package com.figgo.customer.UI

import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Geocoder
import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.view.isVisible
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.figgo.cabs.figgodriver.Fragment.RideHistory
import com.figgo.cabs.figgodriver.MapData
import com.figgo.cabs.figgodriver.Service.FireBaseService
import com.figgo.customer.R
import com.figgo.customer.UI.AdavanceCityCabActivity.ThankyouScreenAdvProceed
import com.figgo.customer.UI.CityCabActivity
import com.figgo.customer.UI.DashBoard
import com.figgo.customer.UI.OneWayOutstationActivity.OneWay_Activity
import com.figgo.customer.UI.PaymentMethodActivity
import com.figgo.customer.Util.MapUtility
import com.figgo.customer.pearlLib.BaseClass
import com.figgo.customer.pearlLib.Helper
import com.figgo.customer.pearlLib.PrefManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.libraries.places.api.Places
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList


class SearchDriverHistory : BaseClass() , PaymentResultListener,OnMapReadyCallback ,GoogleMap.OnMarkerClickListener {
    lateinit var pref: PrefManager
    lateinit var progressDialog:ProgressDialog
    lateinit var cTimer : CountDownTimer
    var count :Int = 0
    var count1 :Int = 0
    var txtTimer: TextView? = null
    var txtPer: TextView? = null
    var transaction_id :String ?= ""
    var  driver_id:String? = null
    var  ride_id:String? = null

    //  lateinit var driverlayout:ConstraintLayout
    var v_number:String = ""
    var prices:String = ""
    var name:String = ""
    var v_name:String = ""
    var dlnumber:String = ""
    var driver_image:String = ""
    var taxi_image:String = ""
    var activavehiclenumber:TextView? = null
    var dl_number:TextView? = null
    var drivername:TextView? = null
    var vehiclename:TextView? = null
    var price:TextView? = null
    var textView:TextView? = null
    var mobilenumber:TextView? = null
    var activaimg:ImageView? = null
    var  driverimg:CircleImageView? = null
    var ll_search:LinearLayout? = null
    var  ll_details:LinearLayout? = null
    var x:Int=-1
    lateinit var mProgressBar: ProgressBar
    lateinit var mainConst: ConstraintLayout
    var i = 0
    var tv_tolocationdetails:TextView? = null
    var tv_fromlocationdetails:TextView? = null
    lateinit var intents:String
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Driver Found"
    var paymentMethodActivity = PaymentMethodActivity()
    private var originLatitude: Double = 28.5021359
    private var originLongitude: Double = 77.4054901
    private var destinationLatitude: Double = 28.5151087
    private var destinationLongitude: Double = 77.3932163
    private var waypointsLatitude : Double = 28.5151087
    private var waypointsLongitude : Double = 77.3932163
    lateinit var geocoder: Geocoder
    var rideId:String=""
    private lateinit var driverlocation:LatLng
    private var mMap: GoogleMap? = null
    var destination:MarkerOptions? = null
    lateinit var timer : CountDownTimer
    var customerLoc:LatLng? = null
    var driverLoc:LatLng? = null
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var toLat = ""
    var toLong = ""
    var fromLat = ""
    var fromLong = ""

    lateinit var driver_name:TextView
    lateinit var activanumber:TextView
    var pickupLocation: LatLng? = null
    var dropLocation: LatLng? = null
    var pontos: List<LatLng> = java.util.ArrayList()
    var polyline: Polyline? = null
    var line: Polyline? = null

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
        setContentView(R.layout.activity_historysearch_driver)
        pref = PrefManager(this@SearchDriverHistory)
//        var tv_click = findViewById<TextView>(R.id.tv_click)
        // ll_details  = findViewById<ConstraintLayout>(R.id.ll_details)
        txtTimer = findViewById<TextView>(R.id.txt_timer)
        txtPer = findViewById<TextView>(R.id.txt_per)
      //  var tv_accept = findViewById<TextView>(R.id.tv_accept)
        //   var shareimg = findViewById<ImageView>(R.id.shareimg)
        //   var backimg = findViewById<ImageView>(R.id.backimg)
        activaimg = findViewById<ImageView>(R.id.activaimg)
        mainConst = findViewById<ConstraintLayout>(R.id.mainConst)
        activavehiclenumber = findViewById<TextView>(R.id.activavehiclenumber)
        driverimg = findViewById<CircleImageView>(R.id.driverimg)
        drivername = findViewById<TextView>(R.id.drivername)
        vehiclename = findViewById<TextView>(R.id.vehiclename)
        price = findViewById<TextView>(R.id.price)
        tv_tolocationdetails = findViewById<TextView>(R.id.tv_tolocationdetails)
        tv_fromlocationdetails = findViewById<TextView>(R.id.tv_fromlocationdetails)
        // textView = findViewById<TextView>(R.id.textView)
        //  var iv_bellicon = findViewById<ImageView>(R.id.iv_bellicon)
        //   var ride_service_rating = findViewById<RatingBar>(R.id.ride_service_rating)
        dl_number = findViewById<TextView>(R.id.dl_number)
        ll_search = findViewById<LinearLayout>(R.id.ll_main)
        ll_details = findViewById<LinearLayout>(R.id.ll_details)
        // var ll_back = findViewById<LinearLayout>(R.id.ll_back)
        var tv_next = findViewById<TextView>(R.id.tv_next)
        driver_id = intent.getStringExtra("driver_id")
        ride_id = intent.getStringExtra("ride_id")
        pref = PrefManager(this)

        mProgressBar = findViewById<ProgressBar>(R.id.progressbar)
        mProgressBar.setProgress(i)
        tv_tolocationdetails?.text = pref.getLiveLoc()
        tv_fromlocationdetails?.text = pref.getManualLoc()
        /* getcurrentdriverdetails()*/

        mainConst?.isVisible = true
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
      /*  tv_accept.setOnClickListener {


            if (::cTimer.isInitialized) {
                cTimer.cancel()
            }
            if (::timer.isInitialized) {
                timer.cancel()
            }
            val myService = Intent(
                this@SearchDriverHistory,
                FireBaseService

                ::class.java
            )
            stopService(myService)
            showPendingPopup()
        }*/
/*        iv_bellicon.setOnClickListener {
            startActivity(Intent(this, NotificationBellIconActivity::class.java))
        }*/
        tv_next.setOnClickListener {

            if (::cTimer.isInitialized) {
                cTimer.cancel()
            }
            if (::timer.isInitialized) {
                timer.cancel()
            }
            val dialog = Dialog(this@SearchDriverHistory)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.emergency_dialog)
            val body = dialog.findViewById(R.id.error) as TextView
            val alert = dialog.findViewById(R.id.alert) as TextView
            alert.isVisible = false
            body.setText("Dear Sir/Ma'am , your ride fare is Rs "+pref.getPrice()+" and pick up time is "+pref.getTime()+" . If 100% sure for this ride then accept or otherwise reject. Charges Apply after 5 Min delay")

            val yesBtn = dialog.findViewById(R.id.ok) as TextView
            val canBtn = dialog.findViewById(R.id.cancel) as TextView

            yesBtn.setOnClickListener {
                dialog.dismiss()
                pref.setSearchBack("")
                pref.setToLatL("")
                pref.setToLngL("")
                pref.setToLatM("")
                pref.setToLngM("")
                dialog.dismiss()
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
                } catch (e: Exception) {
                    Toast.makeText(this, "Error in payment: " + e.message, Toast.LENGTH_SHORT).show();
                    e.printStackTrace()
                }

            }



            canBtn.setOnClickListener {

                dialog.dismiss()
                val URL = Helper.ADVANCE_RIDE_DELETE
                val progressDialog = ProgressDialog(this)
                progressDialog.show()
                val queue = Volley.newRequestQueue(this)
                val json = JSONObject()
                json.put("ride_id", pref.getRideId())
                val jsonOblect: JsonObjectRequest =
                    object : JsonObjectRequest(Method.POST, URL, json, object :
                        Response.Listener<JSONObject?> {
                        @SuppressLint("SuspiciousIndentation")
                        override fun onResponse(response: JSONObject?) {
                            Log.d("SendData", "response===" + response)
                            if (response != null) {
                                progressDialog.hide()
                                try {

                                    val status = response.getString("status")

                                    if (status.equals("true")) {
                                        pref.setSearchBack("")
                                        pref.setToLatL("")
                                        pref.setToLngL("")
                                        pref.setToLatM("")
                                        pref.setToLngM("")
                                        startActivity(
                                            Intent(
                                                this@SearchDriverHistory,
                                                DashBoard::class.java
                                            )
                                        )
                                        finish()

                                    } else {

                                        Toast.makeText(
                                            this@SearchDriverHistory,
                                            "Something went wrong!",
                                            Toast.LENGTH_LONG
                                        ).show()

                                    }


                                } catch (e: Exception) {
                                    MapUtility.showDialog(e.toString(), this@SearchDriverHistory)

                                }
                            }

                        }
                    }, object : Response.ErrorListener {
                        override fun onErrorResponse(error: VolleyError?) {
                            progressDialog.hide()
                            Log.d("SendData", "error===" + error)
                            // Toast.makeText(this@Current_Driver_Details_List, "Something went wrong!", Toast.LENGTH_LONG).show()

                            MapUtility.showDialog(error.toString(), this@SearchDriverHistory)
                        }
                    }) {

                        @Throws(AuthFailureError::class)
                        override fun getHeaders(): Map<String, String> {
                            val headers: MutableMap<String, String> = java.util.HashMap()
                            headers.put("Content-Type", "application/json; charset=UTF-8")
                            headers.put("Authorization", "Bearer " + pref.getToken())
                            headers.put("Accept", "application/vnd.api+json")
                            return headers
                        }
                    }

                queue.add(jsonOblect)


            }

            if (!(this@SearchDriverHistory as Activity).isFinishing) {
                dialog.show()
            }
            val window: Window? = dialog.getWindow()
            window?.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )


        }
        /* ll_back.setOnClickListener {
             val URL = Helper.ride_delete
             val progressDialog = ProgressDialog(this)
             progressDialog.show()
             val queue = Volley.newRequestQueue(this)
             val json = JSONObject()
             json.put("ride_id", pref.getRideId())
             val jsonOblect: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URL, json, object :
                 Response.Listener<JSONObject?>               {
                 @SuppressLint("SuspiciousIndentation")
                 override fun onResponse(response: JSONObject?) {
                     Log.d("SendData", "response===" + response)
                     if (response != null) {
                         progressDialog.hide()
                         try {

                             val status = response.getString("status")

                             if (status.equals("true")){

                                 val dialog = Dialog(this@SearchDriver)
                                 dialog.setCancelable(false)
                                 dialog.setContentView(R.layout.serach_driver_dialog)
                                 val body = dialog.findViewById(R.id.error) as TextView

                                 val yesBtn = dialog.findViewById(R.id.ok) as Button
                                 val canBtn = dialog.findViewById(R.id.cancel) as Button
                                 yesBtn.setOnClickListener {
                                     pref.setSearchBack("")
                                     startActivity(Intent(this@SearchDriver,CityCabActivity::class.java))
                                     finish()
                                 }
                                 canBtn.setOnClickListener {
                                     dialog.dismiss()
                                 }
                                 if (!(this@SearchDriver as Activity).isFinishing) {
                                     dialog.show()
                                 }
                                 val window: Window? = dialog.getWindow()
                                 window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

                             }else{

                                 Toast.makeText(this@SearchDriver, "Something went wrong!", Toast.LENGTH_LONG).show()

                             }



                         }catch (e:Exception){
                             MapUtility.showDialog(e.toString(),this@SearchDriver)

                         }
                     }

                 }
             }, object : Response.ErrorListener {
                 override fun onErrorResponse(error: VolleyError?) {
                     progressDialog.hide()
                     Log.d("SendData", "error===" + error)
                     // Toast.makeText(this@Current_Driver_Details_List, "Something went wrong!", Toast.LENGTH_LONG).show()

                     MapUtility.showDialog(error.toString(),this@SearchDriver)
                 }
             }) {

                 @Throws(AuthFailureError::class)
                 override fun getHeaders(): Map<String, String> {
                     val headers: MutableMap<String, String> = java.util.HashMap()
                     headers.put("Content-Type", "application/json; charset=UTF-8")
                     headers.put("Authorization", "Bearer " + pref.getToken())
                     headers.put("Accept", "application/vnd.api+json")
                     return headers
                 }
             }

             queue.add(jsonOblect)



         }*/

        //  rideId=pref.getride_id()
        //pref.setSearchBack("1")
        // shareimg()
        // onBackPress()
        // intents = intent.getStringExtra("intent").toString()

    }














    @SuppressLint("MissingInflatedId")
    private fun showPendingPopup() {
        //Create a View object yourself through inflater


        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.row_pending_layout)
        val txt_msg = dialog.findViewById<TextView>(R.id.txt_msg)
        txt_msg.setText("Dear Sir/Ma'am , your ride fare is Rs "+pref.getPrice()+" and pick up time is "+pref.getTime()+" . If 100% sure for this ride then accept or ortherwise reject. Charges Apply after 5 Min delay")
        val imageView: ImageView = dialog.findViewById(R.id.iv_check)
        val book_now: TextView = dialog.findViewById(R.id.book_now)
        imageView.setOnClickListener { dialog.dismiss() }
        book_now.setOnClickListener {



            /* if (ContextCompat.checkSelfPermission(
                     this@SearchDriver,
                     Manifest.permission.READ_SMS
                 ) != PackageManager.PERMISSION_GRANTED
             ) {
                 ActivityCompat.requestPermissions(
                     this@SearchDriver,
                     arrayOf(
                         Manifest.permission.READ_SMS,
                         Manifest.permission.RECEIVE_SMS
                     ),
                     101
                 )
             }
             val Service = PaytmPGService.getProductionService()
             val paramMap = java.util.HashMap<String, String>()
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
                 this@SearchDriver,
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
                 })*/

        }

        dialog.show()
        val window: Window? = dialog.getWindow()
        window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)


    }
    private fun searchDriver() {
        // progressDialog = ProgressDialog(this@SearchDriver)
        //  progressDialog.show()
        val queue = Volley.newRequestQueue(this@SearchDriverHistory)
        val json = JSONObject()
        json.put("ride_id" ,pref.getride_id())
        Log.d("SendData", pref.getride_id())
        val jsonOblect: JsonObjectRequest =  object : JsonObjectRequest(Method.POST, Helper.ADVANCE_SEARCH_DRIVER, json, object : Response.Listener<JSONObject?> {
            override fun onResponse(response: JSONObject?) {
                Log.d("SendData", "response===" + response)
                if (response != null) {
                    //  progressDialog.hide()
                    try {
                        val searching_status = response.getString("searching_status")
                        /*Log.d("SendData", "searching_status===" + searching_status)*/
                        if (searching_status.equals("0")) {

                            Toast.makeText(this@SearchDriverHistory, "Unable to find driver...", Toast.LENGTH_LONG).show()

                        } else if (searching_status.equals("1")) {



                            getRideStatus()


                        }
                    }catch (e:Exception){
                        MapUtility.showDialog(e.toString(),this@SearchDriverHistory)

                    }
                } else{
                    Toast.makeText(this@SearchDriverHistory, "Unable to search driver...", Toast.LENGTH_LONG).show()
                }
            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {

                // progressDialog.hide()
                MapUtility.showDialog(error.toString(),this@SearchDriverHistory)

                /* Toast.makeText(context, "Something went wrong!"+error, Toast.LENGTH_LONG).show()*/
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers.put("Content-Type", "application/json; charset=UTF-8")
                headers.put("Authorization", "Bearer " + pref.getToken())
                headers.put("Accept", "application/vnd.api+json") //put your token here
                return headers
            }
        }

        queue.add(jsonOblect)

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun addNotification() {
        val mNotificationManager: NotificationManager
        val mBuilder = NotificationCompat.Builder(this@SearchDriverHistory, "notify_001")
        val intent = Intent(this@SearchDriverHistory, SearchDriverHistory::class.java)
        pref.setNotify("true")
        var pendingIntent: PendingIntent? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this@SearchDriverHistory, 0, intent, PendingIntent.FLAG_MUTABLE)
        }else {
            pendingIntent = PendingIntent.getActivity(this@SearchDriverHistory, 0, intent, 0)
        }
        val bigText = NotificationCompat.BigTextStyle()
        bigText.setBigContentTitle("Booking is Accepted")
        bigText.bigText("Your Booking is Accepted by driver...")
        bigText.setSummaryText("Text in detail")

        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setSmallIcon(R.drawable.appicon)
        mBuilder.setContentTitle("Booking is Accepted")
        mBuilder.setContentText("Your Booking is Accepted by driver...")
        mBuilder.priority = Notification.PRIORITY_MAX
        mBuilder.setStyle(bigText)

        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "notify_001"
            val channel = NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_HIGH)
            mNotificationManager.createNotificationChannel(channel)
            mBuilder.setChannelId(channelId)
        }

        mNotificationManager.notify(10, mBuilder.build())
    }
    fun cancelNotification(ctx: Context, notifyId: Int) {
        val ns: String = Context.NOTIFICATION_SERVICE
        val nMgr = ctx.getSystemService(ns) as NotificationManager
        nMgr.cancel(notifyId)
    }
    private fun String(s: String): String? {

        return s
    }

    private fun getRideStatus() {

        val URL = Helper.AD_CHECK_RIDE_REQUEST_STATUS
        Log.d("searchDriver", "json===" +URL )
        Log.d("SendData", pref.getride_id() )
        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        json.put("ride_id", pref.getRideId())
        // json.put("ride_id", "1070")
        val jsonOblect: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, URL, json,
                Response.Listener<JSONObject?> { response ->
                    if (response != null) {
                        try {
                            if (::cTimer.isInitialized) {
                                cTimer.cancel()
                            }
                            // ll_details?.visibility = View.VISIBLE
                            x = 1
                            Log.d("Ride Status", "response===" + response)

                            val status = response.getString("status")

                            if (status.equals("true")) {
                               // startService(Intent(this@SearchDriverHistory, FireBaseService::class.java))
                                if(pref.getNotify().equals("false")) {
                                    addNotification()
                                }else  if(pref.getNotify().equals("true")) {
                                }

                                ll_search?.isVisible = false
                                ll_details?.isVisible = true
                                // RunAnimation()
                                prices = response.getJSONObject("data").getString("price")

                                driver_id = response.getJSONObject("data").getString("driver_id")
                                pref.setdriver_id(driver_id!!)
                                name = response.getJSONObject("data").getJSONObject("ride_driver")
                                    .getString("name")
                                dlnumber =
                                    response.getJSONObject("data").getJSONObject("ride_driver")
                                        .getString("dl_number")
                                v_number =
                                    response.getJSONObject("data").getJSONObject("ride_driver")
                                        .getJSONObject("cab").getString("v_number")


                                v_name =
                                    response.getJSONObject("data").getJSONObject("ride_driver")
                                        .getJSONObject("cab").getJSONObject("cab_detail").getString("name")
                                // val full_image = response.getJSONObject("data").getJSONObject("ride_driver").getJSONObject("cab").getJSONObject("cab_detail").getString("full_image")
                                pref.setReqRideId(response.getJSONObject("data").getString("id"))
                                val rating_avg = response.getJSONObject("data").getJSONObject("ride_driver").getString("rating_avg")
                                driver_image = response.getJSONObject("data").getJSONObject("ride_driver").getJSONObject("documents").getString("driver_image")
                                taxi_image = response.getJSONObject("data").getJSONObject("ride_driver").getJSONObject("documents").getString("taxi_image")
                                // Picasso.get().load(full_image).into(activaimg)
                                drivername?.setText(name)
                                dl_number?.setText(dlnumber)
                                activavehiclenumber?.setText(v_number)
                                price?.setText("\u20B9"+prices)
                                vehiclename?.setText(v_name)
                                pref.setPrice(prices)
                                if(!driver_image.equals("")){
                                    Picasso.get().load(driver_image).placeholder(R.drawable.girl_img).into(driverimg)
                                }

                                if(!taxi_image.equals("")){
                                    Picasso.get().load(taxi_image).placeholder(R.drawable.blueactiva_img).into(activaimg)
                                }


                            } else if (status.equals("false")) {
                                //  Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()

                                startTimer()

                            }

                        }catch (e:Exception){
                            MapUtility.showDialog(e.toString(),this@SearchDriverHistory)

                        }
                    }
                }, object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {

                        Log.d("SendData", "error===" + error)
                        //

                        MapUtility.showDialog(error.toString(),this@SearchDriverHistory)
                    }
                }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> = java.util.HashMap()
                    headers.put("Content-Type", "application/json; charset=UTF-8");
                    headers.put("Authorization", "Bearer " + pref.getToken());
                    headers.put("Accept", "application/vnd.api+json");
                    return headers
                }
            }

        queue.add(jsonOblect)

    }
    override fun onPaymentSuccess(s: String?) {
        mainConst?.isVisible = false

        progressDialog = ProgressDialog(this)
        progressDialog.show()
        Toast.makeText(this, "payment successful", Toast.LENGTH_SHORT).show()

        try {
            transaction_id = s


            cancelNotification(this@SearchDriverHistory,10)
            getOtp()

        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Exception in onPaymentSuccess", e)
        }

    }

    override fun onPaymentError(i: Int, s: String?) {
        Toast.makeText(this, "Payment failed$i", Toast.LENGTH_SHORT).show()

    }

    private fun getAcceptRide() {

        val URL =Helper.AD_ACCEPT_CITYRIDE
        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        json.put("ride_request_id", pref.getReqRideId())
        json.put("ride_id", pref.getride_id())
        Log.d("sentData",json.toString())
        //  Log.d("rides",pref.getride_id())
        val jsonOblect: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, URL, json, object :
                Response.Listener<JSONObject?>               {
                @SuppressLint("SuspiciousIndentation")
                override fun onResponse(response: JSONObject?) {

                    Log.d("SendData", "response===" + response)
                    if (response != null) {
                        try {

                            val status = response.getString("status")
                            if (status.equals("true")) {

                                progressDialog.hide()
                              //  val intent = Intent(this@SearchDriverHistory, ThankyouScreenAdvProceed::class.java)
/*
                                intent.putExtra("name", name)
                                intent.putExtra("dl_number", dlnumber)
                                intent.putExtra("veh_number", v_number)
                                intent.putExtra("price", prices)
                                intent.putExtra("transaction_id",transaction_id)
                                intent.putExtra("driver_image",driver_image)
                                intent.putExtra("taxi_image",taxi_image)
                                intent.putExtra("veh_name",v_name)
*/

                               // startActivity(intent)


                            } else {

                            }
                        }catch (e:Exception){
                            MapUtility.showDialog(e.toString(),this@SearchDriverHistory)

                        }
                    }

                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    // progressDialog.hide()
                    Log.d("SendData", "error===" + error)
                    // Toast.makeText(this@Current_Driver_Details_List, "Something went wrong!", Toast.LENGTH_LONG).show()

                    MapUtility.showDialog(error.toString(),this@SearchDriverHistory)
                }
            }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> = java.util.HashMap()
                    headers.put("Content-Type", "application/json; charset=UTF-8");
                    headers.put("Authorization", "Bearer " + pref.getToken());
                    headers.put("Accept", "application/vnd.api+json");
                    return headers
                }
            }

        queue.add(jsonOblect)

    }
    private fun getOtp() {
        val progressDialog = ProgressDialog(this@SearchDriverHistory)
        progressDialog.show()
        val URL = Helper.ADVANCE_UPDATE_CITY_RIDE_PAYMENT_STATUS
        val queue = Volley.newRequestQueue(this@SearchDriverHistory)
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
                            startActivity(Intent(this@SearchDriverHistory, ThankyouScreenAdvProceed::class.java))

                            /* supportFragmentManager.beginTransaction().apply {
                                 replace(R.id.cabdetailsframe, thankyouScreenFragment)
                                 commit()
                                 mainConst.isVisible = true

                             }*/
                        }catch (e:Exception){
                            MapUtility.showDialog(e.toString(),this@SearchDriverHistory)

                        }
                    }

                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("SendData", "error===" + error)
                    progressDialog.hide()
                    //Toast.makeText(this@CabDetailsActivity, "Something went wrong!", Toast.LENGTH_LONG).show()
                    MapUtility.showDialog(error.toString(),this@SearchDriverHistory)
                }
            }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> = java.util.HashMap()
                    headers.put("Content-Type", "application/json; charset=UTF-8");
                    headers.put("Authorization", "Bearer " + pref.getToken());
                    headers.put("Accept", "application/vnd.api+json");
                    return headers
                }
            }

        queue.add(jsonOblect)

    }


    fun startTimer() {

        cTimer = object : CountDownTimer(300000, 1000) {
            override fun onTick(millisUntilFinished: Long) {//300000
                // txtTimer?.setText("seconds remaining: " +""+ (millisUntilFinished / 1000).toString())
                i++
                mProgressBar.progress = i as Int * 100 / (300000 / 1000)
                txtTimer?.setText("seconds : " +""+ i as Int * 100 / (100000 / 1000)+" ( max 300 sec)")
                txtPer?.setText("" +""+ i as Int * 100 / (300000 / 1000)+" %")

                if (mProgressBar.progress == 100){
                    if (::cTimer.isInitialized) {
                        cTimer.cancel()
                        finish()
                    }
                    Toast.makeText(this@SearchDriverHistory, "Unable to found nearby drivers...", Toast.LENGTH_LONG).show()
                    val URL = Helper.ADVANCE_RIDE_DELETE
                    val progressDialog = ProgressDialog(this@SearchDriverHistory)
                    progressDialog.show()
                    val queue = Volley.newRequestQueue(this@SearchDriverHistory)
                    val json = JSONObject()
                    json.put("ride_id", pref.getRideId())
                    val jsonOblect: JsonObjectRequest =
                        object : JsonObjectRequest(Method.POST, URL, json, object :
                            Response.Listener<JSONObject?> {
                            @SuppressLint("SuspiciousIndentation")
                            override fun onResponse(response: JSONObject?) {
                                Log.d("SendData", "response===" + response)
                                if (response != null) {
                                    progressDialog.hide()
                                    try {

                                        val status = response.getString("status")

                                        if (status.equals("true")) {

                                            finish()
                                            startActivity(Intent(this@SearchDriverHistory, DashBoard::class.java))
                                        } else {

                                            Toast.makeText(
                                                this@SearchDriverHistory,
                                                "Something went wrong!",
                                                Toast.LENGTH_LONG
                                            ).show()

                                        }


                                    } catch (e: Exception) {
                                        MapUtility.showDialog(e.toString(), this@SearchDriverHistory)

                                    }
                                }

                            }
                        }, object : Response.ErrorListener {
                            override fun onErrorResponse(error: VolleyError?) {
                                progressDialog.hide()
                                Log.d("SendData", "error===" + error)
                                // Toast.makeText(this@Current_Driver_Details_List, "Something went wrong!", Toast.LENGTH_LONG).show()

                                MapUtility.showDialog(error.toString(), this@SearchDriverHistory)
                            }
                        }) {

                            @Throws(AuthFailureError::class)
                            override fun getHeaders(): Map<String, String> {
                                val headers: MutableMap<String, String> = java.util.HashMap()
                                headers.put("Content-Type", "application/json; charset=UTF-8")
                                headers.put("Authorization", "Bearer " + pref.getToken())
                                headers.put("Accept", "application/vnd.api+json")
                                return headers
                            }
                        }

                    queue.add(jsonOblect)


                    // finish()
                }
                if (count % 10 ==  0) {
                    getRideStatus()

                    // Toast.makeText(this@SearchDriver, "fetching driver...", Toast.LENGTH_LONG).show()

                }
                count++

            }

            override fun onFinish() {
                Toast.makeText(this@SearchDriverHistory, "Unable to found nearby drivers...", Toast.LENGTH_LONG).show()
                // textView?.setText("Unable to found nearby drivers...")

                i++;
                mProgressBar.setProgress(100);

                /* deletePendingReq()*/
                //  finish()

            }
        }
        cTimer.start()
    }






    override fun onBackPressed() {
        val dialog = Dialog(this@SearchDriverHistory)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.serach_driver_dialog)


        val yesBtn = dialog.findViewById(R.id.ok) as TextView
        val canBtn = dialog.findViewById(R.id.cancel) as TextView
        yesBtn.setOnClickListener {
            dialog.dismiss()
            pref.setSearchBack("")

            val URL = Helper.ADVANCE_RIDE_DELETE
            val progressDialog = ProgressDialog(this)
            progressDialog.show()
            val queue = Volley.newRequestQueue(this)
            val json = JSONObject()
            json.put("ride_id", pref.getRideId())
            val jsonOblect: JsonObjectRequest =
                object : JsonObjectRequest(Method.POST, URL, json, object :
                    Response.Listener<JSONObject?> {
                    @SuppressLint("SuspiciousIndentation")
                    override fun onResponse(response: JSONObject?) {
                        Log.d("SendData", "response===" + response)
                        if (response != null) {
                            progressDialog.hide()
                            try {

                                val status = response.getString("status")

                                if (status.equals("true")) {


                                     pref.setToLatL("")
                                    pref.setToLngL("")
                                     pref.setToLatM("")
                                    pref.setToLngM("")
                                    startActivity(Intent(this@SearchDriverHistory, DashBoard::class.java))
                                    finish()

                                } else {

                                    Toast.makeText(
                                        this@SearchDriverHistory,
                                        "Something went wrong!",
                                        Toast.LENGTH_LONG
                                    ).show()

                                }


                            } catch (e: Exception) {
                                MapUtility.showDialog(e.toString(), this@SearchDriverHistory)

                            }
                        }

                    }
                }, object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {
                        progressDialog.hide()
                        Log.d("SendData", "error===" + error)
                        // Toast.makeText(this@Current_Driver_Details_List, "Something went wrong!", Toast.LENGTH_LONG).show()

                        MapUtility.showDialog(error.toString(), this@SearchDriverHistory)
                    }
                }) {

                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers: MutableMap<String, String> = java.util.HashMap()
                        headers.put("Content-Type", "application/json; charset=UTF-8")
                        headers.put("Authorization", "Bearer " + pref.getToken())
                        headers.put("Accept", "application/vnd.api+json")
                        return headers
                    }
                }

            queue.add(jsonOblect)

        }
        canBtn.setOnClickListener {
            dialog.dismiss()
        }
        if (!(this@SearchDriverHistory as Activity).isFinishing) {
            dialog.show()
        }
        val window: Window? = dialog.getWindow()
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )



    }

    override fun onResume() {
        super.onResume()


        if(pref.getNotify().equals("false")) {
            searchDriver()

        }else  if(pref.getNotify().equals("true")) {
           getRideStatus()

        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        toLat = pref.getToLatL()
        toLong = pref.getToLngL()
        fromLat = pref.getToLatM()
        fromLong = pref.getToLngM()


        pickupLocation = LatLng(toLat.toDouble(), toLong.toDouble())
        dropLocation = LatLng(fromLat.toDouble(), fromLong.toDouble())
        val height = 100
        val width = 100
        val bitmapdraw = resources.getDrawable(R.drawable.pic_location) as BitmapDrawable
        val b = bitmapdraw.bitmap
        val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
        mMap?.addMarker(
            MarkerOptions().position(pickupLocation!!)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                .title("pickup")
        )
        val bitmapdraw2 = resources.getDrawable(R.drawable.drop_location) as BitmapDrawable
        val b2 = bitmapdraw2.bitmap
        val smallMarker2 = Bitmap.createScaledBitmap(b2, width, height, false)
        mMap?.addMarker(
            MarkerOptions().position(dropLocation!!)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker2))
                .title("dropoff")

        )

        val source = "" + toLat + "," + toLong
        val destination = "" + fromLat + "," + fromLong
        Log.e("Origin ", "$source\n Destination $destination")
        GetDirection().execute(source, destination)


        //   val myLocation = LatLng(30.302810, 78.012234)
        //  mMap.addMarker(MarkerOptions().position(myLocation).title("Marker in Sydney"))
        //  mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
    }

    override fun onMarkerClick(p0: Marker): Boolean {

        TODO("Not yet implemented")
    }

    inner class GetDirection :
        AsyncTask<String?, String?, String?>() {



        override fun onPreExecute() {
            super.onPreExecute()
        }

        protected override fun doInBackground(vararg params: String?): String? {
            var stringUrl = ""


            stringUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=" + params[0] + "&destination=" + params[1] + "&key=" + "AIzaSyCbd3JqvfSx0p74kYfhRTXE7LZghirSDoU" + "&sensor=false"
            Log.e("URL : ", "" + stringUrl)
            val response = StringBuilder()
            try {
                val url = URL(stringUrl)
                val httpconn = url.openConnection() as HttpURLConnection
                if (httpconn.responseCode == HttpURLConnection.HTTP_OK) {
                    val input = BufferedReader(InputStreamReader(httpconn.inputStream), 8192)
                    var strLine: String? = null
                    while (input.readLine().also { strLine = it } != null) {
                        response.append(strLine)
                    }
                    input.close()
                }
                val jsonOutput = response.toString()
                val jsonObject = JSONObject(jsonOutput)

                // routesArray contains ALL routes
                val routesArray = jsonObject.getJSONArray("routes")
                // Grab the first route
                val route = routesArray.getJSONObject(0)
                val poly = route.getJSONObject("overview_polyline")
                val polyline = poly.getString("points")
                pontos = decodePoly(polyline)!!
                val legs = route.getJSONArray("legs")
                var steps: JSONObject
                var distance: JSONObject? = null
                var totalDistance = 0f
                for (i in 0 until legs.length()) {
                    steps = legs.getJSONObject(i)
                    distance = steps.getJSONObject("distance")
                    val total = distance.getString("text").split(" ".toRegex()).toTypedArray()
                    totalDistance += total[0].replace(",", "").toFloat()
                }
               // distanceString = "$totalDistance Km"
               // Log.e("Total Distance : ", "" + distanceString)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        private fun decodePoly(encoded: String): List<LatLng>? {
            val poly: MutableList<LatLng> = ArrayList()
            var index = 0
            val len = encoded.length
            var lat = 0
            var lng = 0
            while (index < len) {
                var b: Int
                var shift = 0
                var result = 0
                do {
                    b = encoded[index++].code - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20)
                val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
                lat += dlat
                shift = 0
                result = 0
                do {
                    b = encoded[index++].code - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20)
                val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
                lng += dlng
                val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
                poly.add(p)
            }
            return poly
        }


        override fun onPostExecute(file_url: String?) {


            var src1: LatLng? = null
            var dest: LatLng? = null
            for (i in 0 until pontos.size - 1) {
                Log.e("call poly ", "loop = $i")
                val src: LatLng = pontos.get(i)
                if (i == 0) {
                    src1 = src
                }
                dest = pontos.get(i + 1)
                polyline?.remove()
                try {
                    polyline = mMap?.addPolyline(

                        PolylineOptions().add(
                            LatLng(src.latitude, src.longitude),
                            LatLng(dest.latitude, dest.longitude)
                        ).width(7f).color(
                            Color.GREEN
                        ).geodesic(true)
                    )
                } catch (e: NullPointerException) {
                    Log.e("Error", "NullPointerException onPostExecute: $e")
                } catch (e2: Exception) {
                    Log.e("Error", "Exception onPostExecute: $e2")
                }
            }
            try {
                val builder = LatLngBounds.Builder()
                builder.include(src1!!)
                builder.include(dest!!)
                val bounds = builder.build()
                //                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                val padding = 250 // offset from edges of the map in pixels
                val cu = CameraUpdateFactory.newLatLngBounds(bounds, 100)
                mMap?.moveCamera(cu)
                this@SearchDriverHistory.runOnUiThread {
                    // kmsTxt?.setText("" + distanceString)
                }
            } catch (e: Exception) {
            }
        }
    }

}