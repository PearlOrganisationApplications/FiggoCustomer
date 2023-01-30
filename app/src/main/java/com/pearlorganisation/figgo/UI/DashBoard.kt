package com.pearlorganisation.figgo.UI
//Neeraj
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.pearlorganisation.CurrentBottomNavigationFragment.CurrentMoreFragment
import com.pearlorganisation.CurrentBottomNavigationFragment.CurrentRidesFragment
import com.pearlorganisation.DrawerItemActivity.CancellationPolicy
import com.pearlorganisation.DrawerItemActivity.CurrentAboutActivity
import com.pearlorganisation.DrawerItemActivity.TermAndConditionActivity
import com.pearlorganisation.DrawerSupportActivity
import com.pearlorganisation.Edit_Profile_Activity
import com.pearlorganisation.NotificationBellIconActivity
import com.pearlorganisation.PrefManager
import com.pearlorganisation.figgo.Adapter.CabCategoryAdapter
import com.pearlorganisation.figgo.Adapter.FiggoAddAdapter
import com.pearlorganisation.figgo.BaseClass
import com.pearlorganisation.figgo.Model.CabCategory
import com.pearlorganisation.figgo.Model.FiggoAdd
import com.pearlorganisation.figgo.R
import com.pearlorganisation.figgo.UI.Fragments.HomeDashboard
import com.pearlorganisation.figgo.UI.Fragments.SupportBottomNav
import com.pearlorganisation.figgo.databinding.ActivityDashBoardBinding
import com.pearlorganisation.figgo.databinding.NavHeaderCustomerMenuDrawerBinding
import java.util.*


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class DashBoard : BaseClass(){

    lateinit var binding: ActivityDashBoardBinding
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var textView:TextView
    private val REQUEST_CHECK_SETTINGS: Int=101
    private lateinit var mMap: GoogleMap
    var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=101
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationRequest: LocationRequest
    private val ADDRESS_PICKER_REQUEST = 1
    private var currentLocation: Location? = null
    lateinit var locationManager: LocationManager
    private val requestcodes = 2
    private val permissionId = 2
    private var hasGps = false
    private var hasNetwork = false
    private var locationByGps: Location? = null
    private var locationByNetwork: Location? = null
    private var lastKnownLocationByGps: Location? = null


    lateinit var customerMenuDrawerBinding: NavHeaderCustomerMenuDrawerBinding
    lateinit var continu : Button
    lateinit var main : LinearLayout
    lateinit var perm : LinearLayout
    lateinit var cabCategoryAdapter: CabCategoryAdapter
    var cab_category_list=ArrayList<CabCategory>()
    lateinit var figgoAddAdapter: FiggoAddAdapter
    var figgo_add_list=ArrayList<FiggoAdd>()
    var doubleBackToExitPressedOnce = false
    var count = 0
    var transaction_id :String ?= ""
    var backPressedTime: Long = 0
    var PERMISSIONS_CODE=2

    var to_lat :String ?= ""
    var from_lat :String ?= ""
    var to_lng :String ?= ""
    var from_lng :String ?= ""
    var press : String ?= ""
    var manualLoc: TextView? = null
    var AUTOCOMPLETE_REQUEST_CODE = -1
    var liveLoc: TextView? = null


    var homeFrag = HomeDashboard()
    private var mGoogleApiClient: GoogleApiClient? = null
    var mLastLocation: Location? = null
    var mLocationRequest: com.google.android.gms.location.LocationRequest? = null
    private val mLogout: Button? = null
    private val mRequest: Button? = null
    private val pickupLocation: LatLng? = null
    lateinit var navView : NavigationView
    lateinit var prefManager: PrefManager
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
      setContentView(R.layout.a_dashboard)
        prefManager = PrefManager(this)

        prefManager.setType("")
        prefManager.setToLatL("")
        prefManager.setToLngL("")
        prefManager.setToLatM("")
        prefManager.setToLngM("")
        prefManager.setTypeC("")
        prefManager.setToLatLC("")
        prefManager.setToLngLC("")
        prefManager.setToLatMC("")
        prefManager.setToLngMC("")
        Log.d("token",prefManager.getToken())

        var window=window
        window.setStatusBarColor(Color.parseColor("#000F3B"))
        val drawerLayout = findViewById<View>(R.id.drawerLayout) as DrawerLayout
        var menu_naviagtion = findViewById<ImageView>(R.id.menu_naviagtion)
        val navigationView = findViewById<View>(R.id.navView) as NavigationView
       var  lleditprofile = navigationView.getHeaderView(0).findViewById<View>(R.id.ll_editprofile)
        var tv_rajsharma = navigationView.getHeaderView(0).findViewById<TextView>(R.id.tv_rajsharma)
        var tv_mobilenumber = navigationView.getHeaderView(0).findViewById<TextView>(R.id.tv_mobilenumber)
        var tv_gmail = navigationView.getHeaderView(0).findViewById<TextView>(R.id.tv_gmail)
        var iv_imageView = navigationView.getHeaderView(0).findViewById<ImageView>(R.id.iv_imageView)


        var rides =/* RidesBottom()*/ CurrentRidesFragment()
        var more = /*RoundAndTourFragment()*/ CurrentMoreFragment()
        var support = SupportBottomNav()
        var navView = findViewById<NavigationView>(R.id.navView)
        var shareimg = findViewById<ImageView>(R.id.shareimg)
         main = findViewById<LinearLayout>(R.id.main)
        continu = findViewById<Button>(R.id.continu)
        perm = findViewById<LinearLayout>(R.id.perm)
        val textView = findViewById<TextView>(R.id.tv1)
        var iv_user = findViewById<ImageView>(R.id.iv_user)
        var iv_bellicon = findViewById<ImageView>(R.id.iv_bellicon)

/*
        liveLoc = findViewById<TextView>(R.id.live_loc)
*/


        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["api_key"]
        val apiKey = getString(R.string.api_key)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        val autocompleteSupportFragment1 = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment1) as AutocompleteSupportFragment?

        autocompleteSupportFragment1!!.setPlaceFields(
            listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.PHONE_NUMBER, Place.Field.LAT_LNG, Place.Field.OPENING_HOURS, Place.Field.RATING, Place.Field.USER_RATINGS_TOTAL))


        autocompleteSupportFragment1.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {

                val address = place.address
                val name = place.name
               /* val phone = place.phoneNumber
                val latlng = place.latLng
                val latitude = latlng?.latitude
                val longitude = latlng?.longitude*/

                val isOpenStatus : String = if(place.isOpen == true){
                    "Open"
                } else {
                    "Closed"
                }

              /*  val rating = place.rating
                val userRatings = place.userRatingsTotal*/

               /* textView.text = "Name: $name \nAddress: $address\nIs open: $isOpenStatus \n" */
            }

            override fun onError(status: Status) {
                Toast.makeText(applicationContext,"Some error occurred", Toast.LENGTH_SHORT).show()
            }
        })




       /* tv_rajsharma.text=prefManager.gettv_rajsharma()*/
        tv_mobilenumber.text=prefManager.gettv_mobilenumber()
       /* tv_gmail.text=prefManager.gettv_gmail()*/
        iv_imageView.setImageResource(R.drawable.girl)

        toggle = ActionBarDrawerToggle(this@DashBoard, drawerLayout, R.string.Change_MPIN, R.string.Rides)
        drawerLayout.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()

        iv_user.setOnClickListener {
            startActivity(Intent(this,Edit_Profile_Activity::class.java))
        }

        iv_bellicon.setOnClickListener {
            startActivity(Intent(this,NotificationBellIconActivity::class.java))
        }



     /*   menu_naviagtion.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)

        }*/


        lleditprofile.setOnClickListener {
            startActivity(Intent(this,Edit_Profile_Activity::class.java))
        }


       /* liveLoc?.setOnClickListener {

            press = "live";
            val field = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS,Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field).build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
*/

        continu.setOnClickListener {
           grantLocPer()
            perm.isVisible = false
            main.isVisible = true
        }



        grantLocPer()

       var bottom = findViewById<BottomNavigationView>(R.id.navigation_bar)
        bottom.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home_b->{
                    setfragment(homeFrag)
                    true

                }
                R.id.rides_b->{
                    setfragment(rides)
                    true

                }


                R.id.more_b->{
                    setfragment(more)
                    true

        }
                R.id.support_b->{
            setfragment(support)
            true


        }

                else -> {
                    setfragment(homeFrag)
                        true

                }                }
            }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.change_mpin -> {
                    Toast.makeText(this@DashBoard, "change_mpin Clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.rides -> {
                    Toast.makeText(this@DashBoard, "rides Clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.Logout -> {
                    prefManager.setToken("")
                    prefManager.setMpin("")
                    startActivity(Intent(this,LoginActivity::class.java))
                }

                R.id.term_condition -> {
                    startActivity(Intent(this,TermAndConditionActivity::class.java))
                }
                R.id.cancellation_policy -> {
                    startActivity(Intent(this,CancellationPolicy::class.java))
                }
                R.id.About_Figgo -> {
                    startActivity(Intent(this,CurrentAboutActivity::class.java))
                }
                R.id.Logout -> {
                    startActivity(Intent(this,CurrentAboutActivity::class.java))
                }
                R.id.Support -> {
                    startActivity(Intent(this,DrawerSupportActivity::class.java))
                }
            }
            true
        }

    }

    private fun setfragment(frag: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.framedash, frag)
            commit()
        }}



    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            if (doubleBackToExitPressedOnce) {
                // System.exit(0);
                val a = Intent(Intent.ACTION_MAIN)
                a.addCategory(Intent.CATEGORY_HOME)
                a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(a)
                finish()
                return
            }
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()

            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                doubleBackToExitPressedOnce = false
            }, 2000)
        } else {
            supportFragmentManager.popBackStack()
        }
    }

   private fun requestPermissions() {
       ActivityCompat.requestPermissions(this@DashBoard,
           arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION), permissionId)

   }
    private fun isLocationPermissionGranted(): Boolean {
        return if (ActivityCompat.checkSelfPermission(this@DashBoard, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@DashBoard, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this@DashBoard,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)


           // ActivityCompat.requestPermissions(this@DashBoard, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
              //  requestcodes

            false
        } else {
            true
        }
    }

    fun grantLocPer() {

        if (isLocationPermissionGranted()) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        this@DashBoard,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    checkLocationService()
                } else {
                    ActivityCompat.requestPermissions(
                        this@DashBoard,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                    );
                }
            } else {
                checkLocationService()
            }

        } else {
            ActivityCompat.requestPermissions(
                this@DashBoard,
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
        val client = LocationServices.getSettingsClient(this@DashBoard)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this@DashBoard){it->
            it.locationSettingsStates;
           setfragment(homeFrag)
        }

        task.addOnFailureListener(this@DashBoard) { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult (this@DashBoard, REQUEST_CHECK_SETTINGS)

                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }

            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode== AUTOCOMPLETE_REQUEST_CODE){
            if (resultCode == AppCompatActivity.RESULT_OK){
                val place = Autocomplete.getPlaceFromIntent(data!!)
                if (press.equals("manual")){
                    manualLoc!!.setText(place.address)
                    from_lat = place.latLng.latitude.toString()
                    from_lng = place.latLng.longitude.toString()
                }else if (resultCode == AutocompleteActivity.RESULT_ERROR){
                    val status = Autocomplete.getStatusFromIntent(data!!)
                }else if (resultCode == AppCompatActivity.RESULT_CANCELED){

                }
            }else if (requestCode == REQUEST_CHECK_SETTINGS){
                if (resultCode == Activity.RESULT_OK) {
                    val result = data!!.getStringExtra("result")
                   /* fetchCurrentLocation()*/
                }

                else if (resultCode == Activity.RESULT_CANCELED) {

                }
            }
        }
        if (resultCode == -1) {
           setfragment(homeFrag)
        } else {
            grantLocPer()
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
                    if ((ContextCompat.checkSelfPermission(this@DashBoard,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) ===
                                PackageManager.PERMISSION_GRANTED)) {

                        main.isVisible = true
                        perm.isVisible = false
                           grantLocPer()

                    }
                } else {

                    main.isVisible = false
                    perm.isVisible = true
                }
                return
            }
        }


    }


}