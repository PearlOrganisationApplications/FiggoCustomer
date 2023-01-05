package com.pearlorganisation.figgo.UI.Fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.pearlorganisation.PrefManager
import com.pearlorganisation.figgo.Adapter.AdvanceCityDataAdapter
import com.pearlorganisation.figgo.Adapter.OneWayKmCountAdapter
import com.pearlorganisation.figgo.CurrentMap.MapsActivity1
import com.pearlorganisation.figgo.IOnBackPressed
import com.pearlorganisation.figgo.Model.AdvanceCityCabModel
import com.pearlorganisation.figgo.Model.CurrentModel
import com.pearlorganisation.figgo.OneWayRideFragment
import com.pearlorganisation.figgo.OneWay_Km_CountActivity
import com.pearlorganisation.figgo.R
import com.pearlorganisation.figgo.databinding.ActivityMainBinding
import com.pearlorganisation.figgo.databinding.FragmentCurrentCityCabBinding
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class Current_cityCab : Fragment(),IOnBackPressed {
    lateinit var binding: FragmentCurrentCityCabBinding

    lateinit var advanceCityAdapter: AdvanceCityDataAdapter
    private lateinit var recyclerView: RecyclerView
    lateinit var recyclerv: RecyclerView
    var cablist=ArrayList<AdvanceCityCabModel>()
    var mList= ArrayList<CurrentModel>()
    lateinit var oneWayKmCountAdapter: OneWayKmCountAdapter

    var to_lat :String ?= ""
    var from_lat :String ?= ""
    var to_lng :String ?= ""
    var from_lng :String ?= ""
    var current_loc:TextView? = null
    var destination_loc:TextView? = null
    var to_location_name:String ?= null
    var from_location_name:String? = null
    var linear_des:String ? = " "
    var live_loc:String ? = " "
    lateinit var pref: PrefManager
    var selects : String ?= "";
    lateinit var ll_location : CardView
    lateinit var ll_choose_vehicle : CardView
    var press : String ?= "";
    var datetext: TextView? = null
    var timetext: TextView? = null
    var manualLoc: TextView? = null
    var progress: ProgressBar? = null
    var liveLoc: TextView? = null
    var nxtbtn: Button? = null
    private lateinit var mainBinding: ActivityMainBinding
    var AUTOCOMPLETE_REQUEST_CODE = -1
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_current_city_cab, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = PrefManager(requireActivity())
        var calenderimg = view.findViewById<LinearLayout>(R.id.calenderimg)
        datetext = view.findViewById<TextView>(R.id.datetext)
        var watchimg = view?.findViewById<LinearLayout>(R.id.watchimg)
        timetext = view?.findViewById<TextView>(R.id.timetext)
        ll_location = view?.findViewById<CardView>(R.id.ll_location)!!
        ll_choose_vehicle = view?.findViewById<CardView>(R.id.ll_choose_vehicle)!!
        manualLoc = view?.findViewById<TextView>(R.id.loc_manual)
        liveLoc = view?.findViewById<TextView>(R.id.live_loc)
        nxtbtn = view.findViewById(R.id.nxtbtn)
        recyclerView = view.findViewById(R.id.current_cab_list)
        progress = view.findViewById<ProgressBar>(R.id.progress)
        val recyclerv = view.findViewById<RecyclerView>(R.id.onewayvehiclelist)
      //  current_loc = view?.findViewById<TextView>(R.id.current_loc)
      /*  destination_loc = view?.findViewById<TextView>(R.id.destination_loc)*/

        var locLinear = view?.findViewById<LinearLayout>(R.id.linear_loc)
        var submit = view?.findViewById<Button>(R.id.submit)
        var destLinear = view?.findViewById<LinearLayout>(R.id.linear_des)
        ll_choose_vehicle?.isVisible = false

        val apiKey = getString(R.string.api_key)
        if (!Places.isInitialized()) {
            Places.initialize(requireActivity(), apiKey)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val currentDate = LocalDateTime.now().format(formatter)
            val formated = DateTimeFormatter.ofPattern("HH:mm:s")
            val currentTime = LocalDateTime.now().format(formated)

            datetext?.setText(currentDate)
            timetext?.setText(currentTime)
        } else {

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
                            if (monthOfYear < 9){
                                dat = (year.toString() + "-0" + (monthOfYear + 1) + "-" + dayOfMonth.toString())
                            }else {
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
            watchimg?.setOnClickListener {
                val cal = Calendar.getInstance()
                val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    if (timetext != null) {
                        timetext?.text = SimpleDateFormat("HH:mm:s").format(cal.time)
                    }
                }
                TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
            }

        }
        manualLoc?.setOnClickListener {

            press = "manual";
            val field = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field)
                    .build(requireActivity())
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        liveLoc?.setOnClickListener {

            press = "live";
            val field = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field)
                    .build(requireActivity())
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        submit?.setOnClickListener {
            if (to_lat == ""){
              //  startActivity(Intent(requireActivity(), OneWay_Km_CountActivity::class.java))
                Toast.makeText(requireActivity(), "Please select Start Address", Toast.LENGTH_LONG).show()
            }else if (from_lat == ""){
                Toast.makeText(requireActivity(), "Please select Destination Address", Toast.LENGTH_LONG).show()

            }else {
                submitform()
            }

            nxtbtn?.setOnClickListener {
                startActivity(Intent(requireActivity(), MapsActivity1::class.java))
                    /*vehicle_type_id.setvehicle_type_id("vehicle_type_id")
                    ride_id.setride_id("ride_id")*/

            }

        }
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locLinear?.setOnClickListener {

            val internet :Boolean = isOnline(requireActivity())
            if(internet == true) {
                mainBinding = ActivityMainBinding.inflate(layoutInflater)
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                selects = "start"

                getLocation()
            }else{
                Toast.makeText(requireActivity(), "Please turn on internet", Toast.LENGTH_LONG).show()

            }
        }

        destLinear?.setOnClickListener {

            val internet :Boolean = isOnline(requireActivity())
            if(internet == true) {
                mainBinding = ActivityMainBinding.inflate(layoutInflater)
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

                selects = "dest"
                getLocation()
            }else{
                Toast.makeText(requireActivity(), "Please turn on internet", Toast.LENGTH_LONG).show()

            }
        }


       /* cablist.add(AdvanceCityCab(R.drawable.figgo_auto,"75-100"))
        cablist.add(AdvanceCityCab(R.drawable.figgo_bike,"45-65"))
        cablist.add(AdvanceCityCab(R.drawable.figgo_e_rick,"25-40"))
        cablist.add(AdvanceCityCab(R.drawable.figgo_lux,"125-400"))*/
     //   advanceCityAdapter=AdvanceCityAdapter(requireActivity(),cablist)
     //   binding.currentCabList.adapter=advanceCityAdapter

    }

    private fun submitform() {
        progress?.isVisible = true
        ll_location?.isVisible = false
        ll_choose_vehicle?.isVisible  =false
        val URL = "https://test.pearl-developer.com/figo/api/ride/create-city-ride"
        val queue = Volley.newRequestQueue(requireContext())
        val json = JSONObject()
        json.put("date", datetext?.text.toString())
        json.put("time", timetext?.text.toString())
        json.put("to_lat", to_lat)
        json.put("to_lng", to_lng)
        json.put("from_lat", from_lat)
        json.put("from_lng", from_lng)
        json.put("to_location_name", manualLoc?.text.toString())
        json.put("from_location_name", liveLoc?.text.toString())
        json.put("type","current_booking")
        Log.d("SendData", "json===" + json)
        val jsonOblect: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URL, json, object :
                        Response.Listener<JSONObject?>               {
                    override fun onResponse(response: JSONObject?) {

                        Log.d("SendData", "response===" + response)
                        if (response != null) {
                            val status = response.getString("status")
                            if(status.equals("false")){
                                Toast.makeText(requireActivity(), "Something Went Wrong!", Toast.LENGTH_LONG).show()
                            }else{
                                val data = response.getJSONObject("data")
                                val ride_id = data.getString("ride_id")
                                val vehicle_types = data.getJSONArray("vehicle_types")
                                for (i in 0 until vehicle_types.length()){


                                    val name = vehicle_types.getJSONObject(i).getString("name")
                                    val image = vehicle_types.getJSONObject(i).getString("full_image")
                                    val id = vehicle_types.getJSONObject(i).getString("id")
                                    val min_price = vehicle_types.getJSONObject(i).getString("min_price")
                                    val max_price = vehicle_types.getJSONObject(i).getString("max_price")

                                    cablist.add(AdvanceCityCabModel(name,image,id,ride_id,min_price, max_price))

                                }
                                advanceCityAdapter= AdvanceCityDataAdapter(requireActivity(),cablist)
                                recyclerView.adapter=advanceCityAdapter
                                recyclerView.layoutManager=GridLayoutManager(context,4)
                                progress?.isVisible = false
                                ll_location?.isVisible = false
                                ll_choose_vehicle?.isVisible  =true
                            }

                          /*

                            val size = response.getJSONObject("data").getJSONArray("vehicle_types").length()
                            val ride_id = response.getJSONObject("data").getString("ride_id")

                            for(p2 in 0 until size) {
                                val name = response.getJSONObject("data").getJSONArray("vehicle_types").getJSONObject(p2).getString("name")
                                val image = response.getJSONObject("data").getJSONArray("vehicle_types").getJSONObject(p2).getString("full_image")
                               *//* val ride_id = response.getJSONObject("data").getJSONArray("vehicle_types").getJSONObject(p2).getString("ride_id")*//*
                                val ride_id = response.getJSONObject("data").getString("ride_id")
                                val vehicle_id = response.getJSONObject("data").getString("vehicle_id")

                                cablist.add(AdvanceCityCabModel(name,image,vehicle_id,ride_id))
                            }
                            advanceCityAdapter= AdvanceCityDataAdapter(requireActivity(),cablist)
                            binding.currentCabList.adapter=advanceCityAdapter*/

                        }
                        // Get your json response and convert it to whatever you want.
                    }
                }, object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {
                        Log.d("SendData", "error===" + error)
                        Toast.makeText(requireActivity(), "Something Went Wrong!", Toast.LENGTH_LONG).show()
                    }
                }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers: MutableMap<String, String> = HashMap()
                        headers.put("Content-Type", "application/json; charset=UTF-8");
                        headers.put("Authorization", "Bearer " + pref.getToken());
                        return headers
                    }
                }
        jsonOblect.setRetryPolicy(DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))

        queue.add(jsonOblect)

    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {

        mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
            val location: Location? = task.result
            if (location == null) {
                return@addOnCompleteListener;
            }
            if (location != null) {
                val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                val list: List<Address> =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
                mainBinding.apply {
                    if (selects.equals("start")) {
                        //   tvLatitude.text = "Latitude\n${list[0].latitude}"
                        to_lat = "${list[0].latitude}"
                        to_lng = "${list[0].longitude}"
                        // tvCountryName.text = "Country Name\n${list[0].countryName}"
                        var location: String? = "${list[0].getAddressLine(0)}"
                        liveLoc!!.text = location?.replace("133", "")?.replace(",","")
                        //tvAddress.text = "Address\n${list[0].getAddressLine(0)}"
                    }else{
                        from_lat  = "${list[0].latitude}"
                        from_lng = "${list[0].longitude}"


                        var location: String? = "${list[0].getAddressLine(0)}"
                        manualLoc!!.text = location?.replace("133", "")?.replace(",","")
                        //tvAddress.text = "Address\n${list[0].getAddressLine(0)}"
                    }
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                if (press.equals("manual")) {
                    manualLoc!!.setText(place.address)
                    from_lat = place.latLng.latitude.toString()
                    from_lng = place.latLng.longitude.toString()
                }else if (press.equals("live")){
                    to_lat = place.latLng.latitude.toString()
                    to_lng = place.latLng.longitude.toString()
                    liveLoc!!.setText(place.address)
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(
                        data!!
                )
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
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

        ll_location?.isVisible = true
        ll_choose_vehicle?.isVisible  =false

        return true
    }


}
