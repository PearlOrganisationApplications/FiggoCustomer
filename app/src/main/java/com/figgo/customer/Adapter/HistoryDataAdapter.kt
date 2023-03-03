package com.figgo.customer.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.figgo.customer.Model.HistoryModel
import com.figgo.customer.R
import com.figgo.customer.UI.HistoryMapsActivity


class HistoryDataAdapter(var context:Activity, var historyData:List<HistoryModel>): Adapter<HistoryDataAdapter.HistoryHolder>() {

    class HistoryHolder(itemview: View):ViewHolder(itemview)
    {

        var booking_no = itemView.findViewById<TextView>(R.id.booking_no)
        var to_loc = itemView.findViewById<TextView>(R.id.to_loc)
        var from_loc = itemView.findViewById<TextView>(R.id.from_loc)
        var status = itemView.findViewById<TextView>(R.id.status)
        var distance = itemView.findViewById<TextView>(R.id.distance)
        var tRow = itemView.findViewById<TableRow>(R.id.tRowA)
        var view = itemView.findViewById<TextView>(R.id.view)
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        return HistoryHolder(LayoutInflater.from(parent.context).inflate(R.layout.ridehistoryheader,parent,false))
    }


    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {

        if (position == 0)
        {
            holder.tRow.setBackgroundColor(context.getColor(R.color.appcolorlight))
            holder.booking_no.setTextColor(context.getColor(R.color.white))
            holder.to_loc.setTextColor(context.getColor(R.color.white))
            holder.from_loc.setTextColor(context.getColor(R.color.white))
            holder.status.setTextColor(context.getColor(R.color.white))
            holder.distance.setTextColor(context.getColor(R.color.white))
            holder.view.setTextColor(context.getColor(R.color.white))
            holder.booking_no.setTypeface(null, Typeface.BOLD);
            holder.to_loc.setTypeface(null, Typeface.BOLD);
            holder.from_loc.setTypeface(null, Typeface.BOLD);
            holder.status.setTypeface(null, Typeface.BOLD);
            holder.distance.setTypeface(null, Typeface.BOLD);
            holder.view.setTypeface(null, Typeface.BOLD);

        }else {
            if(position%2==0){
                holder.tRow.setBackgroundColor(Color.LTGRAY)
            }
            else{
                holder.tRow.setBackgroundColor(Color.WHITE)
            }

            holder.view.setBackgroundColor(context.getColor(R.color.appcolorlight))
            holder.view.setTextColor(context.getColor(R.color.white))

            holder.view.setOnClickListener {



                context.startActivity(
                    Intent(context, HistoryMapsActivity::class.java)
                        .putExtra("position",position))
                // .putExtra("driver_id",OneWayListRatingVehicle.driver_id))

            }

        }
        // Log.d("dataXXXX",data[position])
        holder.booking_no.setText(historyData.get(position).booking_no)
        holder.to_loc.setText(historyData.get(position).to_loc)
        holder.from_loc.setText(historyData.get(position).from_loc)
        holder.status.setText(historyData.get(position).status)
        holder.distance.setText(historyData.get(position).distance)
        holder.view.setText(historyData.get(position).view)


    }

    override fun getItemCount(): Int {
       return historyData.size
    }
}