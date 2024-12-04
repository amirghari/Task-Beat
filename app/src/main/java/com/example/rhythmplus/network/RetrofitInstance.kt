//package com.example.rhythmplus.network
//
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//object RetrofitInstance {
//    private const val BASE_URL = "https://users.metropolia.fi/~khaica/parliament_members/"
//
//    val api: DataApi by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(DataApi::class.java)
//    }
//}