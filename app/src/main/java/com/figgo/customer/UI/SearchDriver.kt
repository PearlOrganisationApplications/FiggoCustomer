package com.figgo.customer.UI

import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.view.isVisible
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.figgo.customer.R
import com.figgo.customer.Util.MapUtility
import com.figgo.customer.pearlLib.BaseClass
import com.figgo.customer.pearlLib.Helper
import com.figgo.customer.pearlLib.PrefManager
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject


class SearchDriver : BaseClass() , PaymentResultListener {
    lateinit var pref: PrefManager
    lateinit var progressDialog:ProgressDialog
    lateinit var cTimer : CountDownTimer
    var count :Int = 0
    var txtTimer: TextView? = null
    var txtPer: TextView? = null
    var transaction_id :String ?= ""
    var  driver_id:String? = null
    var  ride_id:String? = null
    var toLat:String = ""
    lateinit var driverlayout:ConstraintLayout
    var v_number:String = ""
    var prices:String = ""
    var name:String = ""
    var dlnumber:String = ""
    var activavehiclenumber:TextView? = null
    var dl_number:TextView? = null
    var drivername:TextView? = null
    var price:TextView? = null
    var textView:TextView? = null
    var mobilenumber:TextView? = null
    var activaimg:ImageView? = null
    var  driverimg:ImageView? = null
    var ll_search:LinearLayout? = null
    var  ll_details:LinearLayout? = null
    var x:Int=-1
   lateinit var mProgressBar: ProgressBar

    var i = 0

    lateinit var intents:String
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Driver Found"
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
        setContentView(R.layout.activity_search_driver)
        pref = PrefManager(this@SearchDriver)
//        var tv_click = findViewById<TextView>(R.id.tv_click)
        driverlayout  = findViewById<ConstraintLayout>(R.id.layoutdriverdetails)
         txtTimer = findViewById<TextView>(R.id.txt_timer)
         txtPer = findViewById<TextView>(R.id.txt_per)
        var tv_accept = findViewById<TextView>(R.id.tv_accept)
        var shareimg = findViewById<ImageView>(R.id.shareimg)
        var backimg = findViewById<ImageView>(R.id.backimg)
         activaimg = findViewById<ImageView>(R.id.activaimg)
         activavehiclenumber = findViewById<TextView>(R.id.activavehiclenumber)
         driverimg = findViewById<CircleImageView>(R.id.driverimg)
         drivername = findViewById<TextView>(R.id.drivername)
         price = findViewById<TextView>(R.id.price)
       // textView = findViewById<TextView>(R.id.textView)
        var iv_bellicon = findViewById<ImageView>(R.id.iv_bellicon)
        var ride_service_rating = findViewById<RatingBar>(R.id.ride_service_rating)
         dl_number = findViewById<TextView>(R.id.dl_number)
        ll_search = findViewById<LinearLayout>(R.id.ll_main)
        ll_details = findViewById<LinearLayout>(R.id.ll_details)
        var ll_back = findViewById<LinearLayout>(R.id.ll_back)
        var tv_reject_btn = findViewById<TextView>(R.id.tv_reject_btn)
        driver_id = intent.getStringExtra("driver_id")
        ride_id = intent.getStringExtra("ride_id")
        pref = PrefManager(this)

        mProgressBar=findViewById<ProgressBar>(R.id.progressbar)
        mProgressBar.setProgress(i)

        /* getcurrentdriverdetails()*/

        tv_accept.setOnClickListener {
            showPendingPopup()
        }
        iv_bellicon.setOnClickListener {
            startActivity(Intent(this, NotificationBellIconActivity::class.java))
        }
        tv_reject_btn.setOnClickListener {
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

        }
        ll_back.setOnClickListener {
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



        }


        pref.setSearchBack("1")
       // shareimg()
       // onBackPress()
        // intents = intent.getStringExtra("intent").toString()

        if(pref.getNotify().equals("false")) {
            searchDriver()

        }else  if(pref.getNotify().equals("true")) {
            getRideStatus()

        }

    }

    private fun RunAnimation() {
        val a: Animation = AnimationUtils.loadAnimation(this, R.anim.scale)
        a.reset()
        val tv = findViewById<View>(R.id.text) as TextView
        tv.clearAnimation()
        tv.startAnimation(a)
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
        val book_now: Button = dialog.findViewById(R.id.book_now)
        imageView.setOnClickListener { dialog.dismiss() }
        book_now.setOnClickListener {

            pref.setSearchBack("")

            val amt = pref.getPrice()
            val amount = Math.round(amt.toFloat() * 100).toInt()
            val checkout = Checkout()
            checkout.setKeyID("rzp_test_capDM1KlnUhj5f")
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
        window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)


    }
    private fun searchDriver() {
       // progressDialog = ProgressDialog(this@SearchDriver)
      //  progressDialog.show()
        val queue = Volley.newRequestQueue(this@SearchDriver)
        val json = JSONObject()
        json.put("ride_id" ,pref.getride_id())
        Log.d("SendData", pref.getride_id())
        val jsonOblect: JsonObjectRequest =  object : JsonObjectRequest(Method.POST, Helper.Searching_driver, json, object : Response.Listener<JSONObject?> {
            override fun onResponse(response: JSONObject?) {
                Log.d("SendData", "response===" + response)
                if (response != null) {
                  //  progressDialog.hide()
                    try {
                        val searching_status = response.getString("searching_status")
                        /*Log.d("SendData", "searching_status===" + searching_status)*/
                        if (searching_status.equals("0")) {

                            Toast.makeText(this@SearchDriver, "Unable to find driver...", Toast.LENGTH_LONG).show()

                        } else if (searching_status.equals("1")) {



                            getRideStatus()


                        }
                    }catch (e:Exception){
                        MapUtility.showDialog(e.toString(),this@SearchDriver)

                    }
                } else{
                    Toast.makeText(this@SearchDriver, "Unable to search driver...", Toast.LENGTH_LONG).show()
                }
            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {

               // progressDialog.hide()
                MapUtility.showDialog(error.toString(),this@SearchDriver)

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
        val mBuilder = NotificationCompat.Builder(this@SearchDriver, "notify_001")
        val intent = Intent(this@SearchDriver, SearchDriver::class.java)
       pref.setNotify("true")
        var pendingIntent: PendingIntent? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this@SearchDriver, 0, intent, PendingIntent.FLAG_MUTABLE)
        }else {
            pendingIntent = PendingIntent.getActivity(this@SearchDriver, 0, intent, 0)
        }
        val bigText = NotificationCompat.BigTextStyle()
        bigText.setBigContentTitle("Booking is Accepted")
        bigText.bigText("Your Booking Accepted by driver Please wait...")
        bigText.setSummaryText("Text in detail")

        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setSmallIcon(R.drawable.appicon)
        mBuilder.setContentTitle("Booking is Accepted")
        mBuilder.setContentText("Your Booking Accepted by driver Please wait...")
        mBuilder.priority = Notification.PRIORITY_MAX
        mBuilder.setStyle(bigText)

        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "Your_channel_id"
            val channel = NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_HIGH)
            mNotificationManager.createNotificationChannel(channel)
            mBuilder.setChannelId(channelId)
        }

        mNotificationManager.notify(0, mBuilder.build())
    }

    private fun String(s: String): String? {

        return s
    }

    private fun getRideStatus() {

        val URL ="https://test.pearl-developer.com/figo/api/ride/check-ride-request-status"

        Log.d("searchDriver", "json===" +URL )
        Log.d("SendData", pref.getride_id() )
        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        json.put("ride_id", pref.getride_id())
     // json.put("ride_id", "1070")
        val jsonOblect: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, URL, json,
                Response.Listener<JSONObject?> { response ->
                    if (response != null) {
                        try {
                            if (::cTimer.isInitialized) {
                                cTimer.cancel()
                            }
                            driverlayout.visibility = View.VISIBLE
                            x = 1
                            Log.d("Ride Status", "response===" + response)

                            val status = response.getString("status")

                            if (status.equals("true")) {

                                if(pref.getNotify().equals("false")) {
                                    addNotification()
                                }else  if(pref.getNotify().equals("true")) {
                                }

                                ll_search?.isVisible = false
                                ll_details?.isVisible = true
                               // RunAnimation()
                                prices = response.getJSONObject("data").getString("price")
                                name = response.getJSONObject("data").getJSONObject("ride_driver")
                                    .getString("name")
                                dlnumber =
                                    response.getJSONObject("data").getJSONObject("ride_driver")
                                        .getString("dl_number")
                                v_number =
                                    response.getJSONObject("data").getJSONObject("ride_driver")
                                        .getJSONObject("cab").getString("v_number")
                               // val full_image = response.getJSONObject("data").getJSONObject("ride_driver").getJSONObject("cab").getJSONObject("cab_detail").getString("full_image")
                                pref.setReqRideId(response.getJSONObject("data").getString("id"))
                                val rating_avg = response.getJSONObject("data").getJSONObject("ride_driver").getString("rating_avg")

                               // Picasso.get().load(full_image).into(activaimg)
                                drivername?.setText(name)
                                dl_number?.setText(dlnumber)
                                activavehiclenumber?.setText(v_number)
                                price?.setText(prices)
                                pref.setPrice(prices)
                                /*   if(!full_image.equals("")){
                                Picasso.get().load(full_image).placeholder(R.drawable.girl_img).into(driverimg)
                            }*/

                                /* if(!taxi_image.equals("")){
                                Picasso.get().load(taxi_image).placeholder(R.drawable.blueactiva_img).into(activaimg)
                            }*/


                            } else if (status.equals("false")) {
                                //  Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()

                                startTimer()

                            }

                        }catch (e:Exception){
                            MapUtility.showDialog(e.toString(),this@SearchDriver)

                        }
                    }
                }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {

                    Log.d("SendData", "error===" + error)
                    //

                    MapUtility.showDialog(error.toString(),this@SearchDriver)
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
        Toast.makeText(this, "payment successful", Toast.LENGTH_SHORT).show()

        try {
            transaction_id = s
         //  getOtp()
           getAcceptRide()

        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Exception in onPaymentSuccess", e)
        }

    }

    override fun onPaymentError(i: Int, s: String?) {
        Toast.makeText(this, "Payment failed$i", Toast.LENGTH_SHORT).show()

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
                    Toast.makeText(this@SearchDriver, "Unable to found nearby drivers...", Toast.LENGTH_LONG).show()
                    val URL = Helper.ride_delete
                    val progressDialog = ProgressDialog(this@SearchDriver)
                    progressDialog.show()
                    val queue = Volley.newRequestQueue(this@SearchDriver)
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

                                            startActivity(
                                                Intent(
                                                    this@SearchDriver,
                                                    CityCabActivity::class.java
                                                )
                                            )
                                        } else {

                                            Toast.makeText(
                                                this@SearchDriver,
                                                "Something went wrong!",
                                                Toast.LENGTH_LONG
                                            ).show()

                                        }


                                    } catch (e: Exception) {
                                        MapUtility.showDialog(e.toString(), this@SearchDriver)

                                    }
                                }

                            }
                        }, object : Response.ErrorListener {
                            override fun onErrorResponse(error: VolleyError?) {
                                progressDialog.hide()
                                Log.d("SendData", "error===" + error)
                                // Toast.makeText(this@Current_Driver_Details_List, "Something went wrong!", Toast.LENGTH_LONG).show()

                                MapUtility.showDialog(error.toString(), this@SearchDriver)
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


                    finish()
                }
                if (count % 10 ==  0) {
                    getRideStatus()

                   // Toast.makeText(this@SearchDriver, "fetching driver...", Toast.LENGTH_LONG).show()

                }
                count++

            }

            override fun onFinish() {
                Toast.makeText(this@SearchDriver, "Unable to found nearby drivers...", Toast.LENGTH_LONG).show()
               // textView?.setText("Unable to found nearby drivers...")

                i++;
                mProgressBar.setProgress(100);

                /* deletePendingReq()*/
                finish()

            }
        }
        cTimer.start()
    }
    private fun getUpdatePayment() {
      //  val progressDialog = ProgressDialog(this)
      //  progressDialog.show()
        val URL =Helper.UPDATE_CITYRIDE_RIDE_PAYMENT_STATUS
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
                      //  progressDialog.hide()
                        try {

                            val status = response.getString("status")
                            if (status.equals("true")) {

                            } else {

                            }
                        }catch (e:Exception){
                            MapUtility.showDialog(e.toString(),this@SearchDriver)

                        }
                    }

                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                  //  progressDialog.hide()
                    Log.d("SendDataE", "error===" + error)
                    // Toast.makeText(this@Current_Driver_Details_List, "Something went wrong!", Toast.LENGTH_LONG).show()


                    MapUtility.showDialog(error.toString(),this@SearchDriver)
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


    private fun getAcceptRide() {
       // val progressDialog = ProgressDialog(this)
      //  progressDialog.show()
        val URL =Helper.ACCEPT_CURRENT_CITYRIDE
        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        json.put("ride_request_id", pref.getReqRideId())
        json.put("ride_id", pref.getride_id())
      //  Log.d("transac",transaction_id.toString())
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
                                val intent = Intent(this@SearchDriver, EmergencyRoutedraweActivity::class.java)
                                intent.putExtra("name", name)
                                intent.putExtra("dl_number", dlnumber)
                                intent.putExtra("veh_number", v_number)
                                intent.putExtra("price", prices)



                                startActivity(intent)

                                /*startActivity(Intent).(this@SearchDriver,EmergencyRoutedraweActivity::class.java))*/


                            } else {

                            }
                        }catch (e:Exception){
                            MapUtility.showDialog(e.toString(),this@SearchDriver)

                        }
                    }

                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                   // progressDialog.hide()
                    Log.d("SendData", "error===" + error)
                    // Toast.makeText(this@Current_Driver_Details_List, "Something went wrong!", Toast.LENGTH_LONG).show()

                    MapUtility.showDialog(error.toString(),this@SearchDriver)
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
        val progressDialog = ProgressDialog(this)
        progressDialog.show()
        val URL =Helper.UPDATE_CITY_RIDE_PAYMENT_STATUS
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
                            getAcceptRide()
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
                    headers.put("Content-Type", "application/json; charset=UTF-8");
                    headers.put("Authorization", "Bearer " + pref.getToken());
                    headers.put("Accept", "application/vnd.api+json");
                    return headers
                }
            }

        queue.add(jsonOblect)

    }
    override fun onBackPressed() {

        val URL = Helper.ride_delete
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

                                val dialog = Dialog(this@SearchDriver)
                                dialog.setCancelable(false)
                                dialog.setContentView(R.layout.serach_driver_dialog)
                                val body = dialog.findViewById(R.id.error) as TextView

                                val yesBtn = dialog.findViewById(R.id.ok) as Button
                                val canBtn = dialog.findViewById(R.id.cancel) as Button
                                yesBtn.setOnClickListener {
                                    pref.setSearchBack("")
                                    startActivity(
                                        Intent(
                                            this@SearchDriver,
                                            CityCabActivity::class.java
                                        )
                                    )
                                    finish()
                                }
                                canBtn.setOnClickListener {
                                    dialog.dismiss()
                                }
                                if (!(this@SearchDriver as Activity).isFinishing) {
                                    dialog.show()
                                }
                                val window: Window? = dialog.getWindow()
                                window?.setLayout(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )

                            } else {

                                Toast.makeText(
                                    this@SearchDriver,
                                    "Something went wrong!",
                                    Toast.LENGTH_LONG
                                ).show()

                            }


                        } catch (e: Exception) {
                            MapUtility.showDialog(e.toString(), this@SearchDriver)

                        }
                    }

                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    progressDialog.hide()
                    Log.d("SendData", "error===" + error)
                    // Toast.makeText(this@Current_Driver_Details_List, "Something went wrong!", Toast.LENGTH_LONG).show()

                    MapUtility.showDialog(error.toString(), this@SearchDriver)
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
}