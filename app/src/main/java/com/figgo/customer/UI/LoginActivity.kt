package com.figgo.customer.UI

import android.annotation.SuppressLint
import android.app.ProgressDialog

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.navigation.NavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.figgo.customer.pearlLib.PrefManager
import com.figgo.customer.R
import com.figgo.customer.Util.MapUtility
import com.figgo.customer.pearlLib.Helper

import org.json.JSONObject


class LoginActivity : AppCompatActivity(){

    private val REQUEST_LOCATION = 1
    private val RC_SIGN_IN = 1
    private var mGoogleApiClient: GoogleApiClient? = null
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var nav_controller:NavController
    lateinit var pref: PrefManager
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var cc_number : ConstraintLayout
    lateinit var otp_screen : CardView
    lateinit var input_number : TextView
    lateinit var enteredOTP: EditText
    lateinit var resend : TextView
    lateinit var otp_Verify_button: Button
    lateinit var progress: ProgressBar
    lateinit var cTimer : CountDownTimer
     var count : Int = 0
    lateinit var txt_mpin : TextView
    lateinit var txt_otp : TextView
    lateinit var login_mpin : CardView
    lateinit var btn_login_mpin : TextView
    lateinit var txt_otpW : TextView
    lateinit var txt_MPinW : TextView
    lateinit var mPin : EditText
    lateinit var ll_number : LinearLayout
    var press :String ?= "otp"
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        pref = PrefManager(this)
        //  var email = findViewById<TextView>(R.id.email)
        //  var number = findViewById<TextView>(R.id.number)
        input_number = findViewById<TextView>(R.id.input_number)
        ll_number = findViewById<LinearLayout>(R.id.ll_number)


        val continuetv = findViewById<TextView>(R.id.continuetv)
       // var forgot_account = findViewById<TextView>(R.id.forgot_account)
       // var google_login_button = findViewById<TextView>(R.id.google_login_button)
        cc_number = findViewById<ConstraintLayout>(R.id.cc_number)
        otp_screen = findViewById<CardView>(R.id.otp_screen)
        progress = findViewById<ProgressBar>(R.id.progress)
        enteredOTP = findViewById<EditText>(R.id.enteredOTP)
        otp_Verify_button = findViewById<Button>(R.id.otp_Verify_button)
        resend = findViewById<TextView>(R.id.resend)
        login_mpin = findViewById<CardView>(R.id.c_mpin)
        btn_login_mpin = findViewById<TextView>(R.id.btn_login_mpin)

        txt_mpin = findViewById<TextView>(R.id.txtMpin)
        txt_otpW = findViewById<TextView>(R.id.txt_otp)
        txt_MPinW = findViewById<TextView>(R.id.txt_mpin)
        txt_otp = findViewById<TextView>(R.id.txtOtp)
        mPin = findViewById<EditText>(R.id.edt_mpin)
        var window = window
        window.setStatusBarColor(Color.parseColor("#000F3B"))

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

      //  ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
        // mGoogleApiClient = GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this)
        //    .addOnConnectionFailedListener(this).build()


        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this@LoginActivity, gso);
        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this@LoginActivity)
        //updateUI(account)

// Set the dimensions of the sign-in button.
        // Set the dimensions of the sign-in button.
        val signInButton = findViewById<TextView>(R.id.google_login_button)

        signInButton.setOnClickListener {
            val signInIntent: Intent = mGoogleSignInClient!!.getSignInIntent()
            startActivityForResult(signInIntent, RC_SIGN_IN)

        }
        /* email.setOnClickListener {
                 input_number.setHint("Enter Your Email Id")
                 email.setTypeface(Typeface.DEFAULT_BOLD);
                 input_number.inputType = TYPE_CLASS_TEXT
                 number.setTypeface(Typeface.DEFAULT)


             }
             number.setOnClickListener {
                 input_number.setHint("Enter Your Mobile number")
                 number.setTypeface(Typeface.DEFAULT_BOLD);
               //  email.setTypeface(Typeface.DEFAULT)
                 input_number.inputType = TYPE_CLASS_NUMBER
             }*/

        continuetv.setOnClickListener {

            //var mobile_num=binding.inputNumber.text.toString()

            if (press.equals("otp")) {
                if (input_number.length() < 10){
                    Toast.makeText(this@LoginActivity, "Required 10 Digit", Toast.LENGTH_SHORT).show()

                }else {

                    login()
                }
            }else if (press.equals("mpin")) {
                if (mPin.text.toString().equals("")){
                    Toast.makeText(this@LoginActivity, "Enter MPIN", Toast.LENGTH_SHORT).show()

                }else{
                    if (mPin.text.toString().length < 4){
                        Toast.makeText(this@LoginActivity, "Required 4 Digit", Toast.LENGTH_SHORT).show()

                    }else{
                        if (pref.getMpin().equals(mPin.text.toString())){
                            startActivity(Intent(this,DashBoard::class.java))
                        }else{
                            Toast.makeText(this@LoginActivity, "Wrong MPIN", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
            }
            //getotp()

        }
       // val s = pref.getMpin()
      //  Log.d("MPIN ", "" + pref.getMpin())

        txt_mpin.setOnClickListener {

            //var mobile_num=binding.inputNumber.text.toString()
            txt_otp.setBackgroundColor(getColor(R.color.colorbluedark))
            txt_mpin.setBackgroundColor(Color.BLACK)

            press = "mpin"

            txt_otpW.isVisible = false
            txt_MPinW.isVisible = true
            ll_number.isVisible = false
            mPin.isVisible = true
            //getotp()

        }

        txt_otp.setOnClickListener {
            txt_mpin.setBackgroundColor(getColor(R.color.colorbluedark))
            txt_otp.setBackgroundColor(Color.BLACK)

            press = "otp"
            txt_otpW.isVisible = true
            txt_MPinW.isVisible = false
            mPin.isVisible = false
            ll_number.isVisible = true
            //getotp()

        }


        btn_login_mpin.setOnClickListener {

          if (mPin.text.toString().equals("")){
              Toast.makeText(this@LoginActivity, "Enter MPIN", Toast.LENGTH_SHORT).show()

          }else{
              if (mPin.text.toString().length < 4){
                  Toast.makeText(this@LoginActivity, "Required 4 Digit", Toast.LENGTH_SHORT).show()

              }else{
                  if (pref.getMpin().equals(mPin.text.toString())){
                      startActivity(Intent(this,DashBoard::class.java))
                  }else{
                      Toast.makeText(this@LoginActivity, "Wrong MPIN", Toast.LENGTH_SHORT).show()

                  }
              }
          }

        }
        otp_Verify_button.setOnClickListener {
            if (enteredOTP.text.toString().equals("") || enteredOTP.text.toString().equals("null")) {
                Toast.makeText(this@LoginActivity, "Enter Otp", Toast.LENGTH_SHORT).show()
            } else {

                if (enteredOTP.length() < 6){
                    Toast.makeText(this@LoginActivity, "Required 6 Digit", Toast.LENGTH_SHORT).show()


                }else {
                    verifyOtp()
                }
            }
        }
        resend.setOnClickListener {

            getotp()

        }
    }
    fun startTimer() {
        cTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                resend.setText("seconds remaining: " +"00:"+ (millisUntilFinished / 1000).toString())
            }

            override fun onFinish() {
                resend.setText("RESEND OTP!")
                resend.setTextColor(Color.BLUE)

            }
        }
        cTimer.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }





    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.d("Account ", "" + account.account)

            pref.setAccountDetails(
                account.account.toString(),
                account.displayName.toString(),
                account.photoUrl.toString())
            // prefManager.setToken("")
            Toast.makeText(this@LoginActivity, "Signed In :" + account.account.toString(), Toast.LENGTH_LONG).show()

           /* if (pref.getMpin().equals("") || pref.getMpin().equals("null")) {
                startActivity(Intent(this,MPinGenerate::class.java))
            } else {

            }*/
            startActivity(Intent(this,DashBoard::class.java))
            // Signed in successfully, show authenticated UI.
            // updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("SignIN = ", "signInResult:failed code=" + e.statusCode)
            //updateUI(null)
        }
    }
    private fun login(){
        progress.isVisible  =true
        cc_number.isVisible = false
        otp_screen.isVisible = false
        val URL = Helper.REGISTER
        val queue = Volley.newRequestQueue(this@LoginActivity)
        val json = JSONObject()
        json.put("contact_no", input_number.text.toString())
        Log.d("SendData", "json===" + json)
        val jsonOblect: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, URL, json, object :
                Response.Listener<JSONObject?> {
                override fun onResponse(response: JSONObject?) {
                    Log.d("SendData", "response===" + response)
                    if (response != null) {
                        try {
                            pref.setUserId(response.getJSONObject("user").getString("id"))
                            pref.settv_mobilenumber(
                                response.getJSONObject("user").getString("contact_no")
                            )
                            pref.settv_rajsharma(response.getJSONObject("user").getString("name"))
                            pref.settv_gmail(response.getJSONObject("user").getString("email"))
                            getotp()
                        }catch (e:Exception){
                            MapUtility.showDialog(e.toString(),this@LoginActivity)

                        }
                    }

                    // Get your json response and convert it to whatever you want.
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    MapUtility.showDialog(error.toString(),this@LoginActivity)
                    //Toast.makeText(this@LoginActivity, "Something went wrong!", Toast.LENGTH_LONG).show()
                }
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> = HashMap()
                    //  headers["Authorization"] = "TOKEN" //put your token here
                    headers.put("Accept", "application/vnd.api+json")
                    return headers
                }
            }

        queue.add(jsonOblect)


        //startActivity(Intent(this,MPinGenerate::class.java))

    }

    private fun getotp(){
        pref.setNumber(input_number.text.toString())
        val URL = Helper.SEND_OTP
        val queue = Volley.newRequestQueue(this@LoginActivity)
        val json = JSONObject()
        json.put("type", "user")
        json.put("type_id", pref.getUserId())
        json.put("contact_no", input_number.text.toString())
        Log.d("SendData", "json===" + json)
        val jsonOblect: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, URL, json, object :
                Response.Listener<JSONObject?> {
                override fun onResponse(response: JSONObject?) {
                    Log.d("SendData", "response===" + response)
                    if (response != null) {
                        progress.isVisible  =false
                        cc_number.isVisible = false
                        otp_screen.isVisible = true
                        startTimer()
                    }
                    // Get your json response and convert it to whatever you want.
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                  //  Toast.makeText(this@LoginActivity, "Something went wrong!", Toast.LENGTH_LONG).show()

                    MapUtility.showDialog(error.toString(),this@LoginActivity)
                }
            }) {
                @SuppressLint("SuspiciousIndentation")
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> = java.util.HashMap()
                    headers.put("Content-Type", "application/json; charset=UTF-8");
                    headers.put("Accept", "application/vnd.api+json");
                    return headers
                }
            }

        queue.add(jsonOblect)
        //startActivity(Intent(this,MPinGenerate::class.java))

    }

    private fun verifyOtp(){
        val progressDialog = ProgressDialog(this)
        progressDialog.show()
        val URL = Helper.CHECK_OTP
        val queue = Volley.newRequestQueue(this@LoginActivity)
        val json = JSONObject()
        json.put("type", "user")
        json.put("type_id", pref.getUserId())
        json.put("otp", enteredOTP.text.toString())
        Log.d("SendData", "json===" + json)
        val jsonOblect: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, URL, json, object :
                Response.Listener<JSONObject?> {
                override fun onResponse(response: JSONObject?) {
                    Log.d("SendData", "response===" + response)
                    if (response != null) {
                        progressDialog.hide()
                        try {
                            if (pref.getToken().equals("") || pref.getToken().equals("null")) {

                                val token = response.getString("token")
                                pref.setToken(token)
                                pref.isValidLogin()
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Login Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                //  Log.d("SendData", "token===" + token)
                                //  startActivity(Intent(this@LoginActivity,MPinGenerate::class.java))
                               /* if (pref.getMpin().equals("")) {
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            MPinGenerate::class.java
                                        )
                                    )
                                } else {
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            DashBoard::class.java
                                        )
                                    )
                                }*/
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        DashBoard::class.java
                                    )
                                )

                            } else {
                                val token = response.getString("token")
                                pref.setToken(token)
                                pref.isValidLogin()
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Login Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(this@LoginActivity, DashBoard::class.java))
                            }
                        }catch (e:Exception){
                            MapUtility.showDialog(e.toString(),this@LoginActivity)

                        }

                    }
                    // Get your json response and convert it to whatever you want.
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    progressDialog.hide()
                    MapUtility.showDialog(error.toString(),this@LoginActivity)
                  //  Toast.makeText(this@LoginActivity, "Something went wrong!", Toast.LENGTH_LONG).show()
                }
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> = HashMap()
                    headers.put("Content-Type", "application/json; charset=UTF-8");
                    headers.put("Accept", "application/vnd.api+json") //put your token here
                    return headers
                }
            }

        queue.add(jsonOblect)


        //startActivity(Intent(this,MPinGenerate::class.java))

    }


    override fun onBackPressed() {
        if (count == 0) {
            cc_number.isVisible = true
            otp_screen.isVisible = false
        }else{
            finishAffinity()
        }
        count++
    }

}