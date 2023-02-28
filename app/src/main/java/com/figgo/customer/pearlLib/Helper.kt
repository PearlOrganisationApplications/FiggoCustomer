package com.figgo.customer.pearlLib

 object Helper {
     /****************Customer Application ******************/

    var baseurl = "https://test.pearl-developer.com/figo/api/"
    var  REGISTER = baseurl + "register"
    var  SEND_OTP = baseurl + "otp/send-otp"
    var  CHECK_OTP = baseurl + "otp/check-otp"
    var  GET_STATE = baseurl + "get-state"
    var  GET_CITY = baseurl + "get-city"
    var  create_ride = baseurl + "ride/create-city-ride"
     var  Selectcityvehicletype = baseurl + "ride/select-city-vehicle-type"
     var Searching_driver= baseurl+"ride/searching-driver"
     var CHECK_RIDE_REQUEST_STATUS= baseurl+"ride/check-ride-request-status"
     var ACCEPT_CURRENT_CITYRIDE= baseurl+"ride/accept-current-city-ride"
     var UPDATE_CITY_RIDE_PAYMENT_STATUS= baseurl+"ride/update-city-ride-payment-status"
     var ADVANCE_UPDATE_CITY_RIDE_PAYMENT_STATUS= baseurl+"advance-ride/update-advance-city-ride-payment-status"
     var ENTER_PASSENGER_DETAILS= baseurl+"ride/enter-passenger-details"
     var RIDE_HISTORY= baseurl+"user/ride-history"
     var check_status= baseurl+"check-status"
     var customer_booking_list= baseurl+"driver-ride/get-city-ride-request"
     var reject_city_ride_request= baseurl+"driver-ride/reject-city-ride-request"
     var accept_city_ride_request= baseurl+"driver-ride/accept-city-ride-request"
     var check_ride_otp= baseurl+"driver-ride/check-ride-otp"
     var get_all_details= baseurl+"driver/get-all-details"
     var get_cab_work_details= baseurl+"driver/get-cab-work-details"
     var ride_history= baseurl+"driver/ride-history"
     var ride_delete = baseurl+"ride/ride-delete"
     var EMERGENCY = baseurl+"emergency"

     var Ride_Status = baseurl+"ride/check-ride-status"
     var SUBMIT_RATING = baseurl+"submit-rating"
     var UPDATE_PROFILE = baseurl+"update-user-profile"
     var USER_PROFILE = baseurl+"get-user-profile"
//oneway
    var ONEWAY_CREATE_RIDE = baseurl+"Oneway/create-oneway-ride"
    var Vehicle_OneWay = baseurl+"Oneway/select-outstation-vehicle-type"



}