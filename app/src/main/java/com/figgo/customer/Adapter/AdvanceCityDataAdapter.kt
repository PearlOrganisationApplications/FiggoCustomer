package com.figgo.customer.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.figgo.customer.pearlLib.PrefManager
import com.figgo.customer.Model.AdvanceCityCabModel
import com.figgo.customer.R
import com.figgo.customer.UI.AdavanceCityCabActivity.CabDetailsActivity
import com.squareup.picasso.Picasso


class AdvanceCityDataAdapter(var context:Activity, var cablist:List<AdvanceCityCabModel>): Adapter<AdvanceCityDataAdapter.AdvanceCityHolder>() {
    lateinit var pref: PrefManager
    var row_index = -1
    class AdvanceCityHolder(itemview: View):ViewHolder(itemview)
    {
        var cab=itemview.findViewById<ImageView>(R.id.cab)
        var ratings=itemview.findViewById<TextView>(R.id.rating)
            // var min=itemview.findViewById<TextView>(R.id.min)
        var max=itemview.findViewById<TextView>(R.id.max)
        var linear=itemview.findViewById<LinearLayout>(R.id.linear)
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvanceCityHolder {
        return AdvanceCityHolder(LayoutInflater.from(parent.context).inflate(R.layout.city_cab_layout,parent,false))
    }


    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: AdvanceCityHolder, position: Int) {
        pref = PrefManager(context)
     var data=cablist[position]
      //  holder.cab.setImageResource(data.cab)
        holder.ratings.text=data.name
       // holder.min.text=data.min
        holder.max.text="Rs ." +data.max
        Picasso.get().load(data.image).into(holder.cab)

        holder.itemView.setOnClickListener {

           pref.setRideId(data.rideId)
            pref.setVehicleId(data.vehicleId)
            row_index = position
            notifyDataSetChanged()
            context.startActivity(Intent(context, CabDetailsActivity::class.java))
           }

        if (row_index === position) {
            holder.linear.setBackgroundColor(R.color.colorcoffie)

        }


    }

    override fun getItemCount(): Int {
       return cablist.size
    }
}