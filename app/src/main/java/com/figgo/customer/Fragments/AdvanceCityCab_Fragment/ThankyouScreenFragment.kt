package com.figgo.customer.Fragments.AdvanceCityCab_Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.figgo.customer.pearlLib.PrefManager
import com.figgo.customer.R
import com.figgo.customer.UI.CityCabActivity
import com.figgo.customer.UI.DashBoard

class ThankyouScreenFragment :  Fragment() {
    lateinit var pref: PrefManager
    var booking_id: TextView? = null
    var otpText: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_thankyou_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var next_button = view.findViewById<TextView>(R.id.next_button)
        var book_other = view.findViewById<TextView>(R.id.book_other)
        var close = view.findViewById<TextView>(R.id.close)
        booking_id = view.findViewById<TextView>(R.id.booking_id)
        otpText = view.findViewById<TextView>(R.id.otp)
        pref = PrefManager(requireActivity())
        var shareimg = view.findViewById<ImageView>(R.id.shareimg)
        var ll_back = view.findViewById<LinearLayout>(R.id.ll_back)

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
            startActivity(Intent(requireActivity(), DashBoard::class.java))
        }




        next_button.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_thankyouScreenFragment_to_vehicleNumberFragment)
        }

        book_other.setOnClickListener {

            startActivity(Intent(requireActivity(), CityCabActivity::class.java))

        }
        close.setOnClickListener {
            startActivity(Intent(requireActivity(), DashBoard::class.java))
        }



    }



}