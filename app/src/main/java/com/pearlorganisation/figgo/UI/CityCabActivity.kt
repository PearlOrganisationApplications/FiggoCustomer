package com.pearlorganisation.figgo.UI

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.pearlorganisation.DrawerItemActivity.CancellationPolicy
import com.pearlorganisation.DrawerItemActivity.CurrentAboutActivity
import com.pearlorganisation.DrawerItemActivity.TermAndConditionActivity
import com.pearlorganisation.DrawerSupportActivity
import com.pearlorganisation.Edit_Profile_Activity
import com.pearlorganisation.PrefManager
import com.pearlorganisation.figgo.BaseClass
import com.pearlorganisation.figgo.R

class CityCabActivity : BaseClass() {
    private lateinit var navController: NavController
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var prefManager: PrefManager
   /* lateinit var prefManager: PrefManager*/
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
        setContentView(R.layout.drawer_screen2)
        prefManager = PrefManager(this)
        shareimg()
        var shareimg=findViewById<ImageView>(R.id.shareimg)
        val drawerLayout = findViewById<View>(R.id.drawerLayout) as DrawerLayout
        var menu_naviagtion = findViewById<ImageView>(R.id.menu_naviagtion)
        val navigationView = findViewById<View>(R.id.navView) as NavigationView
        var navView = findViewById<NavigationView>(R.id.navView)
        var  lleditprofile = navigationView.getHeaderView(0).findViewById<View>(R.id.ll_editprofile)
        var tv_rajsharma = navigationView.getHeaderView(0).findViewById<TextView>(R.id.tv_rajsharma)
        var tv_mobilenumber = navigationView.getHeaderView(0).findViewById<TextView>(R.id.tv_mobilenumber)
        var tv_gmail = navigationView.getHeaderView(0).findViewById<TextView>(R.id.tv_gmail)
        var iv_imageView = navigationView.getHeaderView(0).findViewById<ImageView>(R.id.iv_imageView)

      /*  tv_rajsharma.text=prefManager.gettv_rajsharma()
        tv_gmail.text=prefManager.gettv_gmail()
        */
        tv_mobilenumber.text=prefManager.gettv_mobilenumber()
        iv_imageView.setImageResource(R.drawable.girl)

        prefManager = PrefManager(this@CityCabActivity)
        menu_naviagtion.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.Change_MPIN, R.string.Rides)
        drawerLayout.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        menu_naviagtion.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }


        lleditprofile.setOnClickListener {
            startActivity(Intent(this, Edit_Profile_Activity::class.java))
        }

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.change_mpin -> {
                    Toast.makeText(this, "change_mpin Clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.rides -> {
                    Toast.makeText(this, "rides Clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.Logout -> {
                    prefManager.setToken("")
                    prefManager.setMpin("")
                    startActivity(Intent(this,LoginActivity::class.java))
                }

                R.id.term_condition -> {
                    startActivity(Intent(this, TermAndConditionActivity::class.java))
                }
                R.id.cancellation_policy -> {
                    startActivity(Intent(this, CancellationPolicy::class.java))
                }
                R.id.About_Figgo -> {
                    startActivity(Intent(this, CurrentAboutActivity::class.java))
                }
                R.id.Support -> {
                    startActivity(Intent(this, DrawerSupportActivity::class.java))
                }

            }
            true
        }


       /* callicon.setOnClickListener {
            // val callIntent = Intent(Intent.ACTION_CALL)
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:+123")
            callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(callIntent)
        }*/


      /*  shareimg.setOnClickListener {
            var intent= Intent()
            intent.action= Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT,"I am Inviting you to join  Figgo App for better experience to book cabs")
            intent.setType("text/plain")
            startActivity(Intent.createChooser(intent, "Invite Friends"))
        }*/

        var window=window
        window.setStatusBarColor(Color.parseColor("#000F3B"))
        val navHostFragment=supportFragmentManager.findFragmentById(R.id.mainContainer) as NavHostFragment
        navController=navHostFragment.navController
        val bottomNavigationView=findViewById<BottomNavigationView>(R.id.navigation_bar)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

    }



    override fun onBackPressed() {

        if (prefManager.getCount().equals("vehicle")) {
            startActivity(Intent(this@CityCabActivity, CityCabActivity::class.java))
        } else {
            super.onBackPressed()
            startActivity(Intent(this, DashBoard::class.java))
        }
    }

}