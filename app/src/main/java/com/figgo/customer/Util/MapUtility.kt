//  The MIT License (MIT)
//  Copyright (c) 2018 Intuz Solutions Pvt Ltd.
//  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
//  (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify,
//  merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
//  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
//  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
//  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
package com.figgo.customer.Util

import android.net.NetworkInfo
import android.net.ConnectivityManager
import android.widget.Toast
import android.view.Gravity
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.figgo.customer.Model.AdvanceCityCabModel
import com.figgo.customer.Model.OneWayvehiclelistModel
import com.figgo.customer.R
import java.lang.Exception
import java.util.ArrayList
import java.util.HashMap

object MapUtility {
    const val MAP_URL = "https://maps.googleapis.com"
    var apiKey = "AIzaSyCbd3JqvfSx0p74kYfhRTXE7LZghirSDoU"
    var currentLocation: Location? = null
    var popupWindow: Dialog? = null
    var ADDRESS = "address"
    var LATITUDE = "lat"
    var LONGITUDE = "long"
    var cablistOneWay= ArrayList<OneWayvehiclelistModel>()
    val paramMap = HashMap<Int, HashMap<String,String>>()
    fun isNetworkAvailable(context: Context): Boolean {
        var activeNetworkInfo: NetworkInfo? = null
        try {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            activeNetworkInfo = connectivityManager.activeNetworkInfo
        } catch (ex: Exception) {
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun showToast(context: Context?, message: String?) {
        try {
            val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
            toast.show()
        } catch (ex: Exception) {
        }
    }

    fun showProgress(context: Context) {
        try {
            if (!(context as Activity).isFinishing) {
                val layout: View =
                    LayoutInflater.from(context).inflate(R.layout.pop_loading, null)
                popupWindow = Dialog(context, R.style.Theme_Figgo)
                popupWindow!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                popupWindow!!.setContentView(layout)
                popupWindow!!.setCancelable(false)
                if (!context.isFinishing) {
                    popupWindow!!.show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideProgress() {
        try {
            if (popupWindow != null && popupWindow!!.isShowing) {
                popupWindow!!.dismiss()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
    fun showDialog(title: String,requireActivity :Context) {

      /*  val builder = AlertDialog.Builder(requireActivity)
        //set title for alert dialog
       // builder.setTitle(R.string.dialogTitle)
        //set message for alert dialog
        builder.setMessage("Error! We are unable to commmunicate with servers, please try again in few mintues. if still problem persists, kindly contact with Figgo Support. with all the details")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        val alertDialog: AlertDialog = builder.create()
        //performing positive action
        builder.setPositiveButton("Ok"){dialogInterface, which ->
           alertDialog.cancel()
        }
        //performing cancel action

        // Create the AlertDialog

        // Set other dialog properties
        alertDialog.setCancelable(true)
        alertDialog.show()*/

        val dialog = Dialog(requireActivity)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_layout)
        val body = dialog.findViewById(R.id.error) as TextView
       body.text = "Error! We are unable to commmunicate with servers, please try again in few mintues. if still problem persists, kindly contact with Figgo Support. With all the details"
    //  body.text = title
        val yesBtn = dialog.findViewById(R.id.ok) as TextView
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        val window: Window? = dialog.getWindow()
        window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }



}