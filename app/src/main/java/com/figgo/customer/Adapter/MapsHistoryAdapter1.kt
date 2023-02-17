package com.figgo.customer.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.figgo.customer.UI.HistoryMapsActivity
import com.figgo.customer.R

class MapsHistoryAdapter1(var data: List<String>,var x:Int,var context: Context): RecyclerView.Adapter<MapsHistoryAdapter1.RideHistory_one>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideHistory_one {
        return RideHistory_one(LayoutInflater.from(parent.context).inflate(R.layout.ridehistory_table_adapter, parent, false))
    }

    override fun onBindViewHolder(holder: RideHistory_one, position: Int) {
        var str = data[position]
      /*  holder.boxTV.text = str
        if (x == 0) {
            //holder.boxTV.setTextColor(Color.WHITE)
            holder.block.setBackgroundColor(context.getColor(R.color.colornavyblue))
            holder.boxTV.setTextColor(context.getColor(R.color.white))
            holder.boxTV.setTypeface(null, Typeface.BOLD);
        } else if (x % 2 == 0) {
            holder.block.setBackgroundColor(Color.LTGRAY)

        }

        if (str.equals("View") && x != 0) {
            holder.boxTV.setBackgroundColor(context.getColor(R.color.appcolorlight))
            holder.boxTV.setTextColor(context.getColor(R.color.white))

            holder.boxTV.setOnClickListener {
                context.startActivity(Intent(context, HistoryMapsActivity::class.java))
            }

        }*/
    }


    override fun getItemCount(): Int {
        return data.size
    }

    class RideHistory_one(itemView: View) : ViewHolder(itemView) {
//        var block = itemView.findViewById<LinearLayout>(R.id.blocklinear)
    }
}
