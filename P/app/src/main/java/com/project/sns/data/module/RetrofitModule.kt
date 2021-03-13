package com.project.sns.data.module

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var instance : Retrofit? = null
    private val gson = GsonBuilder().setLenient().create()

    fun getInstnace() : Retrofit {
        if(instance == null){
            instance = Retrofit.Builder()
                    .baseUrl("https://fcm.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        return instance!!
    }
}

data class ReqDataJson(var data: ReqData?, var to: String?, var direct_book_ok : Boolean = true)
data class ResData(var canonical_ids: String?, var success : String?, var failure : String?, val results: List<Results>?, var multicast_id : String?)
data class ReqData(var score: String = "5x1", var time: String = "15:10")

data class Results(var message_id : String?, var error : String?)
