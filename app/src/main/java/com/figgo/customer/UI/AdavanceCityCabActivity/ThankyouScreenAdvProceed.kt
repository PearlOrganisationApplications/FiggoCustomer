package com.figgo.customer.UI.AdavanceCityCabActivity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.figgo.customer.pearlLib.PrefManager
import com.figgo.customer.R
import com.figgo.customer.UI.CityCabActivity
import com.figgo.customer.UI.DashBoard
import com.figgo.customer.Util.MapUtility
import com.figgo.customer.pearlLib.Helper
import org.json.JSONObject

class ThankyouScreenAdvProceed :  AppCompatActivity() {
    lateinit var pref: PrefManager
    var booking_id: TextView? = null
    var otpText: TextView? = null
    var ride_service_rating: RatingBar? = null
    var figgo_service_rating: RatingBar? = null

    var rating :Float? = 0.0f
    var rating2 :Float? = 0.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.thankyou_screen_proceed)
       // var next_button = view.findViewById<TextView>(R.id.next_button)
        var later = findViewById<TextView>(R.id.later)
        var submit = findViewById<TextView>(R.id.submit)
        booking_id = findViewById<TextView>(R.id.booking_id)
        ride_service_rating = findViewById<RatingBar>(R.id.ride_service_rating)
        figgo_service_rating = findViewById<RatingBar>(R.id.figgo_service_rating)
        pref = PrefManager(this@ThankyouScreenAdvProceed)
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
            startActivity(Intent(this@ThankyouScreenAdvProceed, DashBoard::class.java))
        }




       /* next_button.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_thankyouScreenFragment_to_vehicleNumberFragment)
        }*/

        submit?.setOnClickListener {
            rating = ride_service_rating?.rating
            rating2 = figgo_service_rating?.rating


            if (rating!! > 1.0){

            }else{
                rating = 1.0f
            }
            if (rating2!! > 1.0){

            }else{
                rating2 = 1.0f
            }
            postSubmitRating()

            //  Log.d("Thankyou_RatingCityCab","rating   "+rating)
        }

        later.setOnClickListener {
            startActivity(Intent(this@ThankyouScreenAdvProceed, DashBoard::class.java))
        }
    }
    private fun postSubmitRating() {
        val progressDialog = ProgressDialog(this)
        progressDialog.show()
        val URL = Helper.SUBMIT_RATING
        //   Log.d("searchDriver", "json===" +URL )
        //  Log.d("SendData", pref.getride_id() )
        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        json.put("driver_id", pref.getdriver_id())
        //  json.put("driver_id", 646)
        json.put("driver_rating", rating)
        json.put("figo_rating", rating2)
        // json.put("ride_id", "1070")
        val jsonOblect: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST, URL, json,
                Response.Listener<JSONObject?> { response ->
                    if (response != null) {
                        try {

                            progressDialog.hide()
                            val status = response.getString("status")

                            if (status.equals("true")) {

                                startActivity(
                                    Intent(
                                        this@ThankyouScreenAdvProceed,
                                        DashBoard::class.java
                                    )
                                )




                            } else if (status.equals("false")) {
                                //  Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()


                            }

                        }catch (e:Exception){
                            MapUtility.showDialog(e.toString(),this@ThankyouScreenAdvProceed)

                        }
                    }
                }, object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {

                        Log.d("SendData", "error===" + error)
                        //

                        MapUtility.showDialog(error.toString(),this@ThankyouScreenAdvProceed)
                    }
                }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> = java.util.HashMap()
                    headers.put("Content-Type", "application/json; charset=UTF-8");
                    headers.put("Authorization", "Bearer " + pref.getToken());
                    headers.put("Accept", "application/vnd.api+json");
                    return headers
                }
            }

        queue.add(jsonOblect)

    }





    override fun onBackPressed() {

        Toast.makeText(this@ThankyouScreenAdvProceed, "Unable to back your money already deducted", Toast.LENGTH_LONG).show()
    }



}