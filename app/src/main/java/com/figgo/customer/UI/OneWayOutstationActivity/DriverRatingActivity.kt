package com.figgo.customer.UI.OneWayOutstationActivity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.figgo.customer.R
import com.figgo.customer.UI.DashBoard
import com.figgo.customer.UI.NotificationBellIconActivity
import com.figgo.customer.pearlLib.BaseClass

class DriverRatingActivity : BaseClass() {
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
        setContentView(R.layout.activity_driver_rating_oneway)
        var iv_bellicon = findViewById<ImageView>(R.id.iv_bellicon)
        var submitraing = findViewById<TextView>(R.id.submitraing)
        shareimg()
        onBackPress()

        submitraing.setOnClickListener {
            startActivity(Intent(this, DashBoard::class.java))
        }

        iv_bellicon.setOnClickListener {
            startActivity(Intent(this, NotificationBellIconActivity::class.java))
        }
    }
}