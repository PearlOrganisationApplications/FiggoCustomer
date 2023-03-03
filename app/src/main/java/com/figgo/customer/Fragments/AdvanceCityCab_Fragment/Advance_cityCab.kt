package com.figgo.customer.Fragments.AdvanceCityCab_Fragment
//Neeraj

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.format.Time
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.figgo.customer.Adapter.AdvanceCityDataAdapter
import com.figgo.customer.Model.AdvanceCityCabModel
import com.figgo.customer.R
import com.figgo.customer.UI.AdavanceCityCabActivity.CabDetailsActivity
import com.figgo.customer.UI.AdavanceCityCabActivity.LocationPickerActivity
import com.figgo.customer.UI.IOnBackPressed
import com.figgo.customer.Util.MapUtility
import com.figgo.customer.databinding.ActivityMainBinding
import com.figgo.customer.databinding.FragmentAdvanceCityCabBinding
import com.figgo.customer.pearlLib.Helper
import com.figgo.customer.pearlLib.PrefManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class Advance_cityCab : Fragment(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerDragListener,
    GoogleMap.OnCameraMoveStartedListener,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraMoveCanceledListener,
    GoogleMap.OnCameraIdleListener, IOnBackPressed {
    private val REQUEST_CHECK_SETTINGS: Int=101;
    private lateinit var mMap: GoogleMap
    var marker: Marker? = null
    var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=101;
    private lateinit var fusedLocationClient: FusedLocationProviderClient;
    private lateinit var lastLocation: Location;
    private lateinit var locationRequest: LocationRequest;

    lateinit var binding: FragmentAdvanceCityCabBinding
    lateinit var advanceCityAdapter: AdvanceCityDataAdapter
    var cablist=ArrayList<AdvanceCityCabModel>()
    var manualLoc: TextView? = null
    var liveLoc: TextView? = null
    var AUTOCOMPLETE_REQUEST_CODE = -1
    private val ADDRESS_PICKER_REQUEST = 1
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
     var to_lat :Double ?= 0.0
    var from_lat :Double ?= 0.0
    var to_lng :Double ?= 0.0
    var from_lng :Double ?= 0.0
    lateinit var pref: PrefManager
    var selects : String ?= ""
    lateinit var ll_location : LinearLayout
    lateinit var ll_choose_vehicle : LinearLayout
    var  myLocation : LatLng ?= null
    lateinit var locationManager: LocationManager
    private val requestcodes = 2
    private var hasGps = false
    private var hasNetwork = false
    private var locationByGps: Location? = null
    private var locationByNetwork: Location? = null
    private var lastKnownLocationByGps: Location? = null
    private lateinit var googleApiClient: GoogleApiClient
    var press : String ?= "";
    var datetext: TextView? = null
    var timetext: TextView? = null
    var mapFragment: Fragment? = null
    private var onResu: String? = ""
    lateinit var cTimer : CountDownTimer


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

       binding=DataBindingUtil.inflate(inflater,R.layout.fragment_advance_city_cab, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pref = PrefManager(requireActivity())
       /* if (pref.getTypeC().equals("1") || pref.getTypeC().equals("2")){

            Navigation.findNavController(view).navigate(R.id.current)

        }*/

        var calenderimg = view.findViewById<LinearLayout>(R.id.calenderimg)
         datetext = view.findViewById<TextView>(R.id.datetext)
        var watchimg = view?.findViewById<LinearLayout>(R.id.watchimg)
         timetext = view?.findViewById<TextView>(R.id.timetext)
         ll_location = view?.findViewById<LinearLayout>(R.id.ll_location)!!
         ll_choose_vehicle = view?.findViewById<LinearLayout>(R.id.ll_choose_vehicle)!!
        manualLoc = view?.findViewById<TextView>(R.id.loc_manual)
         liveLoc = view?.findViewById<TextView>(R.id.live_loc)
        var locLinear = view?.findViewById<LinearLayout>(R.id.linear_loc)
        var submit = view?.findViewById<Button>(R.id.submit)
        var next = view?.findViewById<Button>(R.id.next)
        var destLinear = view?.findViewById<LinearLayout>(R.id.linear_des)
        var advance_li = view?.findViewById<LinearLayout>(R.id.adLinear)
        var set = view?.findViewById<TextView>(R.id.img_marker)

        ll_choose_vehicle?.isVisible = false
        pref.setCount("location")
        pref.setBookingNo("")
        pref.setOtp("")
        pref.setRideId("")
        pref.setVehicleId("")
        pref.setTypeC("")
        pref.setToLatLC("")
        pref.setToLngLC("")
        pref.setToLatMC("")
        pref.setToLngMC("")
        startTimer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
          val currentDate = LocalDateTime.now().format(formatter)
            datetext?.setText(currentDate)
        }else{
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date: String = df.format(Calendar.getInstance().time)
            datetext?.setText(date)
        }

        val apiKey = getString(R.string.api_key)

        if (!Places.isInitialized()) {
            Places.initialize(requireActivity(), apiKey)
        }

        set?.setOnClickListener {
            mMap!!.clear()
            advance_li?.isVisible = true
           // map_li?.isVisible = false

        }








        calenderimg.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = context?.let { it1 ->
                DatePickerDialog(
                    it1,
                    { view, year, monthOfYear, dayOfMonth ->
                        val dat : String
                        val datApi : String
                        if (monthOfYear < 9){
                           // dat = (dayOfMonth.toString() + "-0" + (monthOfYear + 1) + "-" + year.toString())
                            dat = (year.toString() + "-0" + (monthOfYear + 1) + "-" + dayOfMonth.toString())
                        }else {
                             //dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year.toString())
                            dat = (year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString())
                        }

                        datetext?.setText(dat)
                    },
                    year,
                    month,
                    day
                )
            }

            if (datePickerDialog != null) {
                datePickerDialog.show()
            }

        }
        watchimg?.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                val am_pm = if (hour < 12) "AM" else "PM"
               var selectedHour : Int
                if (hour > 12){
                    selectedHour = hour - 12
                }else{
                    if(hour == 0){
                        selectedHour = 12

                    }else {
                        selectedHour = hour
                    }
                }
                cal.set(Calendar.HOUR_OF_DAY, selectedHour)
                cal.set(Calendar.MINUTE, minute)
                if (timetext != null) {
                    if (::cTimer.isInitialized) {
                        cTimer.cancel()
                    }
                    timetext?.text = SimpleDateFormat("HH:mm:ss").format(cal.time)+""+am_pm+""
                }
            }
            TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        }

        manualLoc?.setOnClickListener {
            press = "manual"
            val field = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS,Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field)
                .build(requireActivity())
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        liveLoc?.setOnClickListener {

            press = "live"
            val field = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS,Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field)
                .build(requireActivity())
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        submit?.setOnClickListener {



            if (liveLoc?.text.toString().equals("")){
                Toast.makeText(requireActivity(), "Please select Start Address", Toast.LENGTH_LONG).show()
            }else if (manualLoc?.text.toString().equals("")){
                Toast.makeText(requireActivity(), "Please select Destination Address", Toast.LENGTH_LONG).show()

            }else {

                val dialog = Dialog(requireActivity())
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.confirm_time_dialog)


                val yesBtn = dialog.findViewById(R.id.ok) as TextView
                val canBtn = dialog.findViewById(R.id.cancel) as TextView
                val time = dialog.findViewById(R.id.time) as TextView
                time.setText("Are you sure with this date: "+datetext?.text.toString()+"\n and time: "+timetext?.text.toString())
                yesBtn.setOnClickListener {
                    dialog.dismiss()
                    submitform()

                }
                canBtn.setOnClickListener {
                    dialog.dismiss()
                }
                if (!(requireActivity() as Activity).isFinishing) {
                    dialog.show()
                }
                val window: Window? = dialog.getWindow()
                window?.setLayout(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )





            }

        }

        next?.setOnClickListener {
                startActivity(Intent(requireActivity(), CabDetailsActivity::class.java))
        }


        locLinear?.setOnClickListener {
            val internet :Boolean = isOnline(requireActivity())
            if(internet == true) {
            selects = "start"
            pref.setType("1")
            val i = Intent(requireActivity(), LocationPickerActivity::class.java)
            startActivityForResult(i, ADDRESS_PICKER_REQUEST)
            }else{
                Toast.makeText(requireActivity(), "Please turn on internet", Toast.LENGTH_LONG).show()

            }

        }

        destLinear?.setOnClickListener {

            val internet :Boolean = isOnline(requireActivity())
            if(internet == true) {
                pref.setType("2")
                selects = "dest"
                val i = Intent(requireActivity(), LocationPickerActivity::class.java)
                startActivityForResult(i, ADDRESS_PICKER_REQUEST)

            }else{
                Toast.makeText(requireActivity(), "Please turn on internet", Toast.LENGTH_LONG).show()

            }
        }

       // Initialize Places.

    }

    fun startTimer() {

        cTimer = object : CountDownTimer(3000000, 1000) {
            override fun onTick(millisUntilFinished: Long) {//300000
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                     val currentDate = LocalDateTime.now().format(formatter)

                    val calendar = Calendar.getInstance()
                    val hour = calendar[Calendar.HOUR_OF_DAY]
                    val minutes = calendar[Calendar.MINUTE]
                    val seconds = calendar[Calendar.SECOND]
                    val am_pm = if (hour < 12) "AM" else "PM"
                    var selectedHour : String
                    var selectedMin : String
                    var selectedSec : String

                    if (hour > 12){
                        selectedHour = (hour - 12).toString()
                    }else{
                        if(hour == 0){
                            selectedHour = 12.toString()

                        }else {
                            if (hour < 10){
                                selectedHour = "0"+hour.toString()
                            }else {
                                selectedHour = hour.toString()
                            }
                        }
                    }
                    if (minutes < 10){
                        selectedMin = "0"+minutes.toString()
                    }else{
                        selectedMin = minutes.toString()
                    }
                    if (seconds < 10){
                        selectedSec = "0"+seconds.toString()
                    }else{
                        selectedSec = seconds.toString()

                    }
                    val time = selectedHour+":"+selectedMin+":"+selectedSec+""+am_pm+""

                    timetext?.setText(time)
                } else {
                    var today = Time(Time.getCurrentTimezone());
                    today.setToNow()
                    var selectedHour : String
                    var selectedMin : String
                    var selectedSec : String



                }

            }

            override fun onFinish() {


            }
        }
        cTimer.start()
    }


    private fun submitform() {

        val progressDialog = ProgressDialog(requireActivity())
        progressDialog.show()
        val URL = Helper.create_ride
        Log.d("url",URL)
        val queue = Volley.newRequestQueue(requireContext())
        val json = JSONObject()
        json.put("date", datetext?.text.toString())
        json.put("time", timetext?.text.toString())
        json.put("to_lat", to_lat)
        json.put("to_lng", to_lng)
        json.put("from_lat", from_lat)
        json.put("from_lng", from_lng)
        json.put("type", "advance_booking")
        json.put("to_location_name", liveLoc?.text.toString())
        json.put("from_location_name", manualLoc?.text.toString())

        Log.d("requestdata",""+json)


        val jsonOblect: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URL, json, object :
                Response.Listener<JSONObject?>               {
                @SuppressLint("SuspiciousIndentation")
                override fun onResponse(response: JSONObject?) {
                    Log.d("SendData", "response===" + response)
                    if (response != null) {
                        progressDialog.hide()
                        try {



                            ll_location?.isVisible = false
                            ll_choose_vehicle?.isVisible = true
                            pref.setCount("vehicle")

                            val size = response.getJSONObject("data").getJSONArray("vehicle_types")
                                .length()
                            val rideId = response.getJSONObject("data").getString("ride_id")

                            for (p2 in 0 until size) {

                                val name =
                                    response.getJSONObject("data").getJSONArray("vehicle_types")
                                        .getJSONObject(p2).getString("name")
                                val image =
                                    response.getJSONObject("data").getJSONArray("vehicle_types")
                                        .getJSONObject(p2).getString("full_image")
                                val vehicle_id =
                                    response.getJSONObject("data").getJSONArray("vehicle_types")
                                        .getJSONObject(p2).getString("id")
                                val min =
                                    response.getJSONObject("data").getJSONArray("vehicle_types")
                                        .getJSONObject(p2).getString("min_price")
                                val max =
                                    response.getJSONObject("data").getJSONArray("vehicle_types")
                                        .getJSONObject(p2).getString("max_price")

                                cablist.add(
                                    AdvanceCityCabModel(
                                        name,
                                        image,
                                        rideId,
                                        vehicle_id,
                                        min,
                                        max
                                    )
                                )
                            }

                            advanceCityAdapter = AdvanceCityDataAdapter(requireActivity(), cablist)
                            binding.recylerCabList.layoutManager = GridLayoutManager(context, 3)
                            binding.recylerCabList.adapter = advanceCityAdapter
                            binding.recylerCabList.layoutManager = GridLayoutManager(context, 3)
                        }catch (e:Exception){
                            MapUtility.showDialog(e.toString(),requireActivity())

                        }
                    }
                    // Get your json response and convert it to whatever you want.
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("SendData", "error===" + error)
                    progressDialog.hide()
                    MapUtility.showDialog(error.toString(),requireActivity())
                    //Toast.makeText(requireActivity(), "Something went wrong!", Toast.LENGTH_LONG).show()

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


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (isLocationPermissionGranted()) {
            if (hasGps) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, gpsLocationListener)
            }
//------------------------------------------------------//
            if (hasNetwork) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, networkLocationListener)
            }


            val lastKnownLocationByGps =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)


            lastKnownLocationByGps?.let {
                locationByGps = lastKnownLocationByGps
            }

            val lastKnownLocationByNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            lastKnownLocationByNetwork?.let {
                locationByNetwork = lastKnownLocationByNetwork
            }
            if (locationByNetwork == null) {
                Toast.makeText(requireActivity(), "No Network", Toast.LENGTH_LONG).show()

            } else {


                    to_lat = locationByNetwork?.latitude
                    to_lng = locationByNetwork?.longitude

                    direction

            }

        } else {
            requestPermissions()
        }
    }


    private fun moveMap() {
        val latLng = LatLng(to_lat!!, to_lng!!)
        mMap!!.addMarker(MarkerOptions().position(latLng)) //Adding a title
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(15f))
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                if (press.equals("manual")) {
                    manualLoc!!.setText(place.address)
                    from_lat = place.latLng.latitude
                    from_lng = place.latLng.longitude
                    onResu = "false"
                }else if (press.equals("live")){
                    to_lat = place.latLng.latitude
                    to_lng = place.latLng.longitude
                    liveLoc!!.setText(place.address)
                    onResu = "false"
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
            }
        }else if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getStringExtra("result")
                fetchCurrentLocation()
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

    }

    @SuppressLint("MissingPermission")
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {

                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {

                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {

                    return true
                }
            }
        }
        return false
    }

    override fun onBackPressed(): Boolean {
        ll_location.isVisible = true
        ll_choose_vehicle?.isVisible  =false

        return true
    }


    override fun Any.onBackPressed(): Boolean {
        ll_location.isVisible = true
        ll_choose_vehicle?.isVisible  =false

        return true
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    private fun isLocationPermissionGranted(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                requestcodes
            )
            false
        } else {
            true
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

    val networkLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            locationByNetwork= location
           // locationByNetwork= location
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        locationManager = requireActivity().getSystemService(
            LOCATION_SERVICE
        ) as LocationManager

        val providers: List<String> = locationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val l: Location = locationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                // Found best last known location: %s", l);
                bestLocation = l
            }
        }
        return bestLocation
    }


    override fun openSomeActivityForResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled=true;
            mMap.uiSettings.isMapToolbarEnabled=true;
            mMap.uiSettings.isMyLocationButtonEnabled=true;
            checkLocationService();
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        @SuppressLint("MissingPermission")


        mMap = googleMap
          val latLng = LatLng(to_lat!!, to_lng!!)

         mMap!!.addMarker(MarkerOptions().position(latLng).draggable(true))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(10f))
        mMap!!.setOnMarkerDragListener(this)
         //  mMap!!.setOnMapLongClickListener(this)

    }
    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            // Got last known location. In some rare situations this can be null.
            // 3
            if (location != null) {

                if (selects.equals("start")){
                    lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    to_lat = location.latitude
                    to_lng = location.longitude

                    val geocoder: Geocoder
                    val addresses: List<Address>
                    geocoder = Geocoder(requireActivity(), Locale.getDefault())

                    var strAdd : String? = null
                    try {
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
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
                        Log.w(" Current loction address", "Canont get Address!")
                    }
                    liveLoc?.setText(strAdd)


                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                }else{
                    lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    from_lat = location.latitude
                    from_lng = location.longitude


                    val geocoder: Geocoder
                    val addresses: List<Address>
                    geocoder = Geocoder(requireActivity(), Locale.getDefault())

                    var strAdd : String? = null
                    try {
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
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
                        Log.w(" Current loction address", "Canont get Address!")
                    }
                    manualLoc?.setText(strAdd)

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                }

            }
        }
    }

    fun checkLocationService() {

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2 * 1000);


        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        // builder.setAlwaysShow(true);
        val client = LocationServices.getSettingsClient(requireActivity())
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(requireActivity()) {it->
            it.locationSettingsStates;
            fetchCurrentLocation();
        }

        task.addOnFailureListener(requireActivity()) { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(
                        requireActivity(),
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }

            }
        }
    }
    override fun onConnected(bundle: Bundle?) {
       getLocation()
    }

    override fun onConnectionSuspended(i: Int) {}
    override fun onConnectionFailed(connectionResult: ConnectionResult) {}

    override fun onCameraMoveStarted(p0: Int) {
        Log.v("Onmove start","Onmove "+p0);
        mMap.clear()
    }

    override fun onCameraMove() {

        Log.v("Onmove ","Onmove ");
    }

    override fun onCameraMoveCanceled() {
        Log.v("Onmove cancel","Onmove ");
    }

    override fun onCameraIdle() {
        Log.v("Onmove Idle","Onmove ");
        val markerOptions = MarkerOptions().position(mMap.cameraPosition.target)

        mMap.addMarker(markerOptions)
        val position: LatLng = markerOptions.getPosition()


    }
    private val direction: Unit

    //Showing a dialog till we get the route

        //Creating a string request
        private get() {
            //Getting the URL

            val url = makeURL(to_lat!!, to_lng!!, to_lat!!, to_lng!!)

            //Showing a dialog till we get the route
            val loading = ProgressDialog.show(requireActivity(), "Getting User Location", "Please wait...", false, false)

            //Creating a string request
            val stringRequest = StringRequest(url,
                { response ->
                    loading.dismiss()
                    //Calling the method drawPath to draw the path
                    //drawPath(response)
                }
            ) { loading.dismiss() }

            //Adding the request to request queue
            val requestQueue = Volley.newRequestQueue(requireActivity())
            requestQueue.add(stringRequest)
        }

    fun makeURL(sourcelat: Double, sourcelog: Double, destlat: Double, destlog: Double): String {
        val urlString = StringBuilder()
        urlString.append("https://maps.googleapis.com/maps/api/directions/json")
        urlString.append("?origin=") // from
        urlString.append(java.lang.Double.toString(sourcelat))
        urlString.append(",")
        urlString
            .append(java.lang.Double.toString(sourcelog))
        urlString.append("&destination=") // to
        urlString
            .append(java.lang.Double.toString(destlat))
        urlString.append(",")
        urlString.append(java.lang.Double.toString(destlog))
        urlString.append("&sensor=false&mode=driving&alternatives=true")
        urlString.append("&key=AIzaSyCbd3JqvfSx0p74kYfhRTXE7LZghirSDoU")
        return urlString.toString()
    }//Calling the method drawPath to draw the path



    override fun onMarkerDragStart(marker: Marker) {

    }
    override fun onMarkerDrag(marker: Marker) {}
    override fun onMarkerDragEnd(marker: Marker) {
        val position = marker.position

        if (selects.equals("start")){

            to_lat = position.latitude
            to_lng = position.longitude

            var geocoder: Geocoder
            val addresses: List<Address>
            geocoder = Geocoder(requireActivity(), Locale.getDefault())


            var strAdd : String? = null
            try {
                val addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1)
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
            liveLoc?.setText(strAdd)


        }else{

            from_lat = position.latitude
            from_lng = position.longitude


            val geocoder: Geocoder
            val addresses: List<Address>
            geocoder = Geocoder(requireActivity(), Locale.getDefault())

            var strAdd : String? = null
            try {
                val addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1)
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
                Log.w(" Current loction address", "Canont get Address!")
            }
            manualLoc?.setText(strAdd)

        }
}

    private fun getCurrentLoc(){
        to_lat = pref.getToLatL().toDouble()
        to_lng = pref.getToLngL().toDouble()

        var geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(requireActivity(), Locale.getDefault())


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
        liveLoc?.setText(strAdd)
    }


    private fun getDestinationLoc(){

        from_lat = pref.getToLatM().toDouble()
        from_lng = pref.getToLngM().toDouble()

        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(requireActivity(), Locale.getDefault())

        var strAdd : String? = null
        try {
            val addresses = geocoder.getFromLocation(from_lat!!, from_lng!!, 1)
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
            Log.w(" Current loction address", "Canont get Address!")
        }
        manualLoc?.setText(strAdd)

    }
    override fun onResume() {
        super.onResume()


        if (onResu.equals("false")){
            onResu = ""
        }
        else {
            if (pref.getType().equals("1")) {

                if (pref.getToLatL().equals("")) {

                } else {
                    getCurrentLoc()

                }
                if (pref.getToLatM().equals("")) {


                } else {
                    if (pref.getToLatM().equals("")) {

                    } else {
                        getDestinationLoc()
                    }
                }


            } else if (pref.getType().equals("2")) {
                if (pref.getToLatL().equals("")) {

                } else {
                    if (pref.getToLatL().equals("")) {

                    } else {
                        getCurrentLoc()

                    }
                }
                if (pref.getToLatM().equals("")) {

                } else {
                    getDestinationLoc()
                }


            }
        }

    }


}