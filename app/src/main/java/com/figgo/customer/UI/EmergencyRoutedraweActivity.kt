package com.figgo.customer.UI

import android.annotation.SuppressLint
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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.figgo.cabs.figgodriver.MapData
import com.figgo.cabs.figgodriver.Service.FireBaseService
import com.figgo.customer.R
import com.figgo.customer.pearlLib.BaseClass
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
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request


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
    private var customerLON:Double=0.0
    private var count:Int=0
    var rideID:Int = 0
    private var timer: CountDownTimer?=null
    lateinit var fusedLocationProviderClient:FusedLocationProviderClient
    lateinit var context:Context
    var dropLocation: LatLng? = null
    var line: Polyline? = null
    private var provider: String? = null
    var waypoints = ""
    var customerLoc:LatLng? = null
    private var locationManager: LocationManager? = null
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
        var iv_bellicon = findViewById<ImageView>(R.id.iv_bellicon)
        var tv_emrgencybtn = findViewById<TextView>(R.id.tv_emrgencybtn)
        var tv_vehiclenumber = findViewById<TextView>(R.id.tv_vehiclenumber)
        var iv_drivername = findViewById<TextView>(R.id.iv_drivername)

       rideId=pref.getride_id()
        var tv_activanumber = findViewById<TextView>(R.id.tv_activanumber)
        var tv_drivername = findViewById<TextView>(R.id.tv_drivername)
        var tv_dl_number = findViewById<TextView>(R.id.tv_dl_number)
        var iv_call = findViewById<ImageView>(R.id.iv_call)

     //   var tv_emrgencybtn = findViewById<TextView>(R.id.tv_emrgencybtn)
        val profileName=intent.getStringExtra("name")
        val dl_number=intent.getStringExtra("dl_number")
        val veh_number=intent.getStringExtra("veh_number")
        val price=intent.getStringExtra("price")


        tv_activanumber.setText(veh_number)
        tv_drivername.setText(profileName)
        tv_dl_number.setText(dl_number)

        startService(Intent(this, FireBaseService::class.java))
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        val criteria = Criteria()
        provider = locationManager!!.getBestProvider(criteria, false)

        shareimg()
        onBackPress()

        iv_bellicon.setOnClickListener {
            startActivity(Intent(this, NotificationBellIconActivity::class.java))
        }

        tv_emrgencybtn.setOnClickListener {
            startActivity(Intent(this,DriveRatingActivity::class.java))
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

                timer = object: CountDownTimer(1000000, 500) {
                    override fun onTick(millisUntilFinished: Long) {
                       // val custData = customerRef.child("loc")

                        setCurrentLatLon()


                        Log.d("rideId",rideId)



                    }
                    override fun onFinish() {

                    }
                }
                (timer as CountDownTimer).start()

                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(originLocation, 14F))


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
        waypoints = "waypoints="
        waypoints += waypointsLatitude.toString() + "," + waypointsLongitude.toString() + "|";
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
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
if(destinationLatitude>=0.000&&destinationLongitude>=0.00) {
    driverlocation = LatLng(originLatitude, originLongitude)
    val bitmapdraw = resources.getDrawable(R.drawable.driver) as BitmapDrawable
    val b = bitmapdraw.bitmap
    val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
    mMap?.addMarker(
        MarkerOptions().position(driverlocation!!)
            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
            .title("Driver Location")
    )
}
        val bitmapdraw2 = resources.getDrawable(R.drawable.ic_arrival) as BitmapDrawable
        val b2 = bitmapdraw2.bitmap
        val smallMarker2 = Bitmap.createScaledBitmap(b2, width, height, false)
        mMap?.addMarker(
            MarkerOptions().position(dropLocation!!)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker2))
                .title("Destination")
        )

        val bitmapdraw3 = resources.getDrawable(R.drawable.ic_custmarker) as BitmapDrawable
        val b3 = bitmapdraw3.bitmap
        val smallMarker3 = Bitmap.createScaledBitmap(b3, width, height, false)

        mMap?.addMarker(
            MarkerOptions().position(customerLoc!!)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker3))
                .title("My Location")
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


}

