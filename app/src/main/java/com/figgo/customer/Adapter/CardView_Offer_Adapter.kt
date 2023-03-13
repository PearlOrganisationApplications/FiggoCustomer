package com.figgo.customer.Adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


import com.figgo.customer.pearlLib.PrefManager
import com.figgo.customer.Model.CurrentVehicle_itemListAdapter
import com.figgo.customer.R
import com.figgo.customer.UI.CityCabActivity
import com.figgo.customer.UI.OneWayOutstationActivity.BookingDestinatiion
import com.figgo.customer.UI.OneWayOutstationActivity.OneWay_Activity
import com.figgo.customer.UI.Shared_cab_Activity

class CardView_Offer_Adapter(var context: Activity, var cardoffer_list: ArrayList<CurrentVehicle_itemListAdapter>): RecyclerView.Adapter<CardView_Offer_Adapter.CurrentDriverHolder>() {
    lateinit var pref: PrefManager


    class CurrentDriverHolder(itemview: View): RecyclerView.ViewHolder(itemview){
        var cab_category=itemview.findViewById<ImageView>(R.id.cab_category)
        var cab_name=itemview.findViewById<TextView>(R.id.cab_name)
        var ll_lnr=itemview.findViewById<LinearLayout>(R.id.ll_lnr)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardView_Offer_Adapter.CurrentDriverHolder {
        return CardView_Offer_Adapter.CurrentDriverHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_offer_list, parent, false))
    }

    override fun onBindViewHolder(holder: CardView_Offer_Adapter.CurrentDriverHolder, position: Int) {
        var data=cardoffer_list[position]
        holder.cab_category.setImageResource(data.cab_category)
        holder.cab_name.text=data.cab_name
        holder.itemView.setOnClickListener {
            when(holder.adapterPosition){
                0->{

                    holder.cab_name.setTextColor(Color.WHITE)
                    holder.ll_lnr.setBackgroundColor(Color.parseColor("#A79ECD"))
                    /*holder.cab_category.setImageResource(R.drawable.citycab)*/
                    context.startActivity(Intent(context,CityCabActivity::class.java))
                }
                1->{
                    holder.cab_name.setTextColor(Color.WHITE)
                    holder.ll_lnr.setBackgroundColor(Color.parseColor("#A79ECD"))
                  /*  holder.cab_category.setImageResource(R.drawable.intracity)*/
                    context.startActivity(Intent(context, OneWay_Activity::class.java))
                }
                2->{
                    holder.cab_name.setTextColor(Color.WHITE)
                    holder.ll_lnr.setBackgroundColor(Color.parseColor("#A79ECD"))
                    /*holder.cab_category.setImageResource(R.drawable.city_cab)*/
                    context.startActivity(Intent(context,Shared_cab_Activity::class.java))
                }
            }

        }





    }

    override fun getItemCount(): Int {
        return cardoffer_list.size
    }


}