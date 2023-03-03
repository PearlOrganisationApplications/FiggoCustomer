package com.figgo.customer.UI.OneWayOutstationActivity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.figgo.customer.Model.OneWayvehiclelistModel
import com.figgo.customer.R
import com.figgo.customer.UI.AdavanceCityCabActivity.LocationPickerActivity
import com.figgo.customer.Util.MapUtility
import com.figgo.customer.pearlLib.BaseClass
import com.figgo.customer.pearlLib.Helper
import com.figgo.customer.pearlLib.PrefManager
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class OneWay_Activity : BaseClass() {

    lateinit var pref:PrefManager
    private var onResu: String? = ""
    var to_lat :Double ?= 0.0
    var from_lat :Double ?= 0.0
    var to_lng :Double ?= 0.0
    var from_lng :Double ?= 0.0
    var tv_datetext:TextView? = null
    var tv_timetext:TextView? = null
    var tv_datetextroundtour:TextView? = null
    var tv_timepicroundtour:TextView? = null
    var datepicroundtour:TextView? = null
    var tv_liveloc:TextView? = null
    var tv_manualloc:TextView? = null
    var tvtimepicroundtour:TextView? = null
    var tv_fromdomdatepic:TextView? = null
    var tv_freedomtimepic:TextView? = null
    var selects : String ?= ""
    private val ADDRESS_PICKER_REQUEST = 1
    var AUTOCOMPLETE_REQUEST_CODE = -1
    private var hasGps = false
    private var hasNetwork = false
    var liveLat : String ?= ""
    var liveLng : String ?= ""
    var manualLng : String ?= ""
    var manuaLat : String ?= ""
      var press : String ?= ""
    private var locationByGps: Location? = null
    private var locationByNetwork: Location? = null
    lateinit var locationManager: LocationManager
    lateinit var cTimer : CountDownTimer

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
        setContentView(R.layout.activity_one_way_out_station)
        pref = PrefManager(this@OneWay_Activity)
        var window=window
        window.setStatusBarColor(Color.parseColor("#000F3B"))

        var ll_oneway = findViewById<LinearLayout>(R.id.ll_oneway)
        var ll_freedom = findViewById<LinearLayout>(R.id.ll_freedom)
        var ll_roundtour  = findViewById<LinearLayout>(R.id.ll_roundtour)
        var oneway  = findViewById<CardView>(R.id.oneway)
        var roundandtour  = findViewById<CardView>(R.id.roundandtour)
        var freedomoneway  = findViewById<CardView>(R.id.freedomoneway)
        var iv_freedom  = findViewById<ImageView>(R.id.iv_freedom)
        var iv_oneway  = findViewById<ImageView>(R.id.iv_oneway)
        var iv_round  = findViewById<ImageView>(R.id.iv_round)
        var submitoutstation1 = findViewById<LinearLayout>(R.id.submitoutstation1)
        var shareimg = findViewById<ImageView>(R.id.shareimg)
        var backimg = findViewById<ImageView>(R.id.backimg)
        var ll_submitroundtour = findViewById<LinearLayout>(R.id.ll_submitroundtour)
        var ll_callender = findViewById<LinearLayout>(R.id.ll_callender)
        var ll_watch = findViewById<LinearLayout>(R.id.ll_watch)
        var ll_roundtourdatepicker = findViewById<LinearLayout>(R.id.ll_roundtourdatepicker)
        var ll_timepickerroundtour = findViewById<LinearLayout>(R.id.ll_timepickerroundtour)
        var ll_datepicroundtour = findViewById<LinearLayout>(R.id.ll_datepicroundtour)
        var ll_timepicroundtour = findViewById<LinearLayout>(R.id.ll_timepicroundtour)
        var ll_fromdomdatepic = findViewById<LinearLayout>(R.id.ll_fromdomdatepic)
        var ll_freedomtimepic = findViewById<LinearLayout>(R.id.ll_freedomtimepic)

        var ll_liveloc = findViewById<LinearLayout>(R.id.ll_liveloc)
        var ll_manual = findViewById<LinearLayout>(R.id.ll_manual)
         tv_liveloc = findViewById<TextView>(R.id.tv_liveloc)
         tv_manualloc = findViewById<TextView>(R.id.tv_manualloc)

        tv_datetext = findViewById<TextView>(R.id.tv_datetext)
        tv_timetext = findViewById<TextView>(R.id.tv_timetext)
        tv_datetextroundtour = findViewById<TextView>(R.id.tv_datetextroundtour)
        tv_timepicroundtour = findViewById<TextView>(R.id.tv_timepicroundtour)
        datepicroundtour = findViewById<TextView>(R.id.datepicroundtour)
        tvtimepicroundtour = findViewById<TextView>(R.id.tvtimepicroundtour)
        tv_fromdomdatepic = findViewById<TextView>(R.id.tv_fromdomdatepic)
        tv_freedomtimepic = findViewById<TextView>(R.id.tv_freedomtimepic)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//------------------------------------------------------//
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val apiKey = getString(R.string.api_key)

        if (!Places.isInitialized()) {
            Places.initialize(this@OneWay_Activity, apiKey)
        }
        startTimer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val currentDate = LocalDateTime.now().format(formatter)
            tv_datetext?.setText(currentDate)
        }
        submitoutstation1.setOnClickListener {

            if (tv_liveloc?.text.toString().equals("")){
                Toast.makeText(this@OneWay_Activity, "Please select Start Address", Toast.LENGTH_LONG).show()
            }else if (tv_manualloc?.text.toString().equals("")){
                Toast.makeText(this@OneWay_Activity, "Please select Destination Address", Toast.LENGTH_LONG).show()

            }else {
                submitform()
            }


        }

        ll_submitroundtour.setOnClickListener {
          // startActivity(Intent(this, Maps1_OutStationActivity::class.java))

        }

        oneway.setOnClickListener {
            ll_oneway.isVisible = true
            ll_freedom.isVisible = false
            ll_roundtour.isVisible = false
            iv_oneway.setBackgroundColor(resources.getColor(R.color.coloryellow))
            iv_round.setBackgroundColor(resources.getColor(R.color.white))
            iv_freedom.setBackgroundColor(resources.getColor(R.color.white))


        }

        roundandtour.setOnClickListener {

            ll_oneway.isVisible = false
            ll_freedom.isVisible = false
            ll_roundtour.isVisible = true

            iv_oneway.setBackgroundColor(resources.getColor(R.color.white))
            iv_round.setBackgroundColor(resources.getColor(R.color.coloryellow))
            iv_freedom.setBackgroundColor(resources.getColor(R.color.white))

        }
        freedomoneway.setOnClickListener {

            ll_oneway.isVisible = false
            ll_freedom.isVisible = true
            ll_roundtour.isVisible = false
            iv_oneway.setBackgroundColor(resources.getColor(R.color.white))
            iv_round.setBackgroundColor(resources.getColor(R.color.white))
            iv_freedom.setBackgroundColor(resources.getColor(R.color.coloryellow))

        }





        ll_callender.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = this?.let { it1 ->
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
                        tv_datetext?.setText(dat)
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

        ll_watch.setOnClickListener {
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
                if (tv_timetext != null) {
                    if (::cTimer.isInitialized) {
                        cTimer.cancel()
                    }
                    tv_timetext?.text = SimpleDateFormat("HH:mm:ss").format(cal.time)+""+am_pm+""
                }
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        }

        ll_roundtourdatepicker.setOnClickListener {

            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = this?.let { it1 ->
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
                        tv_datetextroundtour?.setText(dat)
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

        ll_timepickerroundtour.setOnClickListener {
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
                if (tv_timepicroundtour != null) {
                    if (::cTimer.isInitialized) {
                        cTimer.cancel()
                    }
                    tv_timepicroundtour?.text = SimpleDateFormat("HH:mm:ss").format(cal.time)+""+am_pm+""
                }
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()

        }

        tv_liveloc?.setOnClickListener {

            press = "live"
            val field = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field)
                .build(this@OneWay_Activity)
            startForResult.launch(intent)
        }
        tv_manualloc?.setOnClickListener {

            press = "manual"
            val field = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field)
                .build(this@OneWay_Activity)
            startForResult.launch(intent)
        }

        ll_liveloc?.setOnClickListener {
            val internet :Boolean = isOnline(this@OneWay_Activity)
            if(internet == true) {
                selects = "start"
                pref.setType("1")
                val i = Intent(this@OneWay_Activity, LocationPickerActivity::class.java)
                startActivityForResult(i, ADDRESS_PICKER_REQUEST)
            }else{
                Toast.makeText(this@OneWay_Activity, "Please turn on internet", Toast.LENGTH_LONG).show()

            }

        }

        ll_manual?.setOnClickListener {

            val internet :Boolean = isOnline(this@OneWay_Activity)
            if(internet == true) {
                pref.setType("2")
                selects = "dest"
                val i = Intent(this@OneWay_Activity, LocationPickerActivity::class.java)
                startActivityForResult(i, ADDRESS_PICKER_REQUEST)

            }else{
                Toast.makeText(this@OneWay_Activity, "Please turn on internet", Toast.LENGTH_LONG).show()

            }
        }

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
                   // tv_datetext?.setText(currentDate)
                    tv_timetext?.setText(time)
                } else {
                    TODO("VERSION.SDK_INT < O")
                }

            }

            override fun onFinish() {


            }
        }
        cTimer.start()
    }
    private fun submitform() {
        pref.setToLatLC(to_lat.toString())
        pref.setToLngLC(to_lng.toString())
        pref.setToLatMC(from_lat.toString())
        pref.setToLngMC(from_lng.toString())
        pref.setTime(tv_timetext?.text.toString())
        pref.setDate(tv_datetext?.text.toString())
        pref.setLiveLoc( tv_liveloc?.text.toString())
        pref.setManualLoc( tv_manualloc?.text.toString())
        val progressDialog = ProgressDialog(this@OneWay_Activity)
        progressDialog.show()
        val URL = Helper.ONEWAY_CREATE_RIDE
        Log.d("url",URL)
        val queue = Volley.newRequestQueue(this@OneWay_Activity)
        val json = JSONObject()
        json.put("date", tv_datetext?.text.toString())
        json.put("time", tv_timetext?.text.toString())
        json.put("to_lat", to_lat)
        json.put("to_lng", to_lng)
        json.put("from_lat", from_lat)
        json.put("from_lng", from_lng)
        json.put("type", "advance_booking")
        json.put("to_location_name", tv_liveloc?.text.toString())
        json.put("from_location_name", tv_manualloc?.text.toString())

        Log.d("requestdata",""+json)


        val jsonOblect: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URL, json, object :
            Response.Listener<JSONObject?>               {
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(response: JSONObject?) {
                Log.d("SendData", "response===" + response)
                if (response != null) {
                    progressDialog.hide()
                    try {



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

                           MapUtility.cablistOneWay.add(
                               OneWayvehiclelistModel(
                                    name,
                                    image,
                                    rideId,
                                    vehicle_id,
                                    min,
                                    max
                                )
                            )
                        }
                        startActivity(Intent(this@OneWay_Activity, BookingDestinatiion::class.java))

                    }catch (e:Exception){
                        MapUtility.showDialog(e.toString(),this@OneWay_Activity)

                    }
                }
                // Get your json response and convert it to whatever you want.
            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("SendData", "error===" + error)
                progressDialog.hide()
                MapUtility.showDialog(error.toString(),this@OneWay_Activity)
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
    private fun getCurrentLoc(){
        to_lat = pref.getToLatL().toDouble()
        to_lng = pref.getToLngL().toDouble()

        var geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(this@OneWay_Activity, Locale.getDefault())


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
        tv_liveloc?.setText(strAdd)
    }
    private fun getDestinationLoc(){

        from_lat = pref.getToLatM().toDouble()
        from_lng = pref.getToLngM().toDouble()

        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(this@OneWay_Activity, Locale.getDefault())

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
        tv_manualloc?.setText(strAdd)

    }

    val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->

    }


    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {



            if (resultCode == AppCompatActivity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                if (press.equals("manual")) {
                    tv_manualloc!!.setText(place.address)
                    from_lat = place.latLng.latitude
                    from_lng = place.latLng.longitude
                    onResu = "false"
                }else if (press.equals("live")){
                    to_lat = place.latLng.latitude
                    to_lng = place.latLng.longitude
                    tv_liveloc!!.setText(place.address)
                    onResu = "false"
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
            }


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


