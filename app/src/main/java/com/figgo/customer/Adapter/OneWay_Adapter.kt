package com.figgo.customer.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley


import com.figgo.customer.Model.OneWayvehiclelistModel



import com.figgo.customer.R
import com.figgo.customer.UI.CurrentCityCabActivity.SearchDriver
import com.figgo.customer.UI.OneWayOutstationActivity.DriverDetails
import com.figgo.customer.Util.MapUtility
import com.figgo.customer.pearlLib.Helper
import com.figgo.customer.pearlLib.PrefManager
import com.squareup.picasso.Picasso
import org.json.JSONObject

class OneWay_Adapter(var context: Context, var vehicleoutstatiion: ArrayList<OneWayvehiclelistModel>): RecyclerView.Adapter<OneWay_Adapter.VehicleHolder>()  {
    var row_index = -1
    lateinit var pref: PrefManager
    lateinit var progressDialog:ProgressDialog
    class VehicleHolder(itemview: View): RecyclerView.ViewHolder(itemview){

        var tv_minivan = itemview.findViewById<TextView>(R.id.tv_minivan)
        var veh_img = itemview.findViewById<ImageView>(R.id.veh_img)
        var tv_price = itemview.findViewById<TextView>(R.id.tv_price)
        var linear=itemview.findViewById<LinearLayout>(R.id.linear)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OneWay_Adapter.VehicleHolder {

        return OneWay_Adapter.VehicleHolder(LayoutInflater.from(parent.context).inflate(R.layout.vehiclelist_onewayoutstation, parent, false))
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: VehicleHolder, position: Int) {
        pref = PrefManager(context)

        var data = vehicleoutstatiion[position]

        holder.tv_minivan.text = data.name
        holder.tv_price.text = "RS." + data.max
        //  holder.tv_vehicletype.text = data.tv_vehicletype
        Picasso.get().load(data.image).into(holder.veh_img)
        holder.itemView.setOnClickListener {


            row_index = position
         pref.setride_id(data.ride_id)
        progressDialog = ProgressDialog(context)
        progressDialog.show()
        val queue = Volley.newRequestQueue(context)
        val json = JSONObject()
        json.put("vehicle_type_id", data.veh_id)
        json.put("ride_id",data.ride_id)
        Log.d("SendData", "json===" + json)
        val jsonOblect: JsonObjectRequest = object : JsonObjectRequest(Method.POST, Helper.Vehicle_OneWay, json, object :
            Response.Listener<JSONObject?>               {
            override fun onResponse(response: JSONObject?) {
                Log.d("SendData", "response===" + response)
                if (response != null) {

                    progressDialog.hide()
                    try {


                        val status = response.getString("status")
                        if (status.equals("false")) {
                            Toast.makeText(context, "Something Went Wrong!", Toast.LENGTH_LONG)
                                .show()
                        } else {
                            pref.setNotify("false")
                            context.startActivity(Intent(context, DriverDetails::class.java))
                        }
                    }catch (e:Exception){
                        MapUtility.showDialog(e.toString(),context)
                    }
                }
            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("SendData", "error===" + error)
                //  Toast.makeText(context, "Something Went Wrong!", Toast.LENGTH_LONG).show()
                progressDialog.hide()
                MapUtility.showDialog(error.toString(),context)


            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = java.util.HashMap()
                headers.put("Content-Type", "application/json; charset=UTF-8")
                headers.put("Authorization", "Bearer " + pref.getToken())
                headers.put("Accept", "application/vnd.api+json");
                return headers
            }
        }
        jsonOblect.setRetryPolicy(DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        queue.add(jsonOblect)
    }

        if (row_index === position) {
            holder.linear.setBackgroundColor(R.color.colorcoffie)

        }


    }
    override fun getItemCount(): Int {
        return vehicleoutstatiion.size
    }


}