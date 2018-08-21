package com.example.tobibur.moviefinderomdb.model.service

import com.example.tobibur.moviefinderomdb.model.Movie
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiInterface {

    @GET("/")
    fun getMovie(@Query("t")movie_title: String ,@Query("apikey")api_key: String): Call<Movie>
}
