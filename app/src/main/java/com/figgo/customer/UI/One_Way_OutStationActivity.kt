package com.figgo.customer.UI

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.figgo.customer.R
import com.figgo.customer.UI.CurrentMap.MapsActivity1


class One_Way_OutStationActivity : AppCompatActivity() {

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_way_out_station)

        var window=window
        window.setStatusBarColor(Color.parseColor("#000F3B"))

        var ll_oneway = findViewById<LinearLayout>(R.id.ll_oneway)
        var ll_freedom = findViewById<LinearLayout>(R.id.ll_freedom)
        var ll_roundtour  = findViewById<LinearLayout>(R.id.ll_roundtour)
        var oneway  = findViewById<CardView>(R.id.oneway)
        var roundandtour  = findViewById<CardView>(R.id.roundandtour)
        var freedomoneway  = findViewById<CardView>(R.id.freedomoneway)
        var iv_freedom  = findViewById<ImageView>(R.id.iv_freedom)
        var iv_oneway  = findViewById<ImageView>(R.id.iv_oneway)
        var iv_round  = findViewById<ImageView>(R.id.iv_round)
       // var submitoutstation = findViewById<LinearLayout>(R.id.submitoutstation)
        var shareimg = findViewById<ImageView>(R.id.shareimg)
        var backimg = findViewById<ImageView>(R.id.backimg)
       /* var ll_submitfeedom = findViewById<LinearLayout>(R.id.ll_submitfeedom)
        var ll_submitroundtour = findViewById<LinearLayout>(R.id.ll_submitroundtour)*/


       /* backimg.setOnClickListener {
            val intent = Intent(this, HomeDashboard::class.java)
            startActivity(intent)
        }

        shareimg.setOnClickListener {
            var intent= Intent()
            intent.action= Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT,"I am Inviting you to join  Figgo App for better experience to book cabs");
            intent.setType("text/plain")
            startActivity(Intent.createChooser(intent, "Invite Friends"));
        }*/

      /*  submitoutstation.setOnClickListener {
            startActivity(Intent(this, MapsActivity1::class.java))
        }*/

       /* ll_submitroundtour.setOnClickListener {
            startActivity(Intent(this,MapsActivity1::class.java))
        }*/





        oneway.setOnClickListener {
            ll_oneway.isVisible = true
            ll_freedom.isVisible = false
            ll_roundtour.isVisible = false
            iv_oneway.setBackgroundColor(resources.getColor(R.color.coloryellow))
            iv_round.setBackgroundColor(resources.getColor(R.color.white))
            iv_freedom.setBackgroundColor(resources.getColor(R.color.white))


        }

        roundandtour.setOnClickListener {

            ll_oneway.isVisible = false
            ll_freedom.isVisible = true
            ll_roundtour.isVisible = false

            iv_oneway.setBackgroundColor(resources.getColor(R.color.white))
            iv_round.setBackgroundColor(resources.getColor(R.color.coloryellow))
            iv_freedom.setBackgroundColor(resources.getColor(R.color.white))

        }
        freedomoneway.setOnClickListener {

            ll_oneway.isVisible = false
            ll_freedom.isVisible = false
            ll_roundtour.isVisible = true
            iv_oneway.setBackgroundColor(resources.getColor(R.color.white))
            iv_round.setBackgroundColor(resources.getColor(R.color.white))
            iv_freedom.setBackgroundColor(resources.getColor(R.color.coloryellow))


        }
      //  var nav_host_fragment=supportFragmentManager.findFragmentById(R.id.navigation_bar2) as NavHostFragment
      //  nav_controller=nav_host_fragment.navController


    }
}