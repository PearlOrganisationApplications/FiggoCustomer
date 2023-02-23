package com.figgo.customer.Adapter

import android.app.Activity
import android.app.ProgressDialog
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
import com.figgo.customer.pearlLib.PrefManager
import com.figgo.customer.UI.CurrentCityCabActivity.SearchDriver
import com.figgo.customer.Model.CurrentVehicleModel
import com.figgo.customer.R
import com.figgo.customer.Util.MapUtility
import com.figgo.customer.pearlLib.Helper
import com.squareup.picasso.Picasso
import org.json.JSONObject

class CurrentVehicleAdapter(var context: Activity, var cablist: ArrayList<CurrentVehicleModel>): RecyclerView.Adapter<CurrentVehicleAdapter.CurrentVehicleHolder>() {
    lateinit var pref: PrefManager
    var row_index = -1
    lateinit var progressDialog:ProgressDialog

    class CurrentVehicleHolder(itemview: View): RecyclerView.ViewHolder(itemview){
        var cab=itemview.findViewById<ImageView>(R.id.cab)
        var ratings=itemview.findViewById<TextView>(R.id.rating)
      //  var min=itemview.findViewById<TextView>(R.id.min)
        var max=itemview.findViewById<TextView>(R.id.max)
        var linear=itemview.findViewById<LinearLayout>(R.id.linear)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentVehicleAdapter.CurrentVehicleHolder {
        return CurrentVehicleAdapter.CurrentVehicleHolder(LayoutInflater.from(parent.context).inflate(R.layout.current_cab_list, parent, false))
    }

    override fun onBindViewHolder(holder: CurrentVehicleAdapter.CurrentVehicleHolder, position: Int) {
        pref = PrefManager(context)
        var data = cablist[position]
        holder.ratings.text = data.name
        // holder.min.text = data.min
        holder.max.text = "Rs. " + data.max
        Picasso.get().load(data.image).into(holder.cab)

        /*    holder.itemView.setOnClickListener {

            pref.setride_id(data.ride_id)
            pref.setVehicleId(data.driver_id)
            row_index = position
            notifyDataSetChanged()
            context.startActivity(Intent(context, MapsActivity1::class.java))
        }*/
        if (row_index === position) {
            holder.linear.setBackgroundColor(context.resources.getColor(R.color.quantum_bluegrey700))
        }

        holder.linear.setOnClickListener {
            pref.setride_id(data.ride_id)
             progressDialog = ProgressDialog(context)
            progressDialog.show()
            val queue = Volley.newRequestQueue(context)
            val json = JSONObject()
            json.put("vehicle_type_id", data.driver_id)
            json.put("ride_id",data.ride_id)
            Log.d("SendData", "json===" + json)
            val jsonOblect: JsonObjectRequest = object : JsonObjectRequest(Method.POST, Helper.Selectcityvehicletype, json, object :
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
                                context.startActivity(Intent(context, SearchDriver::class.java))
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
    }




    override fun getItemCount(): Int {
       return cablist.size
    }

}