package com.figgo.customer.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.figgo.customer.Model.Data
import com.figgo.customer.R
import com.figgo.customer.UI.AdavanceCityCabActivity.LocationPickerActivity


class RideCityAdapter(var context:Activity, var ride: ArrayList<LocationPickerActivity>): Adapter<RideCityAdapter.RideCityHolder>() {

    private val itemListener: ItemListener? = null
    private val type: String? = null
    class RideCityHolder(itemview: View):ViewHolder(itemview)
    {
       var tv_start_point = itemView.findViewById<AppCompatTextView?>(R.id.tv_start_point)
       var img_marker = itemView.findViewById<ImageView?>(R.id.img_marker)
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideCityHolder {
        return RideCityHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_ride_history,parent,false))
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: RideCityHolder, position: Int) {
        val matchingRideResponse = ride.get(position)
        if (type != null && (type == "from" || type == "checkpoint")) {
           // holder.tv_start_point.setText(matchingRideResponse.start_location)
          //  holder.img_marker.setImageResource(R.drawable.pic_location)
        } else {
           // holder.tv_start_point.setText(matchingRideResponse.end_location)
          //  holder.img_marker.setImageResource(R.drawable.drop_location)
        }

        holder.itemView.setOnClickListener { v: View? ->
            if (itemListener != null) {
              //  itemListener.onCategoryClick(matchingRideResponse)
            }
        }
    }

    override fun getItemCount(): Int {
       return ride.size
    }
    interface ItemListener {
        fun onCategoryClick(matchingRideResponse: Data?)
    }
}