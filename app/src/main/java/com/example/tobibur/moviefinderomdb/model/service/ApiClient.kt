package com.example.tobibur.moviefinderomdb.model.service

import com.example.tobibur.moviefinderomdb.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class ApiClient{

    private val baseURL = "http://www.omdbapi.com"
    private var retrofit : Retrofit? = null

    private var logging = HttpLoggingInterceptor()

    private fun getHttpLogClient() : OkHttpClient{
        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
    }

    fun getClient(): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getHttpLogClient())
                    .build()
        }
        return retrofit
    }
}