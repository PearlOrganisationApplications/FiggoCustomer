package com.figgo.customer.Adapter

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


import com.figgo.customer.Model.OneWayvehiclelistModel



import com.figgo.customer.R
import com.figgo.customer.UI.OneWayOutstationActivity.OneWayCabBookDetails
import com.figgo.customer.pearlLib.PrefManager
import com.squareup.picasso.Picasso

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
         pref.setRideId(data.ride_id)

            pref.setVehicleId(data.veh_id)

            context.startActivity(Intent(context, OneWayCabBookDetails::class.java))

    }

        if (row_index === position) {
            holder.linear.setBackgroundColor(R.color.colorcoffie)

        }


    }
    override fun getItemCount(): Int {
        return vehicleoutstatiion.size
    }


}