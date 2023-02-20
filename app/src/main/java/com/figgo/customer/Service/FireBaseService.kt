package com.figgo.cabs.figgodriver.Service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat

import com.figgo.customer.R
import com.figgo.customer.pearlLib.PrefManager
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.*

class FireBaseService : Service() {
    private val REQUEST_LOCATION = 1
    private var mGoogleApiClient: GoogleApiClient? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var lat=0.0
    var addressName:String=""
    var lon=0.0
    var rideID = ""
    private var originLatitude: Double = 0.0
    private var originLongitude: Double = 0.0
    lateinit var geocoder: Geocoder
    lateinit var prefManager: PrefManager


    lateinit var address:List<Address>
    //var bi = Intent(MyService.COUNTDOWN_BR)
    //var cdt: CountDownTimer? = null
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun onStartCommand(
        intent: Intent,
        flags: Int,
        startId: Int
    ): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()



        prefManager= PrefManager(applicationContext)
rideID = prefManager.getride_id()
        scope.launch(Dispatchers.IO) {  latlong() }

        // Toast.makeText(applicationContext,"sucess",Toast.LENGTH_SHORT).show()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startMyOwnForeground() else startForeground(
            1,
            Notification()
        )
    }


    private suspend fun latlong() {
        val database = FirebaseDatabase.getInstance()
        val customerRef = database.getReference("$rideID customer")
        val driverRef = database.getReference("$rideID driver")
        while(true) {
            setCurrentLatLon()
            customerRef.child("LAT ").setValue("$originLatitude")
            customerRef.child("LON ").setValue("$originLongitude")
            //setCurrentLatLon()
            driverRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var LAT = snapshot.child("LAT ").value
                    var LON = snapshot.child("LON ").value

                    try {
                        Log.d("XXXX","$LAT $LON")
                        prefManager.setDriverlocation(LAT.toString().toFloat(),LON.toString().toFloat())
                    }
                    catch (e:Exception){

                    }


                    //Log.d("$this", "Lat = $LAT ,Lon = $LON")
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

            delay(10000)
        }
    }
        override fun onBind(intent: Intent): IBinder? {
            return null
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private fun startMyOwnForeground() {
            val NOTIFICATION_CHANNEL_ID = "com.example.simpleapp"
            val channelName = "My Background Service"
            val chan = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(chan)
            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            val notification: Notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
            startForeground(2, notification)
            //    Log.d("SEND DATA","Response service==="+channelName)
        }

        companion object {
            private const val TAG = "BroadcastService"
            const val COUNTDOWN_BR = "your_package_name.countdown_br"

    }


    fun setCurrentLatLon(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

            override fun isCancellationRequested() = false
        })
            .addOnSuccessListener { location: android.location.Location? ->
                if (location == null)
                    Toast.makeText(this, "Cannot get location.", Toast.LENGTH_SHORT).show()
                else {
                    originLatitude = location.latitude
                    prefManager.setlatitude(originLatitude.toFloat())
                    originLongitude = location.longitude
                    prefManager.setlongitude(originLongitude.toFloat())
                }
            }
    }
}