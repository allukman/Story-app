package com.karsatech.storyapp.di

import android.content.Context
import com.karsatech.storyapp.data.remote.StoryRepository
import com.karsatech.storyapp.data.remote.retrofit.ApiConfig
import com.karsatech.storyapp.data.remote.retrofit.ApiService

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiClient(context)!!.create(ApiService::class.java)
        return StoryRepository(apiService)
    }
}