package com.figgo.customer.UI.OneWayOutstationActivity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout

import com.figgo.customer.R
import com.figgo.customer.UI.NotificationBellIconActivity
import com.figgo.customer.pearlLib.BaseClass

class EmergencyMapsActivity : BaseClass() {
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
        setContentView(R.layout.activity_emergency_maps_oneway)
        var ll_emergency = findViewById<LinearLayout>(R.id.ll_emergency)

        var iv_bellicon = findViewById<ImageView>(R.id.iv_bellicon)
        shareimg()
        onBackPress()

        iv_bellicon.setOnClickListener {
            startActivity(Intent(this, NotificationBellIconActivity::class.java))
        }

        ll_emergency.setOnClickListener {
            startActivity(Intent(this, DriverRatingActivity::class.java))
        }

    }
}