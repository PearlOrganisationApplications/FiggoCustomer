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
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.figgo.customer.UI.HistoryMapsActivity
import com.figgo.customer.R
import com.figgo.customer.UI.Current_Cab_DetailsActivity

data class BottomHistoryRideAdapter(var data: List<String>,var x:Int,var context: Context): RecyclerView.Adapter<RideHistoryHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideHistoryHolder {
        return RideHistoryHolder(LayoutInflater.from(parent.context).inflate(R.layout.ridehistory_table_adapter,parent,false))

    }

    override fun onBindViewHolder(holder: RideHistoryHolder, position: Int) {

       /* if (x == 0) {
            //holder.boxTV.setTextColor(Color.WHITE)
            holder.block.setBackgroundColor(context.getColor(R.color.colornavyblue))
            holder.booking_no.setTextColor(context.getColor(R.color.white))
            holder.to_loc.setTextColor(context.getColor(R.color.white))
            holder.from_loc.setTextColor(context.getColor(R.color.white))
            holder.status.setTextColor(context.getColor(R.color.white))
            holder.distance.setTextColor(context.getColor(R.color.white))
            holder.booking_no.setTypeface(null, Typeface.BOLD);
            holder.to_loc.setTypeface(null, Typeface.BOLD);
            holder.from_loc.setTypeface(null, Typeface.BOLD);
            holder.status.setTypeface(null, Typeface.BOLD);
            holder.distance.setTypeface(null, Typeface.BOLD);



            holder.booking_no.setText("Booking No")
            holder.to_loc.setText("To Location")
            holder.from_loc.setText("From Location")
            holder.status.setText("Status")
            holder.distance.setText("Distance")





        } else if (x % 2 == 0) {
            holder.block.setBackgroundColor(Color.LTGRAY)

            holder.to_loc.setText("To Location")
            holder.from_loc.setText("From Location")
            holder.status.setText("Status")
            holder.distance.setText("Distance")

            holder.view.setOnClickListener {


                context.startActivity(Intent(context, HistoryMapsActivity::class.java)
                    .putExtra("position",x))
                // .putExtra("driver_id",OneWayListRatingVehicle.driver_id))

            }



        }else{
            holder.block.setBackgroundColor(context.getColor(R.color.appcolorlight))
            holder.booking_no.setTextColor(context.getColor(R.color.white))
            holder.to_loc.setTextColor(context.getColor(R.color.white))
            holder.from_loc.setTextColor(context.getColor(R.color.white))
            holder.status.setTextColor(context.getColor(R.color.white))
            holder.distance.setTextColor(context.getColor(R.color.white))
            holder.booking_no.setText("Booking No")
            holder.to_loc.setText("To Location")
            holder.from_loc.setText("From Location")
            holder.status.setText("Status")
            holder.distance.setText("Distance")

            holder.view.setOnClickListener {


                context.startActivity(Intent(context, HistoryMapsActivity::class.java)
                    .putExtra("position",x))
                // .putExtra("driver_id",OneWayListRatingVehicle.driver_id))

            }

        }*/

       



    }
    override fun getItemCount(): Int {
        return data.size
    }

}
class RideHistoryHolder(itemView: View):ViewHolder(itemView){
  /*  var booking_no=itemView.findViewById<TextView>(R.id.booking_no)
    var to_loc=itemView.findViewById<TextView>(R.id.to_loc)
    var from_loc=itemView.findViewById<TextView>(R.id.from_loc)
    var status=itemView.findViewById<TextView>(R.id.status)
    var distance=itemView.findViewById<TextView>(R.id.distance)
    var block = itemView.findViewById<LinearLayout>(R.id.block)
    var view = itemView.findViewById<Button>(R.id.view)*/
}
