package com.figgo.customer.UI.AdavanceCityCabActivity

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.figgo.customer.pearlLib.PrefManager
import com.figgo.customer.R
import com.figgo.customer.UI.CityCabActivity
import com.figgo.customer.UI.DashBoard

class ThankyouScreen :  AppCompatActivity() {
    lateinit var pref: PrefManager
    var booking_id: TextView? = null
    var otpText: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.thankyou_screen)
       // var next_button = view.findViewById<TextView>(R.id.next_button)
        var book_other = findViewById<TextView>(R.id.book_other)
        var close = findViewById<TextView>(R.id.close)
        booking_id = findViewById<TextView>(R.id.booking_id)
        otpText = findViewById<TextView>(R.id.otp)
        pref = PrefManager(this@ThankyouScreen)
        var shareimg = findViewById<ImageView>(R.id.shareimg)
        var ll_back = findViewById<LinearLayout>(R.id.ll_back)

        //  otpText?.setText("Otp -"+pref.getOtp()+"")
        booking_id?.setText("Booking No -"+pref.getBookingNo()+"")


        shareimg.setOnClickListener {
            var intent= Intent()
            intent.action= Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT,"I am Inviting you to join  Figgo App for better experience to book cabs")
            intent.setType("text/plain")
            startActivity(Intent.createChooser(intent, "Invite Friends"));
        }

        ll_back.setOnClickListener {
            startActivity(Intent(this@ThankyouScreen, DashBoard::class.java))
        }




       /* next_button.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_thankyouScreenFragment_to_vehicleNumberFragment)
        }*/

        book_other.setOnClickListener {

            startActivity(Intent(this@ThankyouScreen, CityCabActivity::class.java))

        }
        close.setOnClickListener {
            startActivity(Intent(this@ThankyouScreen, DashBoard::class.java))
        }
    }





    override fun onBackPressed() {

        Toast.makeText(this@ThankyouScreen, "Unable to back your money already deducted", Toast.LENGTH_LONG).show()
    }



}