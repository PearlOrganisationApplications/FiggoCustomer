package com.figgo.cabs.figgodriver.Adapter

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
import com.figgo.customer.R
import com.figgo.customer.UI.HistoryMapsActivity


class RideHistoryAdapter(var data: List<String>,var x:Int,var context: Context): RecyclerView.Adapter<RideHistoryHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideHistoryHolder {
        return RideHistoryHolder(LayoutInflater.from(parent.context).inflate(R.layout.ridehistory_table_adapter,parent,false))

    }

    override fun onBindViewHolder(holder: RideHistoryHolder, position: Int) {

        if (data[position].equals("View")&&x!=0)
        {
            holder.boxTV.setBackgroundColor(context.getColor(R.color.appcolorlight))
            holder.boxTV.setTextColor(context.getColor(R.color.white))

            holder.boxTV.setOnClickListener {


                context.startActivity(
                    Intent(context, HistoryMapsActivity::class.java)
                    .putExtra("position",x))
                // .putExtra("driver_id",OneWayListRatingVehicle.driver_id))

            }
        }
       // Log.d("dataXXXX",data[position])
        holder.boxTV.setText(data[position])
        if (x==0){
            //holder.boxTV.setTextColor(Color.WHITE)
            holder.block.setBackgroundColor(context.getColor(R.color.colornavyblue))
            holder.boxTV.setTypeface(null, Typeface.BOLD);
            holder.boxTV.setTextColor(context.getColor(R.color.white))
        }else if(x%2==0) {
            holder.block.setBackgroundColor(Color.LTGRAY)
            holder.side.setBackgroundColor(Color.LTGRAY)
        }
    }



    override fun getItemCount(): Int {
        return data.size
    }

}
class RideHistoryHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    var boxTV=itemView.findViewById<TextView>(R.id.tablecontentTV)
    var block = itemView.findViewById<LinearLayout>(R.id.blocklinear)
    var side = itemView.findViewById<LinearLayout>(R.id.history_side_band)
}