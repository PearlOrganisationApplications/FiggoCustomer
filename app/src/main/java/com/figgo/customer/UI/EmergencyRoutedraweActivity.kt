package com.figgo.customer.UI

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.*
import android.location.LocationListener
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.figgo.cabs.figgodriver.MapData
import com.figgo.cabs.figgodriver.Service.FireBaseService
import com.figgo.customer.R
import com.figgo.customer.Util.MapUtility
import com.figgo.customer.pearlLib.BaseClass
import com.figgo.customer.pearlLib.Helper
import com.figgo.customer.pearlLib.PrefManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class EmergencyRoutedraweActivity : BaseClass(), OnMapReadyCallback {

    lateinit var pref: PrefManager
    private var mMap: GoogleMap? = null
    var destination: MarkerOptions? = null
    private var originLatitude: Double = 28.5021359
    private var originLongitude: Double = 77.4054901
    private var destinationLatitude: Double = 28.5151087
    private var destinationLongitude: Double = 77.3932163
    private var waypointsLatitude: Double = 28.5151087
    private var waypointsLongitude: Double = 77.3932163
    private val pERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    var currentLocation: LatLng = LatLng(waypointsLatitude, waypointsLongitude)
    lateinit var textView:TextView
    private val REQUEST_CHECK_SETTINGS: Int=101
    var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=101
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationRequest: LocationRequest
    private val ADDRESS_PICKER_REQUEST = 1
    private val requestcodes = 2
    private val permissionId = 2
    private var hasGps = false
    private var hasNetwork = false
    private var locationByGps: Location? = null
    private var locationByNetwork: Location? = null
    private var lastKnownLocationByGps: Location? = null

    lateinit var geocoder: Geocoder
    private lateinit var driverlocation: LatLng
    private var customerLAT: Double = 0.0
    var rideId: String = ""
    private var customerLON: Double = 0.0
    private var count: Int = 0
    var rideID: Int = 0
    private var timer: CountDownTimer? = null
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var context: Context
    var dropLocation: LatLng? = null
    var line: Polyline? = null
    private var provider: String? = null
    var waypoints = ""
    var customerLoc: LatLng? = null
    var locationname: String? = ""
    private var locationManager: LocationManager? = null
    var to_lat :Double ?= 0.0
    var from_lat :Double ?= 0.0
    var to_lng :Double ?= 0.0
    var from_lng :Double ?= 0.0

    var AUTOCOMPLETE_REQUEST_CODE = -1
    var press : String ?= ""


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
        geocoder = Geocoder(this)
        pref = PrefManager(this@EmergencyRoutedraweActivity)
        var iv_bellicon = findViewById<ImageView>(R.id.iv_bellicon)
        var tv_livelocemeregncy = findViewById<TextView>(R.id.tv_livelocemeregncy)
        var tv_vehiclenumber = findViewById<TextView>(R.id.tv_vehiclenumber)
        var iv_drivername = findViewById<TextView>(R.id.iv_drivername)

        rideId = pref.getride_id()
        var tv_activanumber = findViewById<TextView>(R.id.tv_activanumber)
        var tv_drivername = findViewById<TextView>(R.id.tv_drivername)
        var tv_dl_number = findViewById<TextView>(R.id.tv_dl_number)
        var iv_call = findViewById<ImageView>(R.id.iv_call)

        //   var tv_emrgencybtn = findViewById<TextView>(R.id.tv_emrgencybtn)
        val profileName = intent.getStringExtra("name")
        val dl_number = intent.getStringExtra("dl_number")
        val veh_number = intent.getStringExtra("veh_number")
        val price = intent.getStringExtra("price")
        shareimg()
        onBackPress()


        tv_activanumber.setText(veh_number)
        tv_drivername.setText(profileName)
        tv_dl_number.setText(dl_number)



        startService(Intent(this, FireBaseService::class.java))
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        val criteria = Criteria()
        provider = locationManager!!.getBestProvider(criteria, false)



        iv_bellicon.setOnClickListener {
            startActivity(Intent(this, NotificationBellIconActivity::class.java))
        }



        iv_call.setOnClickListener {

            var intent_call = Intent(Intent.ACTION_DIAL)
            intent_call.data = Uri.parse("tel:" + "+919715597855")
            startActivity(intent_call)
        }




        tv_livelocemeregncy.setOnClickListener {
            var geocoder: Geocoder
            val addresses: List<Address>
            geocoder = Geocoder(this, Locale.getDefault())

            var strAdd : String? = null
            try {
                val addresses = geocoder.getFromLocation(to_lat!!, to_lng!!, 1)
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
            val URL = Helper.emergency
            Log.d("SendData", "URL===" + URL)
            val progressDialog = ProgressDialog(this)
            progressDialog.show()
            val queue = Volley.newRequestQueue(this)
            val json = JSONObject()
            json.put("location_name",strAdd)
            json.put("lat", waypointsLatitude)
            json.put("lng", waypointsLongitude)
            json.put("ride_id", pref.getride_id())
            Log.d("SendData", "json===" + json)
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

                                    Toast.makeText(this@EmergencyRoutedraweActivity, "result successful", Toast.LENGTH_LONG).show()
                                } else {

                                    Toast.makeText(this@EmergencyRoutedraweActivity, "Something went wrong111!", Toast.LENGTH_LONG).show()

                                }


                            } catch (e: Exception) {
                                MapUtility.showDialog(e.toString(), this@EmergencyRoutedraweActivity)

                            }
                        }

                    }
                }, object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {
                        progressDialog.hide()
                        Log.d("SendData", "error===" + error)
                        // Toast.makeText(this@Current_Driver_Details_List, "Something went wrong!", Toast.LENGTH_LONG).show()

                        MapUtility.showDialog(error.toString(), this@EmergencyRoutedraweActivity)
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


        val locationRequest = LocationRequest.CREATOR
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val apiKey = getString(R.string.api_key)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        val database = FirebaseDatabase.getInstance()
        val customerRef = database.getReference("$rideID customer")
        val driverRef = database.getReference("$rideID driver")


        // Map Fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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

            timer = object : CountDownTimer(1000000, 500) {
                override fun onTick(millisUntilFinished: Long) {
                    // val custData = customerRef.child("loc")

                    setCurrentLatLon()

                    liveRouting()
                }

                override fun onFinish() {

                }
            }
            (timer as CountDownTimer).start()

            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(originLocation, 14F))


        }


    }


    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

    }


    private fun getDirectionURL(origin:LatLng, dest:LatLng, secret: String) : String{
        waypoints = "waypoints="
        waypoints += waypointsLatitude.toString() + "," + waypointsLongitude.toString() + "|";
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=driving" +
                "&key=$secret" +
                "&"+waypoints
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
                    originLatitude = pref.getDriverlng().toDouble()
                    //pref.setlongitude(originLongitude.toFloat())
                    //Toast.makeText(this,"Lat :"+lat+"\nLong: "+long, Toast.LENGTH_SHORT).show()
                }
            }


    }

    fun liveRouting(){
        mMap?.clear()
        driverlocation   = LatLng(originLatitude, originLongitude)
        dropLocation  = LatLng(destinationLatitude, destinationLongitude)
        customerLoc = LatLng(waypointsLatitude,waypointsLongitude)
        //customerLocation = LatLng(cust_lat)
        val height = 80
        val width = 80
        val bitmapdraw = resources.getDrawable(R.drawable.ic_pinpoint) as BitmapDrawable
        val b = bitmapdraw.bitmap
        val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
        mMap?.addMarker(MarkerOptions().position(driverlocation!!).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Current Location"))
        val bitmapdraw2 = resources.getDrawable(R.drawable.ic_arrival) as BitmapDrawable
        val b2 = bitmapdraw2.bitmap
        val smallMarker2 = Bitmap.createScaledBitmap(b2, width, height, false)
        mMap?.addMarker(
            MarkerOptions().position(dropLocation!!)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker2))
                .title("drop-off")
        )

        val bitmapdraw3 = resources.getDrawable(R.drawable.driver) as BitmapDrawable
        val b3 = bitmapdraw3.bitmap
        val smallMarker3 = Bitmap.createScaledBitmap(b3, width, height, false)

        mMap?.addMarker(
            MarkerOptions().position(customerLoc!!)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker3))
                .title("drop-off")
        )


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


    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION), permissionId)

    }
    private fun isLocationPermissionGranted(): Boolean {
        return if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)


            false
        } else {
            true
        }
    }

    fun grantLocPer() {
        if (isLocationPermissionGranted()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    checkLocationService()

                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                    );
                }
            } else {
                checkLocationService()
            }

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)

        }
    }


    fun checkLocationService() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2 * 1000);
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        // builder.setAlwaysShow(true);
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this){it->
            it.locationSettingsStates;

            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            hasGps = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
//------------------------------------------------------//
            hasNetwork = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)




        }

        task.addOnFailureListener(this) { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    grantLocPer()
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult (this, REQUEST_CHECK_SETTINGS)

                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }

            }
        }
    }







    @SuppressLint("SuspiciousIndentation")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            101-> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) ===
                                PackageManager.PERMISSION_GRANTED)) {


                        grantLocPer()

                    }else{
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
                    }
                } else {
                    if ((ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) ===
                                PackageManager.PERMISSION_GRANTED)) {




                    }else{
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
                    }


                }
                return
            }
        }


    }

    val gpsLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            locationByGps = location
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}

    }
    //------------------------------------------------------//
    val networkLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            locationByNetwork= location
            // locationByNetwork= location
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }


}



