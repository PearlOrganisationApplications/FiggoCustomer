package com.pearlorganisation.figgo.CurrentMap

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.pearlorganisation.PrefManager
import com.pearlorganisation.figgo.BaseClass
import com.pearlorganisation.figgo.R
import com.pearlorganisation.figgo.databinding.ActivityMaps2Binding
import com.squareup.picasso.Picasso
import org.json.JSONObject

class MapsActivity2 : BaseClass(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMaps2Binding
    lateinit var pref:PrefManager

    var activavehiclenumber:TextView? = null
    var dl_number:TextView? = null
    var drivername:TextView? = null
    var mobilenumber:TextView? = null
    var activaimg:ImageView? = null
    var  driverimg:ImageView? = null
    var ride_service_rating:RatingBar? = null
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
        binding = ActivityMaps2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        pref  = PrefManager(this)
        shareimg()
        onBackPress()

        var accept = findViewById<TextView>(R.id.accept)

         activaimg = findViewById<ImageView>(R.id.activaimg)
         activavehiclenumber = findViewById<TextView>(R.id.activavehiclenumber)
         drivername = findViewById<TextView>(R.id.drivername)

         ride_service_rating = findViewById<RatingBar>(R.id.ride_service_rating)
         dl_number = findViewById<TextView>(R.id.dl_number)
        driverimg = findViewById<ImageView>(R.id.driverimg)


        getmaps()

        accept.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("drivername", drivername?.text.toString())
            bundle.putString("activavehiclenumber", activavehiclenumber?.text.toString())
            bundle.putString("dl_number", dl_number?.text.toString())
            val intent = Intent(this, EmergencyMapsActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
                val URL = "https://test.pearl-developer.com/figo/api/ride/select-driver"
                Log.d("SendData", "URL===" + URL)
                val queue = Volley.newRequestQueue(this)
                val json = JSONObject()
                json.put("ride_id",pref.getride_id())
                json.put("driver_id",pref.getdriver_id())
                json.put("price",pref.getprice())
                Log.d("SendData", "pref.getToken()===" + pref.getToken())
                Log.d("SendData", "json===" + json)
                val jsonOblect: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URL, json, object :
                    Response.Listener<JSONObject?>               {
                    override fun onResponse(response: JSONObject?) {
                        Log.d("SendData", "response===" + response)
                        if (response != null) {
                            val status = response.getString("status")
                            if(status.equals("false")){
                                Toast.makeText(this@MapsActivity2, "Something Went Wrong!", Toast.LENGTH_LONG).show()
                            }else{


                            }
                        }

                    }
                }, object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {
                        Log.d("SendData", "error===" + error)
                        Toast.makeText(this@MapsActivity2, "Something Went Wrong!", Toast.LENGTH_LONG).show()
                    }
                }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers: MutableMap<String, String> = java.util.HashMap()
                        headers.put("Content-Type", "application/json; charset=UTF-8")
                        headers.put("Authorization", "Bearer " + pref.getToken())
                        return headers
                    }
                }
                jsonOblect.setRetryPolicy(DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))

                queue.add(jsonOblect)

            }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val myLocation = LatLng(30.302810, 78.012234)
        mMap.addMarker(MarkerOptions().position(myLocation).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        TODO("Not yet implemented")
    }

    private fun getmaps() {
        val progressDialog = ProgressDialog(this)
        progressDialog.show()
        val URL ="https://test.pearl-developer.com/figo/api/ride/get-driver"
        Log.d("SendData", "URL===" + URL)
        val queue = Volley.newRequestQueue(this)
        val json = JSONObject()
        json.put("driver_id", pref.getdriver_id())
        Log.d("SendData", "json===" + json)
        val jsonOblect: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URL, json, object : Response.Listener<JSONObject?>{
                @SuppressLint("SuspiciousIndentation")
                override fun onResponse(response: JSONObject?) {
                    Log.d("SendData", "response===" + response)

                    if (response != null) {
                        progressDialog.hide()
                        val dataobject = response.getJSONObject("data")
                        val driverObject = dataobject.getJSONObject("driver")
                        val driverName = driverObject.getString("name")
                        val dlNumber = driverObject.getString("dl_number")
                        val rating = driverObject.getString("rating_avg")
                        val vNumber = dataobject.getString("v_number")
                        val docObject = driverObject.getJSONObject("documents")
                        val driver_image = docObject.getString("driver_image")
                        val taxi_image = docObject.getString("taxi_image")
                        activavehiclenumber?.setText(vNumber)
                        drivername?.setText(driverName)
                        dl_number?.setText(dlNumber)
                        ride_service_rating?.rating = rating.toFloat()

                        if(!driver_image.equals("")){
                            Picasso.get().load(driver_image).placeholder(R.drawable.girl_img).into(driverimg)
                        }

                        if(!taxi_image.equals("")){
                            Picasso.get().load(taxi_image).placeholder(R.drawable.blueactiva_img).into(activaimg)
                        }

                    }

                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("SendData", "error===" + error)
                    Toast.makeText(this@MapsActivity2, "Something went wrong!", Toast.LENGTH_LONG).show()

                }
            }) {
                @SuppressLint("SuspiciousIndentation")
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> = HashMap()
                    headers.put("Content-Type", "application/json; charset=UTF-8")
                    headers.put("Authorization", "Bearer " + pref.getToken())
                    return headers
                }
            }

        queue.add(jsonOblect)

    }
}

private fun Intent.putExtra(s: String, drivername: TextView?) {


}