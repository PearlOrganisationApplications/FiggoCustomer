package com.figgo.customer.Fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.figgo.customer.pearlLib.PrefManager
import com.figgo.customer.Adapter.HistoryBottomAdapter
import com.figgo.customer.Adapter.RideHistoryRowAdapter
import com.figgo.customer.R
import com.figgo.customer.Util.MapUtility
import com.figgo.customer.pearlLib.Helper
import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap


class HistoryFragment : Fragment() {

    lateinit var pref: PrefManager

    lateinit var historyBottomAdapter: HistoryBottomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = PrefManager(requireActivity())

        getHistory()
/*
        var contentdata = ArrayList<List<String>>()
        contentdata.add(listOf("Booking Id","Name","To location","From Location","Status","View"))

        for (i in 0..4)
            contentdata.add(listOf("#12345","Sagar","Dehradun","Delhi","Complete","View"))
        contentdata.add(listOf("#123","ABC","Dehradun","Chandigargh","Pending","View"))
        contentdata.add(listOf("#123","ABC","Dehradun","Chandigargh","Pending","View"))
        contentdata.add(listOf("#1367","ABC","Dehradun","Chandigargh","Pending","View"))
        contentdata.add(listOf("#9081","Neeraj","Dehradun","Mohali","Waiting","View"))
        contentdata.add(listOf("#9081","Neeraj","Dehradun","Mohali","Waiting","View"))
        contentdata.add(listOf("#9081","Neeraj","Dehradun","Mohali","Waiting","View"))
        contentdata.add(listOf("#9081","Neeraj","Dehradun","Mohali","Waiting","View"))
        contentdata.add(listOf("#12345","Sagar","Dehradun","Delhi","Complete","View"))
        contentdata.add(listOf("#12345","Sagar","Dehradun","Delhi","Complete","View"))
        header.adapter= RideHistoryRowAdapter(contentdata,requireContext())
        header.layoutManager=LinearLayoutManager(requireContext())*/
    }

    private fun getHistory() {





        val progressDialog = ProgressDialog(requireActivity())
        progressDialog.show()
        val URL =Helper.RIDE_HISTORY
        Log.d("SendData", "URL===" + URL)
        val queue = Volley.newRequestQueue(requireContext())
        var headerData = listOf<String>("Id","Name","To location","From Location","Status","View")
        var contentdata = ArrayList<List<String>>()
        contentdata.add(listOf("Booking ID","To location","From location","Status","Distance","View"))
        var header: RecyclerView? = view?.findViewById<RecyclerView>(R.id.ridehistoryheader)
        val json = JSONObject()
        val jsonOblect: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, URL, json, object : Response.Listener<JSONObject?>               {
                @SuppressLint("SuspiciousIndentation")
                override fun onResponse(response: JSONObject?) {

                    if(response!=null){
                        try {

                            Log.d("Data Response", "" + response)
                            var allride: JSONObject = response.getJSONObject("data")
                            var allrideArray: JSONArray = allride.getJSONArray("all_rides")
                            var ride_details: JSONObject
                            var booking_id: String
                            var destination: String
                            var from: String
                            var status: String
                            var actual_distance: String
                            var price: String
                            for (p in 0..allrideArray.length() - 1) {
                                progressDialog.hide()
                                val booking_id =
                                    response.getJSONObject("data").getJSONArray("all_rides")
                                        .getJSONObject(p).getString("booking_id")
                                val name = response.getJSONObject("data").getJSONArray("all_rides")
                                    .getJSONObject(p).getJSONObject("to_location").getString("name")
                                val name1 = response.getJSONObject("data").getJSONArray("all_rides")
                                    .getJSONObject(p).getJSONObject("from_location")
                                    .getString("name")
                                val status =
                                    response.getJSONObject("data").getJSONArray("all_rides")
                                        .getJSONObject(p).getString("status")
                                val actual_distance =
                                    response.getJSONObject("data").getJSONArray("all_rides")
                                        .getJSONObject(p).getString("actual_distance")

                                val date =
                                    response.getJSONObject("data").getJSONArray("all_rides")
                                        .getJSONObject(p).getString("date_only")

                                val time =
                                    response.getJSONObject("data").getJSONArray("all_rides")
                                        .getJSONObject(p).getString("time_only")
                                val paramMap = HashMap<String, String>()
                                paramMap.put( "booking_id", booking_id);
                                paramMap.put( "to_loc" , name);
                                paramMap.put( "from_loc", name1);
                                paramMap.put( "status", status);
                                paramMap.put( "distance" , actual_distance);
                                paramMap.put( "date" , date);
                                paramMap.put( "time" , time);


                                MapUtility.paramMap.put(p,paramMap)



                                contentdata.add(
                                    listOf(
                                        booking_id,
                                        name,
                                        name1,
                                        status,
                                        actual_distance,
                                        "View"
                                    )
                                )
                            }
                            header?.adapter = RideHistoryRowAdapter(contentdata, requireContext())
                            header?.layoutManager = LinearLayoutManager(requireContext())

                        }catch (e:Exception){
                            MapUtility.showDialog(e.toString(),requireActivity())

                        }
                    }

                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("SendData", "error===" + error)
                    progressDialog.hide()

                    MapUtility.showDialog(error.toString(),requireActivity())
                    //  Toast.makeText(requireActivity(), "Something went wrong!", Toast.LENGTH_LONG).show()

                }
            }) {
                @SuppressLint("SuspiciousIndentation")
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> = HashMap()
                    headers.put("Content-Type", "application/json; charset=UTF-8");
                    headers.put("Authorization", "Bearer " + pref.getToken());
                    return headers
                }
            }
        jsonOblect.setRetryPolicy(DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        queue.add(jsonOblect)

    }


}




