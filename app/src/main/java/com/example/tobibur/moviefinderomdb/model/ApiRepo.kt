package com.example.tobibur.moviefinderomdb.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.example.tobibur.moviefinderomdb.model.service.ApiClient
import com.example.tobibur.moviefinderomdb.model.service.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ApiRepo{

    var endpoints : ApiClient = ApiClient()

    fun getPosts(movieTitle: String) : LiveData<ApiResponse> {
        val apiResponse = MutableLiveData<ApiResponse>()
        val apiService = endpoints.getClient()!!.create(ApiInterface::class.java)
        val call : Call<Movie> = apiService.getMovie(movieTitle,"35ba1dc9")
        call.enqueue(object : Callback<Movie>{
            override fun onFailure(call: Call<Movie>?, t: Throwable?) {
                apiResponse.postValue(ApiResponse(t!!))
            }

            override fun onResponse(call: Call<Movie>?, response: Response<Movie>?) {
                if (response!!.isSuccessful) {
                    apiResponse.postValue(ApiResponse(response.body()!!))
                }
            }

        })

        return apiResponse
    }
}