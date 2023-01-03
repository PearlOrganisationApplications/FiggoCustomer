package com.pearlorganisation.figgo

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.pearlorganisation.PrefManager
import com.pearlorganisation.figgo.Adapter.AdvanceCityDataAdapter
import com.pearlorganisation.figgo.Adapter.OneWayKmCountAdapter
import com.pearlorganisation.figgo.Model.OneWayListRatingVehicle
import com.pearlorganisation.figgo.Model.VehicleInfoList
import com.pearlorganisation.figgo.UI.Fragments.HomeDashboard
import org.json.JSONObject
import java.util.HashMap

class OneWay_Km_CountActivity : AppCompatActivity() {



    lateinit var oneWayKmCountAdapter: OneWayKmCountAdapter
    lateinit var binding:OneWay_Km_CountActivity
    lateinit var pref:PrefManager
    val mList = ArrayList<OneWayListRatingVehicle>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_way_km_count)
        var ll_accept = findViewById<LinearLayout>(R.id.ll_accept)
        val recyclerview = findViewById<RecyclerView>(R.id.onewayvehiclelist)
        var shareimg = findViewById<ImageView>(R.id.shareimg)
        var backimg = findViewById<ImageView>(R.id.backimg)


     /*   getnxtbtn()*/
        backimg.setOnClickListener {
            val intent = Intent(this, HomeDashboard::class.java)
            startActivity(intent)
        }

        shareimg.setOnClickListener {
            var intent= Intent()
            intent.action= Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT,"I am Inviting you to join  Figgo App for better experience to book cabs");
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, "Invite Friends"));
        }

       /* mList.add(OneWayListRatingVehicle("Activa - 2012    Rs.45.00","raingcountlist","ride_service_rating","Reject","Accept"))
        mList.add(OneWayListRatingVehicle("Activa - 2012    Rs.45.00","raingcountlist","ride_service_rating","Reject","Accept"))
        mList.add(OneWayListRatingVehicle("Activa - 2012    Rs.45.00","raingcountlist","ride_service_rating","Reject","accept"))
        mList.add(OneWayListRatingVehicle("Activa - 2012    Rs.45.00","raingcountlist","ride_service_rating","Reject","Accept"))
        mList.add(OneWayListRatingVehicle("Activa - 2012    Rs.45.00","raingcountlist","ride_service_rating","Reject","Accept"))*/

        /*recyclerview.adapter = OneWayKmCountAdapter(this,mList)
        recyclerview.layoutManager = LinearLayoutManager(this)
        oneWayKmCountAdapter = OneWayKmCountAdapter(this,mList)
        recyclerview.adapter = oneWayKmCountAdapter*/


      /*  oneWayKmCountAdapter.onItemClick = {
            val intent = Intent(this,VehicleAboutActivity::class.java)
            *//*intent.putExtra("oneWayKmCountAdapter")*//*
            startActivity(intent)
        }*/
    }

   /* private fun getnxtbtn() {
        val URL = "https://test.pearl-developer.com/figo/api/ride/select-city-vehicle-type"
        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        json.put("vehicle_type_id",pref.getvehicle_type_id())
        json.put("ride_id",pref.getride_id())
        Log.d("SendData", "json===" + json)
        val jsonOblect: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, URL, json, object :
                Response.Listener<JSONObject?>
            {
                @SuppressLint("SuspiciousIndentation")
                override fun onResponse(response: JSONObject?) {
                    Log.d("SendData", "response===" + response)
                    if (response != null) {
                       *//* ll_location?.isVisible = false
                        ll_choose_vehicle?.isVisible  =true*//*

                        val from_location = response.getJSONObject("data").getJSONObject("vehicle").getString("from_location")
                        val image = response.getJSONObject("data").getJSONObject("vehicle").getString("image")
                        val min_price = response.getJSONObject("data").getJSONObject("vehicle").getString("min_price")
                        val max_price = response.getJSONObject("data").getJSONObject("vehicle").getString("max_price")
                        val to_location = response.getJSONObject("data").getJSONObject("vehicle").getString("to_location")
                        val name = response.getJSONObject("data").getJSONObject("vehicle").getString("name")
                        val distance = response.getJSONObject("data").getJSONObject("vehicle").getString("distance")

                        val size = response.getJSONObject("data").getJSONArray("vehicle_types").length()
                        val rideId = response.getJSONObject("data").getString("id")

                        for(p2 in 0 until size) {
                            val name = response.getJSONObject("data").getJSONArray("vehicle_types").getJSONObject(p2).getString("name")
                            val image = response.getJSONObject("data").getJSONArray("vehicle_types").getJSONObject(p2).getString("full_image")
                            val ride_id = response.getJSONObject("data").getJSONArray("vehicle_types").getJSONObject(p2).getString("name")
                            val vehicle_id = response.getJSONObject("data").getString("ride_id")

                        }





                    }
                    // Get your json response and convert it to whatever you want.
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    *//*Toast.makeText(this(), "Something went wrong !!", Toast.LENGTH_LONG).show()*//*
                    Log.d("SendData", "error===" + error)
                    // Error
                }
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> = HashMap()
                    headers.put("Content-Type", "application/json; charset=UTF-8");
                    headers.put("Authorization", "Bearer " + pref.getToken());
                    return headers
                }
            }

        queue.add(jsonOblect)

    }*/

}






