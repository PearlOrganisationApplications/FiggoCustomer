package com.figgo.customer.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.figgo.customer.R

class RideHistoryAdapter2(var data: ArrayList<List<String>>,var content: Context): RecyclerView.Adapter<RideHistoryAdapter2.RideHistoryRowHolder1>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideHistoryRowHolder1 {
        return RideHistoryRowHolder1(LayoutInflater.from(parent.context).inflate(R.layout.ridehistoryheader,parent,false))
    }

    override fun onBindViewHolder(holder: RideHistoryRowHolder1, position: Int) {
      //  holder.recyclerView.adapter=BottomHistoryRideAdapter(data[position],position,content)
      //  holder.recyclerView.layoutManager=
       //     LinearLayoutManager(content, LinearLayoutManager.VERTICAL,false)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class RideHistoryRowHolder1(itemView: View):ViewHolder(itemView) {
      //  var recyclerView = itemView.findViewById<RecyclerView>(R.id.ridehistoryRowRecycler)
    }



}