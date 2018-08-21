package com.example.tobibur.moviefinderomdb.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import com.example.tobibur.moviefinderomdb.model.ApiRepo
import com.example.tobibur.moviefinderomdb.model.ApiResponse

class ResultViewModel : ViewModel(){
    private val mApiResponse: MediatorLiveData<ApiResponse> = MediatorLiveData()
    private val mApiRepo: ApiRepo = ApiRepo()

    fun getData(title: String): LiveData<ApiResponse> {
        mApiResponse.addSource(mApiRepo.getPosts(title)) {
            mApiResponse.value = it
        }

        return mApiResponse
    }
}