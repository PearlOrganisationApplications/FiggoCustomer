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
import com.figgo.customer.Model.HistoryModel
import com.figgo.customer.R
import com.figgo.customer.UI.CabDetailsActivity
import com.squareup.picasso.Picasso


class HistoryDataAdapter(var context:Activity, var historyData:List<HistoryModel>): Adapter<HistoryDataAdapter.HistoryHolder>() {

    class HistoryHolder(itemview: View):ViewHolder(itemview)
    {

      /*  var bookingNo = itemView.findViewById<TextView>(R.id.tBookingNo)
        var toLoc = itemView.findViewById<TextView>(R.id.tToLoc)
        var fromLoc = itemView.findViewById<TextView>(R.id.tFromLoc)
        var status = itemView.findViewById<TextView>(R.id.tStatus)
        var distance = itemView.findViewById<TextView>(R.id.tDistance)*/
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        return HistoryHolder(LayoutInflater.from(parent.context).inflate(R.layout.ridehistoryheader,parent,false))
    }


    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {


      /*  holder.bookingNo.setText(historyData.get(position).booking_no)
        holder.toLoc.setText(historyData.get(position).to_loc)
        holder.fromLoc.setText(historyData.get(position).from_loc)
        holder.status.setText(historyData.get(position).status)
        holder.distance.setText(historyData.get(position).distance)*/

    }

    override fun getItemCount(): Int {
       return historyData.size
    }
}