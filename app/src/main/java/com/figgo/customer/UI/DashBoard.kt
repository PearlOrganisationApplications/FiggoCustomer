package com.figgo.customer.UI
//Neeraj
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.figgo.cabs.figgodriver.Fragment.RideHistory
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.figgo.customer.Fragments.CurrentBottomNavigationFragment.CurrentMoreFragment
import com.figgo.customer.pearlLib.PrefManager
import com.figgo.customer.Adapter.CabCategoryAdapter
import com.figgo.customer.Adapter.FiggoAddAdapter
import com.figgo.customer.pearlLib.BaseClass
import com.figgo.customer.Model.CabCategory
import com.figgo.customer.Model.FiggoAdd
import com.figgo.customer.R
import com.figgo.customer.Fragments.HomeDashboard
import com.figgo.customer.Fragments.SupportBottomNav
import com.figgo.customer.databinding.ActivityDashBoardBinding
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


    /*lateinit var customerMenuDrawerBinding: NavHeaderCustomerMenuDrawerBinding*/
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
        prefManager = PrefManager(this@DashBoard)


        Log.d("token",prefManager.getToken())

        var window=window
        window.setStatusBarColor(Color.parseColor("#000F3B"))
       /* val drawerLayout = findViewById<View>(R.id.drawerLayout) as DrawerLayout*/
        var menu_naviagtion = findViewById<ImageView>(R.id.menu_naviagtion)
        val navigationView = findViewById<View>(R.id.navView) as NavigationView




        var rides =/* RidesBottom()*/ /*CurrentRidesFragment()*/ RideHistory()
        var more = /*RoundAndTourFragment()*/ CurrentMoreFragment()
        var support = SupportBottomNav()
        //var navView = findViewById<NavigationView>(R.id.navView)
      //  var shareimg = findViewById<ImageView>(R.id.shareimg)
         main = findViewById<LinearLayout>(R.id.main)
        continu = findViewById<Button>(R.id.continu)
        perm = findViewById<LinearLayout>(R.id.perm)
        var iv_user = findViewById<ImageView>(R.id.iv_user)
        var iv_bellicon = findViewById<ImageView>(R.id.iv_bellicon)
        var ll_cancelationpolicy = findViewById<LinearLayout>(R.id.ll_cancelationpolicy)
        var ll_termsconditions = findViewById<LinearLayout>(R.id.ll_termsconditions)
        var ll_helpsupport = findViewById<LinearLayout>(R.id.ll_helpsupport)
        var ll_editprofile = findViewById<LinearLayout>(R.id.ll_editprofile)
        var ll_orderbooking = findViewById<LinearLayout>(R.id.ll_orderbooking)
        var ll_offeranddiscount = findViewById<LinearLayout>(R.id.ll_offeranddiscount)
        var ll_figgorewards = findViewById<LinearLayout>(R.id.ll_figgorewards)
        var ll_referandearn = findViewById<LinearLayout>(R.id.ll_referandearn)
        var ll_faqs = findViewById<LinearLayout>(R.id.ll_faqs)
        var ll_ratesus = findViewById<LinearLayout>(R.id.ll_ratesus)
        var ll_feedsback = findViewById<LinearLayout>(R.id.ll_feedsback)
        var img_webview = findViewById<ImageView>(R.id.iv_webview)
         liveLoc = findViewById<TextView>(R.id.live_loc)
        var ll_logout = findViewById<LinearLayout>(R.id.ll_logout)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager








     /*   toggle = ActionBarDrawerToggle(this@DashBoard, drawerLayout, R.string.Change_MPIN, R.string.Rides)
        drawerLayout.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()
*/
        iv_user.setOnClickListener {
            startActivity(Intent(this, Edit_Profile_Activity::class.java))
        }

        iv_bellicon.setOnClickListener {
            startActivity(Intent(this, NotificationBellIconActivity::class.java))
        }

        ll_editprofile.setOnClickListener {
            startActivity(Intent(this, Edit_Profile_Activity::class.java))
        }

        ll_orderbooking.setOnClickListener {
            Toast.makeText(this, "Order and booking", Toast.LENGTH_LONG).show()
        }

        ll_offeranddiscount.setOnClickListener {
            Toast.makeText(this, "Order and discount", Toast.LENGTH_LONG).show()
        }

        ll_figgorewards.setOnClickListener {
            Toast.makeText(this, "Figgo rewards", Toast.LENGTH_LONG).show()
        }

        ll_referandearn.setOnClickListener {
            Toast.makeText(this, "Refers and earn", Toast.LENGTH_LONG).show()
        }

        ll_faqs.setOnClickListener {
            Toast.makeText(this, "Faqs", Toast.LENGTH_LONG).show()
        }
        ll_helpsupport.setOnClickListener {
                //Toast.makeText(this, "Faqs", Toast.LENGTH_LONG).show()
            }


        img_webview.setOnClickListener {
            startActivity(Intent(this,GoogleWebview::class.java))
        }
        ll_feedsback.setOnClickListener {
            Toast.makeText(this, "Please give me feed back", Toast.LENGTH_LONG).show()
        }

        ll_ratesus.setOnClickListener {
            startActivity(Intent(this,GoogleWebview::class.java))
        }

        ll_logout.setOnClickListener {
            prefManager.setToken("")
            startActivity(Intent(this,LoginActivity::class.java))
        }





     /*   menu_naviagtion.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)

        }*/




        ll_cancelationpolicy.setOnClickListener {
            startActivity(Intent(this, CancellationPolicy::class.java))
        }

        ll_termsconditions.setOnClickListener {
            startActivity(Intent(this, TermAndConditionActivity::class.java))
        }



       /* liveLoc?.setOnClickListener {

            press = "live";
            val field = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS,Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field).build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
*/

        continu.setOnClickListener {
         //  grantLocPer()
            perm.isVisible = false
            main.isVisible = true
        }


       setfragment(homeFrag)


       var bottom = findViewById<BottomNavigationView>(R.id.navigation_bar)
        var home_top = findViewById<LinearLayout>(R.id.home_top)
        var home_bottom = findViewById<LinearLayout>(R.id.homebottom)
        var

                live_loc = findViewById<TextView>(R.id.live_loc)
        bottom.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home_b->{
                    setfragment(homeFrag)
                    home_top.visibility=View.VISIBLE
                    home_bottom.visibility=View.VISIBLE
                    live_loc.visibility=View.VISIBLE

                    true

                }
                R.id.rides_b->{
                    setfragment(rides)
                    home_top.visibility=View.GONE
                    home_bottom.visibility=View.GONE
                    live_loc.visibility=View.GONE
                    true

                }


                R.id.more_b->{
                    setfragment(more)
                    home_top.visibility=View.GONE
                    home_bottom.visibility=View.GONE
                    live_loc.visibility=View.GONE
                    true

        }
                R.id.support_b->{
            setfragment(support)
                    home_top.visibility=View.GONE
                    home_bottom.visibility=View.GONE
                    live_loc.visibility=View.GONE
            true


        }

                else -> {
                    setfragment(homeFrag)
                        true

                }                }
            }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)


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


                    to_lat = locationByGps?.latitude.toString()
                    to_lng = locationByGps?.longitude.toString()

            }else{

                to_lat = locationByNetwork?.latitude.toString()
                    to_lng = locationByNetwork?.longitude.toString()

            }
        }else {
            if (locationByNetwork == null) {
               // Toast.makeText(this, "No Network", Toast.LENGTH_LONG).show()

            } else {



                to_lat = locationByNetwork?.latitude.toString()
                to_lng = locationByNetwork?.longitude.toString()


            }
            // use latitude and longitude as per your need
            // }
            // }


        }
        var geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(this, Locale.getDefault())


        var strAdd : String? = null
        try {
            val addresses = geocoder.getFromLocation(to_lat!!.toDouble(), to_lng!!.toDouble(), 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = java.lang.StringBuilder("")
               /* for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }*/
                val cityState = returnedAddress.locality +","+returnedAddress.adminArea;
                strAdd = cityState
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
    override fun onResume(){
        super.onResume()
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
        prefManager.setSearchBack("")
        getLocation()
    }

}