package com.figgo.cabs.figgodriver.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.figgo.customer.Adapter.HistoryDataAdapter
import com.figgo.customer.Adapter.RideHistoryRowAdapter
import com.figgo.customer.Model.AdvanceCityCabModel
import com.figgo.customer.Model.HistoryModel
import com.figgo.customer.R
import com.figgo.customer.Util.MapUtility
import com.figgo.customer.pearlLib.Helper
import com.figgo.customer.pearlLib.PrefManager

import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap

/**
 * A simple [Fragment] subclass.
 * Use the [RideHistory.newInstance] factory method to
 * create an instance of this fragment.
 */
class RideHistory : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    var contentdata= ArrayList<HistoryModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ride_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var prefManager= PrefManager(requireContext())
        var header: RecyclerView = view.findViewById<RecyclerView>(R.id.ridehistoryheader)
       // var url3 = "https://test.pearl-developer.com/figo/api/driver/ride-history"
        val URL = Helper.RIDE_HISTORY
        var progressbar = view.findViewById<ProgressBar>(R.id.ridehistory_progressbar)
       var data_view = view.findViewById<HorizontalScrollView>(R.id.ridehisory_data)
        progressbar.visibility=View.VISIBLE
       data_view.visibility=View.GONE
        val queue = Volley.newRequestQueue(requireContext())

        var headerData = listOf<String>("Booking ID","To","From","Status","Distance","View");
       // var contentdata = ArrayList<List<String>>()
      //  contentdata.add(listOf("Booking ID","To location","From location","Status","Distance","View"))
       /* for (i in 0..40)
            contentdata.add(listOf("1","Sagar Bisht","01-02-2023","8:40am","50min","Chandigarh","Patiala","Pending","View"))*/

        val jsonObject:JsonObjectRequest = object :JsonObjectRequest(Method.POST,URL,null,
            {
                if(it!=null) {
                    try {


                        progressbar.visibility = View.GONE
                        data_view.visibility = View.VISIBLE
                    //   data_view.isNestedScrollingEnabled = false
                        Log.d("Data Response", "" + it)
                        var allride: JSONObject = it.getJSONObject("data")
                        var allrideArray: JSONArray = allride.getJSONArray("all_rides")
                        contentdata.add(
                            HistoryModel(
                                "Booking Id",
                                "To Location",
                                "From Location",
                                "Status",
                                "Distance",
                                "View","Type"
                            )
                        )
                        //  ride_details=allrideArray.optJSONObject(1).getJSONObject("ride_detail")
                        //Log.d("Ride Detail ",""+ride_details.toString())
                        for (p in 0..allrideArray.length() - 1) {
                            val booking_id =
                                it.getJSONObject("data").getJSONArray("all_rides")
                                    .getJSONObject(p).getString("booking_id")
                            val name = it.getJSONObject("data").getJSONArray("all_rides")
                                .getJSONObject(p).getJSONObject("to_location").getString("name")
                            val status = it.getJSONObject("data").getJSONArray("all_rides")
                                .getJSONObject(p).getString("status")
                            val type = it.getJSONObject("data").getJSONArray("all_rides")
                                .getJSONObject(p).getString("type")
                            val name1 = it.getJSONObject("data").getJSONArray("all_rides")
                                .getJSONObject(p).getJSONObject("from_location")
                                .getString("name")

                            val actual_distance =
                                it.getJSONObject("data").getJSONArray("all_rides")
                                    .getJSONObject(p).getString("actual_distance")

                            val date =
                                it.getJSONObject("data").getJSONArray("all_rides")
                                    .getJSONObject(p).getString("date_only")

                            val time =
                                it.getJSONObject("data").getJSONArray("all_rides")
                                    .getJSONObject(p).getString("time_only")
                            val ride_id =
                                it.getJSONObject("data").getJSONArray("all_rides")
                                    .getJSONObject(p).getJSONObject("ride_details").getString("ride_id")

                            val paramMap = HashMap<String, String>()
                            paramMap.put( "booking_id", booking_id);
                            paramMap.put( "to_loc" , name);
                            paramMap.put( "from_loc", name1);
                            paramMap.put( "status", status);
                            paramMap.put( "distance" , actual_distance);
                            paramMap.put( "date" , date);
                            paramMap.put( "time" , time);
                            paramMap.put( "type" , type);
                            paramMap.put( "ride_id" , ride_id);


                            MapUtility.paramMap.put(p,paramMap)

                            contentdata.add(
                                HistoryModel(
                                    booking_id,
                                    name,
                                    name1,
                                    status,
                                    actual_distance,
                                    "View",type
                                )
                            )
                        }
                        header.adapter = HistoryDataAdapter(requireActivity(),contentdata)
                        header.layoutManager = LinearLayoutManager(requireContext())
                        header.isNestedScrollingEnabled = false
                    }
                    catch (e:Exception){
                        Toast.makeText(requireContext(),"Server Problem", Toast.LENGTH_SHORT).show()
                    }
                }

            },{


            }){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers.put("Authorization", "Bearer " + prefManager.getToken())
                return headers
            }
        }
        queue.add(jsonObject)


    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RideHistory.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): RideHistory {
            val fragment = RideHistory()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}