package com.project.sns.data.module

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FCMSend {

    @Headers(value = ["Authorization:key=AAAApjITQYU:APA91bHKlLumCQAdNuVhiE4fCL3WjX0aP_3qvYCyqyfUImT3_P0HVFPW32o5GuEWGz8HQD1-TWZWwMwoHGMbZgsKVwmVxs4ZISJ-T1GlDXBFBxdzQEVmEJi0awIushvd9vDdrblkFDqT"
        , "Content-Type:application/json"])
    @POST("fcm/send")
    fun postFCM(@Body bodyRaw: ReqDataJson) : Call<ResData>
}