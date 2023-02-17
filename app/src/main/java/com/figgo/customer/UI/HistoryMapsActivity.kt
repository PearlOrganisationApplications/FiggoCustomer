package com.figgo.customer.UI

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.figgo.customer.R
import com.figgo.customer.Util.MapUtility
import com.figgo.customer.pearlLib.BaseClass

class HistoryMapsActivity : BaseClass() {
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
        setContentView(R.layout.activity_history_maps)
        var shareimg = findViewById<ImageView>(R.id.shareimg)
        var backimg = findViewById<ImageView>(R.id.backimg)
        var booking_idtxt = findViewById<TextView>(R.id.booking_id)
        var to_loctxt = findViewById<TextView>(R.id.to_loc)
        var from_loctxt = findViewById<TextView>(R.id.from_loc)
        var statusTxt = findViewById<TextView>(R.id.status)

        var dateTxt = findViewById<TextView>(R.id.date)
        var timeTxt = findViewById<TextView>(R.id.time)
        var iv_bellicon = findViewById<ImageView>(R.id.iv_bellicon)

        shareimg()
        onBackPress()


        val pos = intent.getIntExtra("position",-1)

        val hashmap = MapUtility.paramMap.get(pos-1)


        val booking_id = hashmap?.get("booking_id")
        val to_loc = hashmap?.get("to_loc")
        val from_loc = hashmap?.get("from_loc")
        val status = hashmap?.get("status")
        val date = hashmap?.get("date")
        val time = hashmap?.get("time")

        booking_idtxt.setText(booking_id)
        to_loctxt.setText(to_loc)
        from_loctxt.setText(from_loc)
        statusTxt.setText(status)
        dateTxt.setText(date)
        timeTxt.setText(time)


        iv_bellicon.setOnClickListener {
            startActivity(Intent(this, NotificationBellIconActivity::class.java))
        }

    }
}