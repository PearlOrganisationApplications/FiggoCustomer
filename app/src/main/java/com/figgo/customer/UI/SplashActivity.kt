package com.figgo.customer.UI
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat

import com.figgo.customer.pearlLib.PrefManager
import com.figgo.customer.R
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStates
import com.google.android.gms.maps.GoogleMap

class SplashActivity : AppCompatActivity() {
    private val REQUEST_CHECK_SETTINGS: Int=101
    private lateinit var locationRequest: LocationRequest
    var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=101

    var prefManager: PrefManager? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window=window
        window.statusBarColor = Color.parseColor("#000F3B")

        setContentView(R.layout.activity_splash)
        prefManager = PrefManager(this)

        var become_the_luxury=findViewById<TextView>(R.id.become_the_luxury)
        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_side)
        become_the_luxury.startAnimation(slideAnimation)
        grantLocPer()
       // checkLocationService()
    }



    fun grantLocPer() {

        if (isLocationPermissionGranted()) {






                checkLocationService()
            }

         else {
            ActivityCompat.requestPermissions(
                this@SplashActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)

        }
    }



    fun checkLocationService() {

        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY


        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        // builder.setAlwaysShow(true);
        val client = LocationServices.getSettingsClient(this@SplashActivity)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this@SplashActivity){it->
            it.locationSettingsStates
            Handler().postDelayed({

                //  if(prefManager!!.isValid().equals("true")){

                //   startActivity(Intent(this, DashBoard::class.java))
                // }else{
                startActivity(Intent(this, WelcomeSlider::class.java))
                //    }

            },3000)
        }

        task.addOnFailureListener(this@SplashActivity) { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                   // grantLocPer()

                    // grantLocPer()
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult (this@SplashActivity, REQUEST_CHECK_SETTINGS)

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


        if (grantResults.isNotEmpty() && grantResults[0] ==
            PackageManager.PERMISSION_GRANTED
        ) {
            checkLocationService()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) ||
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                val dialog = Dialog(this@SplashActivity)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.permission_layout)
                val body = dialog.findViewById(R.id.error) as TextView
                 body.text = "Warning! We are unable to continue if you want then allow permission this time otherwise you have give app permission manually"
               // body.text = title
                val yesBtn = dialog.findViewById(R.id.ok) as Button
                yesBtn.setOnClickListener {
                    dialog.dismiss()
                    ActivityCompat.requestPermissions(
                        this@SplashActivity,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)

                }

                dialog.show()
                val window: Window? = dialog.getWindow()
                window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

            }


        }
    }




    private fun isLocationPermissionGranted(): Boolean {
        return !(ActivityCompat.checkSelfPermission(this@SplashActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@SplashActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val states = LocationSettingsStates.fromIntent(data!!)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                RESULT_OK ->                     // All required changes were successfully made
                   // Toast.makeText(baseContext, "All good", Toast.LENGTH_SHORT).show()

                    Handler().postDelayed({

                        //  if(prefManager!!.isValid().equals("true")){

                        //   startActivity(Intent(this, DashBoard::class.java))
                        // }else{
                        startActivity(Intent(this, WelcomeSlider::class.java))
                        //    }

                    },5000)
                RESULT_CANCELED -> {
                    checkLocationService()
                }
                else -> {}
            }
        }
    }

    override fun onResume(){
        super.onResume()
        grantLocPer()

    }


}