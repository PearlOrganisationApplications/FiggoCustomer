package com.figgo.customer.UI.OneWayOutstationActivity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.figgo.customer.Adapter.OneWay_Adapter

import com.figgo.customer.Model.OneWayvehiclelistModel

import com.figgo.customer.R
import com.figgo.customer.UI.NotificationBellIconActivity
import com.figgo.customer.Util.MapUtility
import com.figgo.customer.pearlLib.BaseClass
import com.figgo.customer.pearlLib.PrefManager

class BookingDestinatiion: BaseClass() {
    lateinit var pref:PrefManager
    lateinit var onewayOutstationadapter: OneWay_Adapter
    lateinit var recyclerview_vehicleoutstation: RecyclerView

    val vehicleoutstatiion = ArrayList<OneWayvehiclelistModel>()

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

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_destinatiion_oneway)
        pref = PrefManager(this@BookingDestinatiion)
        var iv_bellicon = findViewById<ImageView>(R.id.iv_bellicon)
        var date = findViewById<TextView>(R.id.date)
        var time = findViewById<TextView>(R.id.time)
        var pick_location = findViewById<TextView>(R.id.pick_location)
        var drop_location = findViewById<TextView>(R.id.drop_location)
      //  var iv_bellicon = findViewById<ImageView>(R.id.iv_bellicon)
         recyclerview_vehicleoutstation = findViewById<RecyclerView>(R.id.recyclerview_vehicleoutstation)

        pick_location.setText(pref.getLiveLoc())
        drop_location.setText(pref.getManualLoc())
        date.setText(pref.getDate())
        time.setText(pref.getTime())

        shareimg()
        onBackPress()


        setVehicleAdapter()
        iv_bellicon.setOnClickListener {
            startActivity(Intent(this, NotificationBellIconActivity::class.java))
        }





/*
        vehicleoutstatiion.add(OneWayvehiclelistModel("Mini","mini sedan","RS 1890"))
        vehicleoutstatiion.add(OneWayvehiclelistModel("Mini","mini sedan","RS 1650"))
        vehicleoutstatiion.add(OneWayvehiclelistModel("Sedan daily","sedan daily","RS 1224"))
        vehicleoutstatiion.add(OneWayvehiclelistModel("Sedan prime","sedan prime","RS 2214"))
        vehicleoutstatiion.add(OneWayvehiclelistModel("Sedan luxry","sedan luxry","RS 1450"))
        vehicleoutstatiion.add(OneWayvehiclelistModel("Sedan luxry","sedan luxry","RS 1350"))
        vehicleoutstatiion.add(OneWayvehiclelistModel("Sedan daily","sedan daily","RS 1350"))
        vehicleoutstatiion.add(OneWayvehiclelistModel("Mini","mini sedan","RS 1224"))
        vehicleoutstatiion.add(OneWayvehiclelistModel("Mini","mini sedan","RS 1224"))
        vehicleoutstatiion.add(OneWayvehiclelistModel("Sedan daily","sedan daily","RS 1224"))*/



    }


    private fun setVehicleAdapter(){
        onewayOutstationadapter= OneWay_Adapter(this,MapUtility.cablistOneWay)
        recyclerview_vehicleoutstation.adapter=onewayOutstationadapter
        /*recyclerview_vehiclelist.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true))*/
        recyclerview_vehicleoutstation.layoutManager= LinearLayoutManager(this@BookingDestinatiion)


    }

    override fun onBackPressed() {

        finish()
      //  startActivity(Intent(this@BookingDestinatiion, On::class.java))
    }

}