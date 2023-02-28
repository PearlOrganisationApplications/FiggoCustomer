package com.figgo.customer.pearlLib

import android.content.Context
import android.content.SharedPreferences




 class PrefManager(var context: Context) {
     // Shared preferences file name
     private val PREF_NAME = "welcome"
     private val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
     // shared pref mode
     var PRIVATE_MODE = 0
    var pref: SharedPreferences? = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    var editor: SharedPreferences.Editor? = pref!!.edit()
    var _context: Context? = context
     private val IS_VALID_LOGIN = "no"




 /*   fun PrefManager(context: Context?) {
        pref = _context
        editor =
    }*/
 fun setWelcomeScreen(welcome: Boolean) {
     editor!!.putBoolean("welcome", welcome)
     editor!!.commit()
 }

     fun getWelcomeScreen(): Boolean {
         return pref!!.getBoolean("welcome", true)
     }
    fun setFirstTimeLaunch(isFirstTime: Boolean) {
        editor!!.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
        editor!!.commit()
    }

    fun isFirstTimeLaunch(): Boolean {
        return pref!!.getBoolean(IS_FIRST_TIME_LAUNCH, true)
    }
     fun setCount(mpin: String){
         editor!!.putString("count",mpin)
         editor?.commit()
     }


     fun getCount():String{
         return pref?.getString("count","").toString()
     }
     fun setMpin(mpin: String){
         editor!!.putString("mpin",mpin)
         editor?.commit()
     }


     fun getMpin():String{
         return pref?.getString("mpin","").toString()
     }


     fun setWelcomeSkip(welcome: String){
         editor!!.putString("welcome",welcome)
         editor?.commit()
     }


     fun getWelcomeSkip():String{
         return pref?.getString("welcome","").toString()
     }


     fun setToken(token:String){
         editor?.putString("token",token)
         editor?.commit()
     }

     fun getToken():String{
         return pref?.getString("token","null").toString()
     }

     fun setRideId(ride_id:String){
         editor?.putString("ride_id",ride_id)
         editor?.commit()
     }

     fun getRideId():String{
         return pref?.getString("ride_id","null").toString()
     }
     fun setprice(price:String){
         editor?.putString("price",price)
         editor?.commit()
     }

     fun getprice():String{
         return pref?.getString("price","null").toString()
     }
     fun setOtp(otp:String){
         editor?.putString("otp",otp)
         editor?.commit()
     }

     fun getOtp():String{
         return pref?.getString("otp","null").toString()
     }
     fun setAccountDetails(email:String,name:String,photoURL:String){
         editor?.putString("EmailID",email)
         editor?.putString("EmailName",name)
         editor?.putString("PhotoUrl",photoURL)
         editor?.commit()
     }
     fun getAccountMail():String{
         return pref?.getString("EmailID","")!!
     }
     fun getAccountName():String{
         return pref?.getString("EmailName","")!!
     }
     fun getAccountPhotoURL():String{
         return pref?.getString("PhotoUrl","")!!
     }
     fun setBookingNo(book_no:String){
         editor?.putString("book_no",book_no)
         editor?.commit()
     }

     fun getBookingNo():String{
         return pref?.getString("book_no","null").toString()
     }

     fun setVehicleId(vehicle_id:String){
         editor?.putString("vehicle_id",vehicle_id)
         editor?.commit()
     }

     fun getVehicleId():String{
         return pref?.getString("vehicle_id","null").toString()
     }
     fun setdriver_id(driver_id:String){
         editor?.putString("driver_id",driver_id)
         editor?.commit()
     }

     fun getdriver_id(): String {
         return pref?.getString("driver_id","driver_id").toString()

     }

     fun setactivavehiclenumber(activavehiclenumber:String){
         editor?.putString("activavehiclenumber",activavehiclenumber)
         editor?.commit()
     }

     fun getactivavehiclenumber():String{
         return pref?.getString("activavehiclenumber","null").toString()
     }



     fun setdrivername(drivername:String){
         editor?.putString("drivername",drivername)
         editor?.commit()
     }

     fun getdrivername():String{
         return pref?.getString("drivername","null").toString()
     }




     fun setvehiclname(vehiclname:String){
         editor?.putString("vehiclname",vehiclname)
         editor?.commit()
     }

     fun getvehiclname():String{
         return pref?.getString("vehiclname","null").toString()
     }

     fun setvehicleprice(vehicleprice:String){
         editor?.putString("vehicleprice",vehicleprice)
         editor?.commit()
     }

     fun getvehicleprice():String{
         return pref?.getString("vehicleprice","null").toString()
     }

     fun setvehiclemodel(vehiclemodel:String){
         editor?.putString("vehiclemodel",vehiclemodel)
         editor?.commit()
     }

     fun getvehiclemodel():String{
         return pref?.getString("vehiclemodel","null").toString()
     }






     fun setdl_number(dl_number:String){
         editor?.putString("dl_number",dl_number)
         editor?.commit()
     }

     fun getdl_number():String{
         return pref?.getString("dl_number","null").toString()
     }


     fun setUserId(userid:String){
         editor?.putString("userid",userid)
         editor?.commit()
     }
     fun getUserId():String{
         return pref?.getString("userid","").toString()
     }
     fun isValidLogin(): Boolean {
         return pref!!.getBoolean(IS_VALID_LOGIN, false)
     }
     fun setNumber(number:String){
         editor?.putString("number",number)
         editor?.commit()
     }
     fun getNumber():String{
         return pref?.getString("number","").toString()
     }


     fun setride_id(ride_id:String){
         editor?.putString("ride_id",ride_id)
         editor?.commit()
     }

     fun getride_id():String{
         return pref?.getString("ride_id","null").toString()
     }



     fun setToLatL(lat:String){
         editor?.putString("lat_to_l",lat)
         editor?.commit()
     }

     fun getToLatL():String{
         return pref?.getString("lat_to_l","null").toString()
     }
     fun setToLngL(lat:String){
         editor?.putString("lng_to_l",lat)
         editor?.commit()
     }

     fun getToLngL():String{
         return pref?.getString("lng_to_l","null").toString()
     }
     fun setToLngM(lat:String){
         editor?.putString("lng_to_m",lat)
         editor?.commit()
     }

     fun getToLngM():String{
         return pref?.getString("lng_to_m","null").toString()
     }
     fun setToLatM(lat:String){
         editor?.putString("lat_to_m",lat)
         editor?.commit()
     }

     fun getToLatM():String{
         return pref?.getString("lat_to_m","null").toString()
     }


     fun setType(type:String){
         editor?.putString("type",type)
         editor?.commit()
     }

     fun getType():String{
         return pref?.getString("type","null").toString()
     }
     fun setTypeC(type:String){
         editor?.putString("typeC",type)
         editor?.commit()
     }

     fun getTypeC():String{
         return pref?.getString("typeC","null").toString()
     }

     fun setToLatLC(lat:String){
         editor?.putString("lat_to_lc",lat)
         editor?.commit()
     }

     fun getToLatLC():String{
         return pref?.getString("lat_to_lc","null").toString()
     }
     fun setToLngLC(lat:String){
         editor?.putString("lng_to_lc",lat)
         editor?.commit()
     }
     fun getToLngLC():String{
         return pref?.getString("lng_to_lc","null").toString()
     }

    /* fun settv_rajsharma(){

     }*/


     fun settv_rajsharma(tv_rajsharma:String) {
         editor?.putString("tv_rajsharma",tv_rajsharma)
         editor?.commit()
     }

     fun gettv_rajsharma():String{
         return pref?.getString("tv_rajsharma"," ").toString()
     }


     fun settv_mobilenumber(tv_mobilenumber:String) {
         editor?.putString("tv_mobilenumber",tv_mobilenumber)
         editor?.commit()
     }


     fun gettv_mobilenumber():String{
         return pref?.getString("tv_mobilenumber","null").toString()
     }

     fun settv_gmail(tv_gmail:String) {
         editor?.putString("tv_gmail",tv_gmail)
         editor?.commit()
     }

     fun gettv_gmail():String{
         return pref?.getString("tv_gmail"," ").toString()
     }

     fun setiv_imageView(iv_imageView:String) {
         editor?.putString("iv_imageView",iv_imageView)
         editor?.commit()
     }

     fun getiv_imageView():String{
         return pref?.getString("iv_imageView",getiv_imageView()).toString()
     }



     fun setToLngMC(lat:String){
         editor?.putString("lng_to_mc",lat)
         editor?.commit()
     }

     fun getToLngMC():String{
         return pref?.getString("lng_to_mc","null").toString()
     }
     fun setToLatMC(lat:String){
         editor?.putString("lat_to_mc",lat)
         editor?.commit()
     }

     fun getToLatMC():String{
         return pref?.getString("lat_to_mc","null").toString()
     }

     fun setgetRegistrationToken(getRegistrationToken:String){
         editor?.commit()
         editor?.putString("getRegistrationToken",getRegistrationToken)
     }

     fun getRegistrationToken(): Any {
         return pref?.getString("getRegistrationToken","null").toString()

     }
     fun setReqRideId(type:String){
         editor?.putString("req_ride_id",type)
         editor?.commit()
     }

     fun getReqRideId():String{
         return pref?.getString("req_ride_id","null").toString()
     }

     fun setSearchBack(type:String){
         editor?.putString("back_s",type)
         editor?.commit()
     }

     fun getSearchBack():String{
         return pref?.getString("back_s","null").toString()
     }

     fun setPrice(type:String){
         editor?.putString("price",type)
         editor?.commit()
     }

     fun getPrice():String{
         return pref?.getString("price","null").toString()
     }

     fun setTime(type:String){
         editor?.putString("time",type)
         editor?.commit()
     }

     fun getTime():String{
         return pref?.getString("time","null").toString()
     }
     fun setDate(type:String){
         editor?.putString("date",type)
         editor?.commit()
     }

     fun getDate():String{
         return pref?.getString("date","null").toString()
     }

     fun setNotify(type:String){
         editor?.putString("notify",type)
         editor?.commit()
     }

     fun getNotify():String{
         return pref?.getString("notify","null").toString()
     }

     fun setlatitude(toFloat: Float) {
         editor?.putFloat("f_lat",toFloat)
         editor?.commit()
     }
     fun getlatitude():Float{
         return pref!!.getFloat("f_lat",0.0f)
     }
     fun setlongitude(toFloat: Float) {
         editor?.putFloat("f_lng",toFloat)
         editor?.commit()
     }
     fun getlongitude():Float{
         return pref!!.getFloat("f_lng",0.0f)
     }

     fun setDriverlocation(toFloat: Float, toFloat1: Float) {
         editor?.putFloat("d_lat",toFloat)
         editor?.commit()
         editor?.putFloat("d_lng",toFloat1)
         editor?.commit()
     }
     fun getDriverlat():Float{
         return pref!!.getFloat("d_lat",0.0f)
     }
     fun getDriverlng():Float{
         return pref!!.getFloat("d_lng",0.0f)
     }


     fun setLiveLoc(LiveLoc: String) {
         editor?.putString("LiveLoc",LiveLoc)
         editor?.commit()

     }

     fun getLiveLoc(): String {
         return pref?.getString("LiveLoc","null").toString()
     }

     fun setManualLoc(ManualLoc: String) {
         editor?.putString("ManualLoc",ManualLoc)
         editor?.commit()

     }

     fun getManualLoc(): String {
         return pref?.getString("ManualLoc","null").toString()
     }
 }