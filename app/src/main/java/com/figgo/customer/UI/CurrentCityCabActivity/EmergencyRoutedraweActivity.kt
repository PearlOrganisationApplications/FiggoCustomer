package com.figgo.customer.UI.CurrentCityCabActivity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.*
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.figgo.cabs.figgodriver.MapData
import com.figgo.cabs.figgodriver.Service.FireBaseService
import com.figgo.customer.R
import com.figgo.customer.UI.CityCabActivity
import com.figgo.customer.UI.Thankyou_RatingCityCab
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
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.*


class EmergencyRoutedraweActivity : BaseClass(), OnMapReadyCallback{

    lateinit var pref: PrefManager
    private var mMap: GoogleMap? = null
     var destination:MarkerOptions? = null
    private var originLatitude: Double = 28.5021359
    private var originLongitude: Double = 77.4054901
    private var destinationLatitude: Double = 28.5151087
    private var destinationLongitude: Double = 77.3932163
    private var waypointsLatitude : Double = 28.5151087
    private var waypointsLongitude : Double = 77.3932163
    lateinit var geocoder:Geocoder
    private lateinit var driverlocation:LatLng
    private var customerLAT:Double=0.0
    var rideId:String=""
    var  driverimg:ImageView? = null
    lateinit var cTimer : CountDownTimer
    var tv_otp:TextView? = null
    var transaction_id:String=""
    private var customerLON:Double=0.0
    private var count:Int=0
    private var countT:Int=0
    var rideID:Int = 0
    lateinit var timer: CountDownTimer
    lateinit var fusedLocationProviderClient:FusedLocationProviderClient
    lateinit var context:Context
    var dropLocation: LatLng? = null
    var line: Polyline? = null
    private var provider: String? = null
    var waypoints :LatLng? = null
    var customerLoc:LatLng? = null
    val points: MutableList<LatLng> = ArrayList<LatLng>()
    private var locationManager: LocationManager? = null
    var activaimg:ImageView? = null

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

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_routedrawe)
        geocoder= Geocoder(this)
        pref = PrefManager(this@EmergencyRoutedraweActivity)
     //   var iv_bellicon = findViewById<ImageView>(R.id.iv_bellicon)
       // var tv_emrgencybtn = findViewById<TextView>(R.id.tv_emrgencybtn)
      //  var tv_vehiclenumber = findViewById<TextView>(R.id.tv_vehiclenumber)
      //  var iv_drivername = findViewById<TextView>(R.id.iv_drivername)
        driverimg = findViewById<CircleImageView>(R.id.crl_driverimg)
        activaimg = findViewById<ImageView>(R.id.iv_activaimg)
        var vehiclename = findViewById<TextView>(R.id.vehiclename)


        var tv_activanumber = findViewById<TextView>(R.id.tv_activanumber)
        var tv_drivername = findViewById<TextView>(R.id.tv_drivername)
        var tv_dl_number = findViewById<TextView>(R.id.tv_dl_number)
        var iv_call = findViewById<ImageView>(R.id.iv_call)
        tv_otp = findViewById<TextView>(R.id.tv_otp)
        rideId=pref.getride_id()
       var tv_emrgencybtn = findViewById<TextView>(R.id.tv_emrgencybtn)
        val profileName=intent.getStringExtra("name")
        val dl_number=intent.getStringExtra("dl_number")
        val veh_number=intent.getStringExtra("veh_number")
        val price=intent.getStringExtra("price")
        val taxi_image=intent.getStringExtra("taxi_image")
        val driver_image=intent.getStringExtra("driver_image")
        val veh_name=intent.getStringExtra("veh_name")
        transaction_id= intent.getStringExtra("transaction_id")!!


        if(!driver_image.equals("")){
            Picasso.get().load(driver_image).placeholder(R.drawable.girl_img).into(driverimg)
        }

        if(!taxi_image.equals("")){
            Picasso.get().load(taxi_image).placeholder(R.drawable.blueactiva_img).into(activaimg)
        }
        vehiclename.setText(veh_name)
        tv_activanumber.setText(veh_number)
        tv_drivername.setText(profileName)
        tv_dl_number.setText(dl_number)

        getOtp()
        startService(Intent(this, FireBaseService::class.java))
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        val criteria = Criteria()
        provider = locationManager!!.getBestProvider(criteria, false)

//        shareimg()
  //      onBackPress()

       /* iv_bellicon.setOnClickListener {
            startActivity(Intent(this, NotificationBellIconActivity::class.java))
        }*/

        tv_emrgencybtn.setOnClickListener {



            val dialog = Dialog(this@EmergencyRoutedraweActivity)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.serach_driver_dialog)
            // val body = dialog.findViewById(R.id.error) as TextView

            val yesBtn = dialog.findViewById(R.id.ok) as TextView
            val canBtn = dialog.findViewById(R.id.cancel) as TextView
            yesBtn.setOnClickListener {

                dialog.dismiss()
                var geocoder: Geocoder
                val addresses: List<Address>
                geocoder = Geocoder(this@EmergencyRoutedraweActivity, Locale.getDefault())


                var strAdd : String? = null
                try {
                    val addresses = geocoder.getFromLocation(waypointsLatitude!!, waypointsLongitude!!, 1)
                    if (addresses != null) {
                        val returnedAddress = addresses[0]
                        val strReturnedAddress = java.lang.StringBuilder("")
                        for (i in 0..returnedAddress.maxAddressLineIndex) {
                            strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                        }
                        strAdd = strReturnedAddress.toString()
                        Log.w(" Current loction address", strReturnedAddress.toString())
                    } else {
                        Log.w(" Current loction address", "No Address returned!")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.w(" Current loction address",  e.printStackTrace().toString())
                }
                val URL = Helper.EMERGENCY
                val queue = Volley.newRequestQueue(this)
                val json = JSONObject()
                json.put("ride_id", pref.getride_id())
                json.put("location_name", strAdd)
                json.put("lat", waypointsLatitude)
                json.put("lng", waypointsLongitude)

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

                                        /*startActivity(Intent).(this@SearchDriver,EmergencyRoutedraweActivity::class.java))*/

                                        Toast.makeText(this@EmergencyRoutedraweActivity, "Figgo Safety Response Team will contact you immediately.. !", Toast.LENGTH_LONG).show()

                                    } else {
                                        Toast.makeText(this@EmergencyRoutedraweActivity, "Something went wrong!", Toast.LENGTH_LONG).show()
                                    }
                                }catch (e:Exception){
                                    MapUtility.showDialog(e.toString(),this@EmergencyRoutedraweActivity)

                                }
                            }

                        }
                    }, object : Response.ErrorListener {
                        override fun onErrorResponse(error: VolleyError?) {
                            // progressDialog.hide()
                            Log.d("SendData", "error===" + error)
                            // Toast.makeText(this@Current_Driver_Details_List, "Something went wrong!", Toast.LENGTH_LONG).show()

                            MapUtility.showDialog(error.toString(),this@EmergencyRoutedraweActivity)
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
            canBtn.setOnClickListener {
                dialog.dismiss()
            }
            if (!(this@EmergencyRoutedraweActivity as Activity).isFinishing) {
                dialog.show()
            }
            val window: Window? = dialog.getWindow()
            window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)



            //startActivity(Intent(this,DriveRatingActivity::class.java))

        }

        iv_call.setOnClickListener {
            
            var intent_call = Intent(Intent.ACTION_DIAL)
            intent_call.data = Uri.parse("tel:"+"+919715597855")
            startActivity(intent_call)
        }





        val locationRequest=LocationRequest.CREATOR



        // Fetching API_KEY which we wrapped
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
       //            val value = ai.metaData["api_key"]
        val apiKey = getString(R.string.api_key)

        // Initializing the Places API with the help of our API_KEY
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }


        // Map Fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setCurrentLatLon()
        destinationLatitude = pref.getToLatMC().toDouble()
        destinationLongitude = pref.getToLngMC().toDouble()

            mapFragment.getMapAsync {
                mMap = it
                val originLocation = LatLng(originLatitude, originLongitude)
                mMap!!.addMarker(MarkerOptions().position(originLocation))
                val destinationLocation = LatLng(destinationLatitude, destinationLongitude)
                mMap!!.addMarker(MarkerOptions().position(destinationLocation))
                val urll = getDirectionURL(originLocation, destinationLocation, apiKey)
               // GetDirection(urll).execute()

                timer = object: CountDownTimer(5000000000000000, 500) {
                    override fun onTick(millisUntilFinished: Long) {
                       // val custData = customerRef.child("loc")

                        setCurrentLatLon()


                        Log.d("rideId",rideId)



                    }
                    override fun onFinish() {

                    }
                }
                (timer as CountDownTimer).start()

              //  mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(waypoints!!, 14F))


            }


    }
/*
    override fun onLocationChanged(location: Location) {
        val curLat: Double = location.getLatitude() //current latitude
        val curLong: Double = location.getLongitude() //current longitude

        mMap?.clear()

        pickupLocation = LatLng(curLat, curLong)
        dropLocation = LatLng(from_lat!!.toDouble() , from_lng!!.toDouble())
        val height = 100
        val width = 100
        val bitmapdraw = resources.getDrawable(R.drawable.carmove_img) as BitmapDrawable
        val b = bitmapdraw.bitmap
        val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
        mMap?.addMarker(MarkerOptions().position(pickupLocation!!).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Current Location"))

        val bitmapdraw2 = resources.getDrawable(R.drawable.drop_location) as BitmapDrawable
        val b2 = bitmapdraw2.bitmap
        val smallMarker2 = Bitmap.createScaledBitmap(b2, width, height, false)
        mMap?.addMarker(MarkerOptions().position(dropLocation!!).icon(BitmapDescriptorFactory.fromBitmap(smallMarker2)).title("dropoff"))

        val source = "" + curLat + "," + curLong
        val destination = "" + from_lat!!.toDouble() + "," + from_lng!!.toDouble()
     //   Log.e("Origin ", "$source\n Destination $destination")
        if (polyline.toString().equals("null") || polyline.toString().equals("")){

        }else {
            polyline!!.remove()
        }
        GetDirection().execute(source, destination)

    }
*/



    private fun isGooglePlayServicesAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (ConnectionResult.SUCCESS == status) return true else {
            if (googleApiAvailability.isUserResolvableError(status)) Toast.makeText(this,
                "Please Install google play services to use this application", Toast.LENGTH_LONG).show()
        }
        return false
    }



/*    private fun getDirectionURL(origin:LatLng, dest:LatLng, secret: String) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=driving" +
                "&key=$secret"
    }*/




    override fun onMapReady(p0: GoogleMap) {
        mMap =p0
    }

    private fun getDirectionURL(origin:LatLng, dest:LatLng, secret: String) : String{

        waypoints = LatLng(waypointsLatitude.toDouble() , waypointsLongitude.toDouble())
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${waypointsLatitude},${waypointsLongitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=driving" +
                "&key=$secret"
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>(){
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body?.string()
            Log.d("SEND DATA"," data ===="+ data )

            val result =  ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data, MapData::class.java)
                val path =  ArrayList<LatLng>()
                for (i in 0 until respObj.routes[0].legs[0].steps.size){
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.GREEN)
                lineoption.geodesic(true)
                /* lineoption.startCap()
                 if (result.lastIndex.equals(LatLng(destinationLatitude, destinationLongitude))){

                 }*/
            }
            mMap?.addPolyline(lineoption)
        }
    }
    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
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
            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()





    }
    @SuppressLint("MissingPermission")
    fun setCurrentLatLon(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

            override fun isCancellationRequested() = false
        })
            .addOnSuccessListener { location: android.location.Location? ->
                if (location == null)
                    Toast.makeText(this, "Cannot get location.", Toast.LENGTH_SHORT).show()
                else {
                    waypointsLatitude = location.latitude
                    //pref.setlatitude(originLatitude.toFloat())
                    waypointsLongitude = location.longitude

                    originLatitude = pref.getDriverlat().toDouble()
                    originLongitude = pref.getDriverlng().toDouble()
                    liveRouting()
                    //pref.setlongitude(originLongitude.toFloat())
                    //Toast.makeText(this,"Lat :"+lat+"\nLong: "+long, Toast.LENGTH_SHORT).show()
                }
            }


    }

    fun liveRouting(){

        mMap?.clear()


        dropLocation  = LatLng(destinationLatitude, destinationLongitude)
        customerLoc = LatLng(waypointsLatitude,waypointsLongitude)
        //customerLocation = LatLng(cust_lat)
        val height = 80
        val width = 80
        var markers1 : Marker? = null
if(destinationLatitude>=0.000&&destinationLongitude>=0.00) {
    driverlocation = LatLng(originLatitude, originLongitude)
    val bitmapdraw = resources.getDrawable(R.drawable.driver) as BitmapDrawable
    val b = bitmapdraw.bitmap
    val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
     markers1 = mMap?.addMarker(
        MarkerOptions().position(driverlocation!!)
            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
            .title("Driver Location")
    )
}
        val bitmapdraw2 = resources.getDrawable(R.drawable.ic_arrival) as BitmapDrawable
        val b2 = bitmapdraw2.bitmap
        val smallMarker2 = Bitmap.createScaledBitmap(b2, width, height, false)
        val markers2 = mMap?.addMarker(
            MarkerOptions().position(dropLocation!!)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker2))
                .title("Destination")
        )

        val bitmapdraw3 = resources.getDrawable(R.drawable.ic_custmarker) as BitmapDrawable
        val b3 = bitmapdraw3.bitmap
        val smallMarker3 = Bitmap.createScaledBitmap(b3, width, height, false)

      val markers3 =  mMap?.addMarker(
            MarkerOptions().position(customerLoc!!)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker3))
                .title("My Location")
        )
        if(count == 1) {

            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(customerLoc!!, 11F))

        }
        count++




      /*  val builder = LatLngBounds.Builder()

//the include method will calculate the min and max bound.

//the include method will calculate the min and max bound.
        builder.include(markers1!!.getPosition())
        builder.include(markers2!!.getPosition())
        builder.include(markers3!!.getPosition())


        //val bounds = builder.build()

        val widths = resources.displayMetrics.widthPixels
       // val heights = resources.displayMetrics.heightPixels
        //val padding = (heights * 0.20).toInt() // offset from edges of the map 10% of screen


        val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, 0)

        mMap!!.animateCamera(cu)*/
        val source = "$originLatitude,$originLongitude"
        val destination = "$destinationLatitude,$destinationLongitude"
        Log.e("Origin ", "$source\n Destination $destination")
        //GetDirection().execute(source, destination)
        var url:String=getDirectionURL(driverlocation!!, dropLocation!!,"AIzaSyCbd3JqvfSx0p74kYfhRTXE7LZghirSDoU")
        GetDirection(url).execute()
        Handler().postDelayed({
            //do something
        }, 5000)
    }
   /* fun getZoomLevel(circle: Circle?): Int {
        if (circle != null) {
            val radius = circle.radius
            val scale = radius / 500
            zoomLevel = (16 - Math.log(scale) / Math.log(2.0)).toInt()
        }
        return zoomLevel
    }*/
   private fun getOtp() {
       val progressDialog = ProgressDialog(this)
       progressDialog.show()
       val URL =Helper.UPDATE_CITY_RIDE_PAYMENT_STATUS
       val queue = Volley.newRequestQueue(this)
       val json = JSONObject()
       json.put("transaction_id", transaction_id)
       json.put("payment_type", "card")
       json.put("ride_id", pref.getride_id())
       json.put("ride_request_id", pref.getReqRideId())
     //  Log.d("transac",transaction_id.toString())
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

                           tv_otp?.setText(otp.toString())
                        startTimer()
                       }catch (e:Exception){
                           MapUtility.showDialog(e.toString(),this@EmergencyRoutedraweActivity)

                       }
                   }

               }
           }, object : Response.ErrorListener {
               override fun onErrorResponse(error: VolleyError?) {
                   progressDialog.hide()
                   Log.d("SendData", "error===" + error)
                   // Toast.makeText(this@Current_Driver_Details_List, "Something went wrong!", Toast.LENGTH_LONG).show()

                   MapUtility.showDialog(error.toString(),this@EmergencyRoutedraweActivity)
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



    private fun getRideStatus() {
        // val progressDialog = ProgressDialog(this)
        //  progressDialog.show()
        val URL =Helper.Ride_Status
        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
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


                                val ride_status = response.getString("ride_status")


                                if (ride_status.equals("completed")){
                                    if (::cTimer.isInitialized) {
                                        cTimer.cancel()
                                    }
                                    if (::timer.isInitialized) {
                                        timer.cancel()
                                    }

                                    val intent = Intent(this@EmergencyRoutedraweActivity, Thankyou_RatingCityCab::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            } else {

                            }
                        }catch (e:Exception){
                            MapUtility.showDialog(e.toString(),this@EmergencyRoutedraweActivity)

                        }
                    }

                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    // progressDialog.hide()
                    Log.d("SendData", "error===" + error)
                    // Toast.makeText(this@Current_Driver_Details_List, "Something went wrong!", Toast.LENGTH_LONG).show()

                    MapUtility.showDialog(error.toString(),this@EmergencyRoutedraweActivity)
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

        cTimer = object : CountDownTimer(500000000000, 1000) {
            override fun onTick(millisUntilFinished: Long) {//300000
               // txtTimer?.setText("seconds remaining: " +""+ (millisUntilFinished / 1000).toString())


                if (countT % 10 ==  0) {
                    getRideStatus()

                    // Toast.makeText(this@SearchDriver, "fetching driver...", Toast.LENGTH_LONG).show()

                }
                countT++
            }

            override fun onFinish() {
              startTimer()

            }
        }
        cTimer.start()
    }
    override fun onBackPressed() {

        Toast.makeText(applicationContext, "Unable to back your money already deducted", Toast.LENGTH_LONG).show()
    }

   
}

