package com.figgo.customer.UI

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.figgo.customer.R
import com.figgo.customer.UI.CityCabActivity
import com.figgo.customer.UI.DashBoard
import com.figgo.customer.Util.MapUtility
import com.figgo.customer.pearlLib.BaseClass
import com.figgo.customer.pearlLib.Helper
import com.figgo.customer.pearlLib.PrefManager
import com.squareup.picasso.Picasso
import org.json.JSONObject


class Thankyou_RatingCityCab : BaseClass() {
    lateinit var pref: PrefManager
    var booking_id: TextView? = null
    var otpText: TextView? = null
    var next_button: TextView? = null
    var book_other: TextView? = null
    var tv_submit: TextView? = null
    var tv_later: TextView? = null
    var close: TextView? = null
    var shareimg1: ImageView? = null
    var ll_back: LinearLayout? = null
    var ride_service_rating: RatingBar? = null
    var figgo_service_rating: RatingBar? = null

    var rating :Float? = 0.0f
    var rating2 :Float? = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pref = PrefManager(this@Thankyou_RatingCityCab)
        setLayoutXml()
        initializeViews()
        initializeInputs()
        initializeClickListners()


    }

    override fun setLayoutXml() {
        setContentView(R.layout.fragment_thankyou__rating_)
    }

    override fun initializeViews() {
        //  next_button = findViewById<TextView>(R.id.next_button)
        tv_submit = findViewById<TextView>(R.id.tv_submit)
        tv_later = findViewById<TextView>(R.id.tv_later)
      //  close = findViewById<TextView>(R.id.close)
      //  booking_id = findViewById<TextView>(R.id.booking_id)
      //  otpText = findViewById<TextView>(R.id.otp)

        // ll_back = findViewById<LinearLayout>(R.id.ll_back)
         ride_service_rating = findViewById<RatingBar>(R.id.ride_service_rating)
        figgo_service_rating = findViewById<RatingBar>(R.id.figgo_service_rating)


    }

    @SuppressLint("SuspiciousIndentation")
    override fun initializeClickListners() {




        tv_submit?.setOnClickListener {
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



        tv_later?.setOnClickListener {

            startActivity(
                Intent(
                    this@Thankyou_RatingCityCab,
                    DashBoard::class.java
                )
            )
            //  Log.d("Thankyou_RatingCityCab","rating   "+rating)
        }


      /*  book_other?.setOnClickListener {

            startActivity(Intent(this@Thankyou_RatingCityCab, CityCabActivity::class.java))

        }
        close?.setOnClickListener {
            startActivity(Intent(this@Thankyou_RatingCityCab, DashBoard::class.java))
        }*/


    }

    override fun initializeInputs() {
      //  otpText?.setText("Otp -" + pref.getOtp() + "")
     //   booking_id?.setText("Booking No -" + pref.getBookingNo() + "")
    }

    override fun initializeLabels() {
        TODO("Not yet implemented")
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
                                        this@Thankyou_RatingCityCab,
                                        DashBoard::class.java
                                    )
                                )




                            } else if (status.equals("false")) {
                                //  Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()


                            }

                        }catch (e:Exception){
                            MapUtility.showDialog(e.toString(),this@Thankyou_RatingCityCab)

                        }
                    }
                }, object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {

                        Log.d("SendData", "error===" + error)
                        //

                        MapUtility.showDialog(error.toString(),this@Thankyou_RatingCityCab)
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

        Toast.makeText(applicationContext, "Unable to back your money already deducted", Toast.LENGTH_LONG).show()
    }

}