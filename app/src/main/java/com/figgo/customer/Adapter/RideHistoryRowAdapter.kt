package com.figgo.customer.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.figgo.cabs.figgodriver.Adapter.RideHistoryAdapter
import com.figgo.customer.R
import com.figgo.customer.UI.HistoryMapsActivity


class RideHistoryRowAdapter(var data: ArrayList<List<String>>,var context:Context): RecyclerView.Adapter<RideHistoryRowHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideHistoryRowHolder {
        return RideHistoryRowHolder(LayoutInflater.from(parent.context).inflate(R.layout.ridehistoryheader,parent,false))

    }

    override fun onBindViewHolder(holder: RideHistoryRowHolder, position: Int) {
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

            holder.view.setOnClickListener {


                context.startActivity(
                    Intent(context, HistoryMapsActivity::class.java)
                        .putExtra("position",position))
                // .putExtra("driver_id",OneWayListRatingVehicle.driver_id))

            }

        }
        // Log.d("dataXXXX",data[position])



}

    override fun getItemCount(): Int {
        return data.size
    }

}
class RideHistoryRowHolder(itemView: View):ViewHolder(itemView){
    var booking_no = itemView.findViewById<TextView>(R.id.booking_no)
    var to_loc = itemView.findViewById<TextView>(R.id.to_loc)
    var from_loc = itemView.findViewById<TextView>(R.id.from_loc)
    var status = itemView.findViewById<TextView>(R.id.status)
    var distance = itemView.findViewById<TextView>(R.id.distance)
    var view = itemView.findViewById<TextView>(R.id.view)
    var tRow = itemView.findViewById<TableRow>(R.id.tRowA)
}