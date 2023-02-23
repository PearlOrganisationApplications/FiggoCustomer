package com.figgo.customer.UI.AdavanceCityCabActivity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.*
import android.location.LocationListener
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.figgo.customer.pearlLib.PrefManager
import com.figgo.customer.Adapter.HistoryAdapter
import com.figgo.customer.Adapter.RideCityAdapter
import com.figgo.customer.Model.Data
import com.figgo.customer.Model.HistoryAdd
import com.figgo.customer.R
import com.figgo.customer.Util.MapUtility
import com.release.gfg1.DBHelper
import java.io.IOException
import java.util.*
import java.util.regex.Pattern


class LocationPickerActivity :AppCompatActivity(), OnMapReadyCallback, RideCityAdapter.ItemListener {
    private val REQUEST_CHECK_SETTINGS = 2
    private val REQUEST_ID_MULTIPLE_PERMISSIONS = 2
    private val TAG = LocationPickerActivity::class.java.simpleName
    var PLACE_AUTOCOMPLETE_REQUEST_CODE = 1
    var regex = "^(-?\\d+(\\.\\d+)?),\\s*(-?\\d+(\\.\\d+)?)$"
    var latLongPattern = Pattern.compile(regex)
    private var userAddress = ""
    private var userState = ""
    private var userCity = ""
    private var userPostalCode = ""
    private var userCountry = ""
    private var userAddressline2 = ""
    private val userAddressline1 = ""
    private var addressBundle: Bundle? = null
    private val addressdetails: List<*>? = null
    private var mLatitude = 0.0
    private var mLongitude = 0.0
    private var place_id = ""
    private var place_url = ""
    private var locationByGps: Location? = null
    private var locationByNetwork: Location? = null
    private var mMap: GoogleMap? = null
    private var mLocationPermissionGranted = false
    private var imgSearch: TextView? = null
    private var citydetail: TextView? = null
    private val addressline1: EditText? = null
    private var addressline2: EditText? = null
    lateinit var locationManager: LocationManager
    //inital zoom
    private val previousZoomLevel = -1.0f
    private var isZooming = false
    private var hasGps = false
    private var hasNetwork = false
    //Declaration of FusedLocationProviderClient
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val filterTaskList: MutableList<AsyncTask<*, *, *>> = ArrayList()
    private var doAfterPermissionProvided = 0
    private  var doAfterLocationSwitchedOn = 1
    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var startLocation = false
    private val arrayList: List<Data>? = null
    private val rideHistoryAdapter: RideCityAdapter? = null
    private val strToken = ""
    private val sharedPreferences: SharedPreferences? = null
    private var ll_history: LinearLayout? = null
    private var txt_showmap: LinearLayout? = null
    private var mapFrame: FrameLayout? = null
    private var txtSelectLocation: Button? = null
    private var moving_pointer: ImageView? = null
    private var imgCurrentloc: ImageView? = null
    private var locationPick = false
    private var type: String? = null
    private var count: String? = ""
    private var address: String? = ""
    private var rl_current_location: LinearLayout? = null
    private var edt_search: EditText? = null
    lateinit var pref: PrefManager
    lateinit var historyAddAdapter: HistoryAdapter
    var historyAddList=ArrayList<HistoryAdd>()

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_picker)

        rl_current_location = findViewById(R.id.rl_current_location)
        mapFrame = findViewById(R.id.frame)
        ll_history = findViewById(R.id.ll_history)
        imgCurrentloc = findViewById(R.id.imgCurrentloc)
        txtSelectLocation = findViewById(R.id.fab_select_location)
        val directionTool = findViewById<ImageView>(R.id.direction_tool)
        val googleMapTool = findViewById<ImageView>(R.id.google_maps_tool)
        val recycler_history = findViewById<RecyclerView>(R.id.recycler_history)
        txt_showmap = findViewById(R.id.txt_show_map)
        moving_pointer = findViewById(R.id.moving_pointer)
        edt_search = findViewById<EditText>(R.id.edt_search)
        imgSearch = findViewById(R.id.imgSearch)
        addressline2 = findViewById(R.id.addressline2)
        citydetail = findViewById(R.id.citydetails)



        edt_search?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {



            }
        })






        val db = DBHelper(this@LocationPickerActivity, null)

        // below is the variable for cursor
        // we have called method to get
        // all names from our database
        // and add to name text view
        val cursor = db.getAddress()
        if( cursor != null && cursor.moveToFirst() ) {
            // moving the cursor to first position and
            // appending value in the text view
            cursor!!.moveToFirst()
            historyAddList.add(
                HistoryAdd(
                    cursor.getString(cursor.getColumnIndex(DBHelper.ADDRESS)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.LAT)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.LNG))))


            // moving our cursor to next
            // position and appending values
            while (cursor.moveToNext()) {
                historyAddList.add(
                    HistoryAdd(
                        cursor.getString(cursor.getColumnIndex(DBHelper.ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.LAT)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.LNG))))

            }

            // at last we close our cursor
            cursor.close()
        }

        if (historyAddList.size > 0) {
            historyAddAdapter = HistoryAdapter(this@LocationPickerActivity, historyAddList)
            recycler_history.layoutManager = GridLayoutManager(this@LocationPickerActivity, 1)
            recycler_history.adapter = historyAddAdapter

        }


        pref = PrefManager(this)
        imgSearch?.isVisible = false
        try {
            startLocation = intent.getBooleanExtra("startLocation", false)
            type = intent.getStringExtra("type")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        addressBundle = Bundle()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//------------------------------------------------------//
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getLocationRequest()


        //define callback of location request
        locationCallback = object : LocationCallback() {
            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                Log.d(
                    TAG,
                    "onLocationAvailability: isLocationAvailable =  " + locationAvailability.isLocationAvailable
                )
            }

            override fun onLocationResult(locationResult: LocationResult) {
                Log.d(TAG, "onLocationResult: $locationResult")
                if (locationResult == null) {
                    return
                }
                when (doAfterLocationSwitchedOn) {
                    1 -> startParsingAddressToShow()
                    2 -> {}
                    3 -> {}
                }
                //Location fetched, update listener can be removed
                locationCallback?.let { fusedLocationProviderClient!!.removeLocationUpdates(it) }
            }
        }


        // Try to obtain the map from the SupportMapFragment.
        val mapFragment = fragmentManager.findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)
        //if you want to open the location on the LocationPickerActivity through intent
        //if you want to open the location on the LocationPickerActivity through intent
        val i = intent
        if (i != null) {
            val extras = i.extras
            if (extras != null) {
                userAddress = extras.getString(MapUtility.ADDRESS).toString()
                //temp -> get lat , log from db
                mLatitude = intent.getDoubleExtra(MapUtility.LATITUDE, 0.0)
                mLongitude = intent.getDoubleExtra(MapUtility.LONGITUDE, 0.0)
            }
        }

        if (savedInstanceState != null) {
            mLatitude = savedInstanceState.getDouble("latitude")
            mLongitude = savedInstanceState.getDouble("longitude")
            userAddress = savedInstanceState.getString("userAddress")!!
            currentLatitude = savedInstanceState.getDouble("currentLatitude")
            currentLongitude = savedInstanceState.getDouble("currentLongitude")
        }

        if (!MapUtility.isNetworkAvailable(this)) {
            MapUtility.showToast(this, "Please Connect to Internet")
        }

        imgSearch?.setOnClickListener(View.OnClickListener {
            locationPick = true
            ll_history?.setVisibility(View.VISIBLE)
            rl_current_location?.setVisibility(View.VISIBLE)
            txt_showmap?.setVisibility(View.VISIBLE)
            mapFrame?.setVisibility(View.GONE)
            imgCurrentloc?.setVisibility(View.GONE)
            txtSelectLocation?.setVisibility(View.VISIBLE)
            moving_pointer?.setVisibility(View.GONE)
            /*    if (!Places.isInitialized()) {
                    Places.initialize(this@LocationPickerActivity, MapUtility.apiKey)
                }
                // Set the fields to specify which types of place data to return.
                val fields = Arrays.asList(
                    Place.Field.ID,
                    Place.Field.ADDRESS,
                    Place.Field.NAME,
                    Place.Field.LAT_LNG
                )
                // Start the autocomplete intent.
                val intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields
                ) //                        .setTypeFilter(TypeFilter.ADDRESS)
                    .build(this@LocationPickerActivity)
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)*/
        })


        txtSelectLocation?.setOnClickListener(View.OnClickListener {

            // add data into intent and send back to Parent Activity
            // add data into intent and send back to Parent Activity


            if (addressline2?.text.toString().equals("")){
                Toast.makeText(
                    this,
                    "Select Location First",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }else{
                val db = DBHelper(this@LocationPickerActivity, null)

                if (pref.getType().equals("1")) {
                    pref.setToLatL(mLatitude.toString())
                    pref.setToLngL(mLongitude.toString())
                    db.addAddress(addressline2?.text.toString(),mLatitude.toString(),mLongitude.toString())
                    // historyAddList.add(HistoryAdd(addressline2?.text.toString(),mLatitude.toString(),mLongitude.toString()))
                }else{
                    pref.setToLatM(mLatitude.toString())
                    pref.setToLngM(mLongitude.toString())
                    db.addAddress(addressline2?.text.toString(),mLatitude.toString(),mLongitude.toString())
                    //  historyAddList.add(HistoryAdd(addressline2?.text.toString(),mLatitude.toString(),mLongitude.toString()))

                }
                finish()
            }



        })

        txt_showmap?.setOnClickListener(View.OnClickListener {

            count = "map"
            locationPick = false
            this@LocationPickerActivity.showCurrentLocationOnMap(false)
            imgSearch?.isVisible = true
            ll_history?.setVisibility(View.GONE)
            txt_showmap?.setVisibility(View.GONE)
            mapFrame?.setVisibility(View.VISIBLE)
            imgCurrentloc?.setVisibility(View.VISIBLE)
            txtSelectLocation?.setVisibility(View.VISIBLE)
            moving_pointer?.setVisibility(View.VISIBLE)
            rl_current_location?.setVisibility(View.GONE)
        })

        rl_current_location?.setOnClickListener(View.OnClickListener {
            getLocation()

        })

        imgCurrentloc?.setOnClickListener(View.OnClickListener {
            this@LocationPickerActivity.showCurrentLocationOnMap(false)
            doAfterPermissionProvided = 2
            doAfterLocationSwitchedOn = 2
        })


        // google maps tools
        directionTool?.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(v: View) {
                    this@LocationPickerActivity.showCurrentLocationOnMap(true)
                    doAfterPermissionProvided = 3
                    doAfterLocationSwitchedOn = 3
                }
            })

        googleMapTool.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(v: View) {
                    // Default google map
                    val intent = Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            "http://maps.google.com/maps?q=loc:$mLatitude, $mLongitude"
                        )
                    )
                    this@LocationPickerActivity.startActivity(intent)
                }
            })

        /* arrayList = ArrayList()
         rideHistoryAdapter = RideCityAdapter(this, arrayList, this, type)
         recycler_history.layoutManager = LinearLayoutManager(this)
         recycler_history.adapter = rideHistoryAdapter*/
        // getHistory()



    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient!!.removeLocationUpdates(locationCallback!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //after a place is searched
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                locationPick = true
                val place = Autocomplete.getPlaceFromIntent(data)
                //                userAddress = place.getName() + ", " + place.getAddress();
                userAddress = place.address
                //  addressdetails=place.getAddressComponents();
                Log.e("cell l", "Location 1")
                imgSearch!!.text = "" + userAddress
                mLatitude = place.latLng.latitude
                mLongitude = place.latLng.longitude
                place_id = place.id
                //  place_url = place.websiteUri.toString()
                addressline2!!.setText("" + userAddress)
                //                addMarker();
//                getAddressByGeoCodingLatLng();
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data)
                Log.i(TAG, status.statusMessage!!)
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == REQUEST_CHECK_SETTINGS) {
            //after location switch on dialog shown
            if (resultCode != RESULT_OK) {
                //Location not switched ON
                Toast.makeText(
                    this@LocationPickerActivity,
                    "Location Not Available..",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Start location request listener.
                //Location will be received onLocationResult()
                //Once loc recvd, updateListener will be turned OFF.
                Toast.makeText(this, "Fetching Location...", Toast.LENGTH_LONG).show()
                startLocationUpdates()
            }
        }
    }
    private fun showCurrentLocationOnMap(isDirectionClicked: Boolean) {

        @SuppressLint("MissingPermission") val lastLocation =
            fusedLocationProviderClient!!.lastLocation
        lastLocation.addOnSuccessListener(
            this
        ) { location ->
            if (location != null) {
                mMap!!.clear()
                if (isDirectionClicked) {
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude
                    //Go to Map for Directions
                    val intent = Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            "http://maps.google.com/maps?saddr=$currentLatitude, $currentLongitude&daddr=$mLatitude, $mLongitude"
                        )
                    )
                    this@LocationPickerActivity.startActivity(intent)
                } else {
                    //Go to Current Location
                    mLatitude = location.latitude
                    mLongitude = location.longitude
                    this@LocationPickerActivity.getAddressByGeoCodingLatLng()
                }
            }
        }
        lastLocation.addOnFailureListener { //If perm provided then gps not enabled
            //                getSettingsLocation();
            Toast.makeText(
                this@LocationPickerActivity,
                "Location Not Availabe",
                Toast.LENGTH_SHORT
            )
                .show()
        }

    }
    fun resizeMapIcons(iconName: String?, width: Int, height: Int): Bitmap? {
        val imageBitmap = BitmapFactory.decodeResource(
            resources, resources.getIdentifier(
                iconName, "drawable",
                packageName
            )
        )
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false)
    }
    private fun addMarker() {
        val cameraUpdate: CameraUpdate
        val SPACE = " , "
        val coordinate = LatLng(mLatitude, mLongitude)
        if (mMap != null) {
            val markerOptions: MarkerOptions
            try {
                mMap?.clear()
                cameraUpdate = if (isZooming) {
                    //  camera will not Update
                    CameraUpdateFactory.newLatLngZoom(coordinate, mMap!!.cameraPosition.zoom)
                } else {
                    // camera will Update zoom
                    CameraUpdateFactory.newLatLngZoom(coordinate, 18f)
                }
                mMap?.animateCamera(cameraUpdate)
                mMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
                if (startLocation == false) {
//                    imgSearch.setText("" + userAddress);
                    markerOptions = MarkerOptions().position(coordinate).title(userAddress).icon(
                        BitmapDescriptorFactory.fromBitmap(
                            resizeMapIcons("ic_pointer", 100, 100)!!
                        )
                    )
                    val marker = mMap?.addMarker(markerOptions)
                }
                startLocation = false
                //marker.showInfoWindow();
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
        }
        try {
            userAddressline2 = userAddressline2.substring(0, userAddressline2.indexOf(userCity))
            // userAddressline.replace(userCity,"");
            //  userAddressline.replace(userPostalCode,"");
            //   userAddressline.replace(userState,"");
            //  userAddressline.replace(userCountry,"");
        } catch (ex: java.lang.Exception) {
            Log.d(TAG, "address error $ex")
        }
        try {
            citydetail!!.text = userCity + SPACE + userState + SPACE + userCountry
            addressline2!!.setText(userAddressline2 + citydetail!!.text.toString())
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

    /*var addressUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=${
                helper.makeLatLong(mLocation)
}&key=AIzaSyBcOfzuounli5zAWmrSOxxJ5bBmaer47cE"
        viewModel!!.getGoogleAddressAPI(addressUrl, "fatch_google_address")*/
    fun getAddressFromLatLang(latLng: LatLng): String? {

        val geocoder: Geocoder
        var addresses: List<Address>? = null
        geocoder = Geocoder(this, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            var address: String? = ""
            if (addresses!!.size > 0) address =
                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            /*String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();*/return address
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.clear()
        mMap?.setMapType(MAP_TYPE_NORMAL)
        mMap?.getUiSettings()?.isMapToolbarEnabled = false
        mMap?.getUiSettings()?.isZoomControlsEnabled = false
        if (mMap!!.isIndoorEnabled()) {
            mMap?.setIndoorEnabled(false)
        }
        this@LocationPickerActivity.showCurrentLocationOnMap(false)
        doAfterPermissionProvided = 2
        doAfterLocationSwitchedOn = 2
        mMap?.setInfoWindowAdapter(object : InfoWindowAdapter {
            // Use default InfoWindow frame
            override fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            // Defines the contents of the InfoWindow
            override fun getInfoContents(arg0: Marker): View? {
                val v: View = layoutInflater.inflate(R.layout.info_window_layout, null)
                // Getting the position from the marker
                val latLng = arg0.position
                mLatitude = latLng.latitude
                mLongitude = latLng.longitude
                val tvLat = v.findViewById<TextView>(R.id.address)
                tvLat.text = userAddress
                return v
            }
        })
        //        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap?.setOnCameraMoveStartedListener(OnCameraMoveStartedListener { })
        mMap?.setOnCameraIdleListener(OnCameraIdleListener {
            if (!locationPick) {
                mLatitude = mMap!!.getCameraPosition().target.latitude
                mLongitude = mMap!!.getCameraPosition().target.longitude
                val address = getAddressFromLatLang(LatLng(mLatitude, mLongitude))
                if (address != null) {
                    userAddress = address
                    addressline2!!.setText("" + userAddress)
                    imgSearch!!.text = "" + userAddress
                }
            }
        })

        // Setting a click event handler for the map
        mMap?.setOnMapClickListener(OnMapClickListener { latLng ->
            mMap?.clear()
            mLatitude = latLng.latitude
            mLongitude = latLng.longitude
            Log.e("latlng", latLng.toString() + "")
            isZooming = true
            addMarker()
            if (!MapUtility.isNetworkAvailable(this@LocationPickerActivity)) {
                MapUtility.showToast(this@LocationPickerActivity, "Please Connect to Internet")
            }
            this@LocationPickerActivity.getAddressByGeoCodingLatLng()
        })

        doAfterPermissionProvided = 1

    }
    private fun startParsingAddressToShow() {
        //get address from intent to show on map
        if (userAddress == null || userAddress.isEmpty()) {
            //if intent does not have address,
            //cell is blank
//            showCurrentLocationOnMap(false);
        } else  //check if address contains lat long, then extract
        //format will be lat,lng i.e 19.23234,72.65465
            if (latLongPattern.matcher(userAddress).matches()) {
                val p = Pattern.compile("(-?\\d+(\\.\\d+)?)") // the pattern to search for
                val m = p.matcher(userAddress)
                // if we find a match, get the group
                var i = 0
                while (m.find()) {
                    // we're only looking for 2s group, so get it
                    if (i == 0) mLatitude = m.group().toDouble()
                    if (i == 1) mLongitude = m.group().toDouble()
                    i++
                }
                //show on map
                getAddressByGeoCodingLatLng()
                addMarker()
            } else {
                //get  latlong from String address via reverse geo coding
                //Since lat long not present in db
                if (mLatitude == 0.0 && mLongitude == 0.0) {
                    getLatLngByRevGeoCodeFromAdd()
                } else {
                    // Latlong is more accurate to get exact point on map ,
                    // String address might not be sufficient (i.e Mumbai, Mah..etc)
                    addMarker()
                }
            }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble("latitude", mLatitude)
        outState.putDouble("longitude", mLongitude)
        outState.putString("userAddress", userAddress)
        outState.putBundle("addressBundle", addressBundle)
        outState.putDouble("currentLatitude", currentLatitude)
        outState.putDouble("currentLongitude", currentLongitude)
    }
    private fun getAddressByGeoCodingLatLng() {

        //Get string address by geo coding from lat long
        if (mLatitude != 0.0 && mLongitude != 0.0) {
            if (MapUtility.popupWindow != null && MapUtility.popupWindow!!.isShowing) {
                MapUtility.hideProgress()
            }
            Log.d(TAG, "getAddressByGeoCodingLatLng: START")
            //Cancel previous tasks and launch this one
            for (prevTask in filterTaskList) {
                prevTask.cancel(true)
            }
            filterTaskList.clear()
            val asyncTask = GetAddressFromLatLng()
            filterTaskList.add(asyncTask)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mLatitude, mLongitude)
        }
    }

    private fun getLatLngByRevGeoCodeFromAdd() {

        //Get string address by geo coding from lat long
        if (mLatitude == 0.0 && mLongitude == 0.0) {
            if (MapUtility.popupWindow != null && MapUtility.popupWindow!!.isShowing) {
                MapUtility.hideProgress()
            }
            Log.d(TAG, "getLatLngByRevGeoCodeFromAdd: START")
            //Cancel previous tasks and launch this one
            for (prevTask in filterTaskList) {
                prevTask.cancel(true)
            }
            filterTaskList.clear()
            val asyncTask = GetLatLngFromAddress()
            filterTaskList.add(asyncTask)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userAddress)
        }
    }
    override fun onCategoryClick(data: Data?) {
        val intent = Intent()
        // add data into intent and send back to Parent Activity
        intent.putExtra("lift_data", data.toString())
        intent.putExtra("type", type)
        this@LocationPickerActivity.setResult(RESULT_OK, intent)
        finish()
    }
    override fun onDestroy() {
        super.onDestroy()
        for (task in filterTaskList) {
            task.cancel(true)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mLocationPermissionGranted = false
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    mLocationPermissionGranted = true
                }
            }
        }
    }
    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                this@LocationPickerActivity,
                "Location not Available",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest!!,
            locationCallback!!,
            null /* Looper */
        )
            .addOnSuccessListener { Log.d(TAG, "startLocationUpdates: onSuccess: ") }
            .addOnFailureListener { e ->
                if (e is ApiException) {
                    Log.d(TAG, "startLocationUpdates: " + e.message)
                } else {
                    Log.d(TAG, "startLocationUpdates: " + e.message)
                }
            }
    }
    private fun getLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest!!.setInterval(10000)
        locationRequest!!.setFastestInterval(3000)
        locationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetAddressFromLatLng : AsyncTask<Double?, Void?, Bundle?>() {
        var latitude: Double? = null
        var longitude: Double? = null
        override fun onPreExecute() {
            super.onPreExecute()
            MapUtility.showProgress(this@LocationPickerActivity)
        }

        protected override fun doInBackground(vararg p0: Double?): Bundle? {
            return try {
                latitude = p0[0]
                longitude = p0[1]
                val geocoder: Geocoder
                val addresses: List<Address>?
                geocoder = Geocoder(this@LocationPickerActivity, Locale.getDefault())
                val sb = StringBuilder()
                //get location from lat long if address string is null
                addresses = geocoder.getFromLocation(latitude!!, longitude!!, 1)
                if (addresses != null && addresses.size > 0) {
//                    String address = addresses.get(0).getAddressLine(0);
                    val address = userAddress
                    if (address != null) addressBundle!!.putString("addressline2", address)
                    sb.append(address).append(" ")
                    val city = addresses[0].locality
                    if (city != null) addressBundle!!.putString("city", city)
                    sb.append(city).append(" ")
                    val state = addresses[0].adminArea
                    if (state != null) addressBundle!!.putString("state", state)
                    sb.append(state).append(" ")
                    val country = addresses[0].countryName
                    if (country != null) addressBundle!!.putString("country", country)
                    sb.append(country).append(" ")
                    val postalCode = addresses[0].postalCode
                    if (postalCode != null) addressBundle!!.putString("postalcode", postalCode)
                    sb.append(postalCode).append(" ")
                    // return sb.toString();
                    addressBundle!!.putString("fulladdress", sb.toString())
                    addressBundle
                } else {
                    null
                }
            } catch (e: IOException) {
                e.printStackTrace()
                addressBundle!!.putBoolean("error", true)
                addressBundle
                //return roundAvoid(latitude) + "," + roundAvoid(longitude);
            }

            // return bu;
        }

        // setting address into different components
        override fun onPostExecute(userAddress: Bundle?) {
            super.onPostExecute(userAddress)
            this@LocationPickerActivity.userAddress = userAddress!!.getString("addressline2").toString()
            userCity = userAddress.getString("city")!!
            userState = userAddress.getString("state")!!
            userPostalCode = userAddress.getString("postalcode")!!
            userCountry = userAddress.getString("country")!!
            userAddressline2 = userAddress.getString("addressline2")!!
            MapUtility.hideProgress()
            addMarker()
        }
    }

    private inner class GetLatLngFromAddress : AsyncTask<String?, Void?, LatLng>() {
        override fun onPreExecute() {
            super.onPreExecute()
            MapUtility.showProgress(this@LocationPickerActivity)
        }

        protected override fun doInBackground(vararg p0: String?): LatLng? {
            var latLng = LatLng(0.0, .0)
            try {
                val geocoder: Geocoder
                val addresses: List<Address>?
                geocoder = Geocoder(this@LocationPickerActivity, Locale.getDefault())

                //get location from lat long if address string is null
                addresses = geocoder.getFromLocationName(userAddress!![0].toString(), 1)
                if (addresses != null && addresses.size > 0) {
                    latLng = LatLng(addresses[0].latitude, addresses[0].longitude)
                }
            } catch (ignored: java.lang.Exception) {
            }
            return latLng
        }

        override fun onPostExecute(latLng: LatLng) {
            super.onPostExecute(latLng)
            mLatitude = latLng.latitude
            mLongitude = latLng.longitude
            MapUtility.hideProgress()
            addMarker()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {


        if (hasGps) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                0F,
                gpsLocationListener
            )
        }
//------------------------------------------------------//
        if (hasNetwork) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000,
                0F,
                networkLocationListener
            )
        }


        val lastKnownLocationByGps =
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        // locationByGps = getLastKnownLocation()
        lastKnownLocationByGps?.let {
            locationByGps = lastKnownLocationByGps
        }
        //------------------------------------------------------//


        val lastKnownLocationByNetwork =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        lastKnownLocationByNetwork?.let {
            locationByNetwork = lastKnownLocationByNetwork
        }
//------------------------------------------------------//
        if (locationByGps != null && locationByNetwork != null) {
            if (locationByGps!!.accuracy > locationByNetwork!!.accuracy) {

                if (pref.getType().equals("1")) {
                    pref.setToLatL(locationByGps?.latitude.toString())
                    pref.setToLngL(locationByGps?.longitude.toString())
                }else{
                    pref.setToLatM(locationByGps?.latitude.toString())
                    pref.setToLngM(locationByGps?.longitude.toString())
                }
                finish()
            }else{
                if (pref.getType().equals("1")) {
                    pref.setToLatL(locationByNetwork?.latitude.toString())
                    pref.setToLngL(locationByNetwork?.longitude.toString())
                }else{
                    pref.setToLatM(locationByNetwork?.latitude.toString())
                    pref.setToLngM(locationByNetwork?.longitude.toString())
                }
                finish()

            }

        }else {
            if (locationByNetwork == null) {
                Toast.makeText(this@LocationPickerActivity, "No Network", Toast.LENGTH_LONG).show()

            } else {


                if (pref.getType().equals("1")) {
                    pref.setToLatL(locationByNetwork?.latitude.toString())
                    pref.setToLngL(locationByNetwork?.longitude.toString())
                }else{
                    pref.setToLatM(locationByNetwork?.latitude.toString())
                    pref.setToLngM(locationByNetwork?.longitude.toString())
                }
                finish()
            }
            // use latitude and longitude as per your need
            // }
            // }


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
    override fun onBackPressed() {

        if (count.equals("map")) {
            imgSearch?.isVisible = false
            ll_history?.setVisibility(View.VISIBLE)
            txt_showmap?.setVisibility(View.VISIBLE)
            mapFrame?.setVisibility(View.GONE)
            imgCurrentloc?.setVisibility(View.GONE)
            txtSelectLocation?.setVisibility(View.GONE)
            moving_pointer?.setVisibility(View.GONE)
            rl_current_location?.setVisibility(View.VISIBLE)
            count = ""
        }else{
            super.onBackPressed()
        }
    }
}
