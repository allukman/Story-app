package com.karsatech.storyapp.ui.story.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.karsatech.storyapp.data.remote.response.DetailStory
import com.karsatech.storyapp.data.remote.response.LoginResult
import com.karsatech.storyapp.data.remote.response.StoryResponse
import com.karsatech.storyapp.data.remote.retrofit.ApiService
import com.karsatech.storyapp.utils.UserPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class MainViewModel(private val pref: UserPreference): ViewModel(), CoroutineScope {

    private lateinit var service: ApiService

    private val _stories = MutableLiveData<List<DetailStory>>()
    val stories: LiveData<List<DetailStory>> = _stories

    private val _mError = MutableLiveData<String>()
    val mError: LiveData<String> = _mError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Main

    fun setService(service: ApiService) {
        this.service = service
    }


    companion object {
        private const val TAG = "MainViewModel"
    }

    fun getAllStories() {
        _isLoading.value = true
        launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    service.getAllStories()
                }

                result.enqueue(object : Callback<StoryResponse> {
                    override fun onResponse(
                        call: Call<StoryResponse>,
                        response: Response<StoryResponse>
                    ) {
                        _isLoading.value = false
                        if (response.isSuccessful) {
                            _stories.value = response.body()?.listStory
                        } else {
                            _mError.value = response.message()
                            Log.e(TAG, "onFailure: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                        _isLoading.value = false
                        Log.e(TAG, "onFailure: ${t.message.toString()}")
                    }

                })
            } catch (e: Throwable) {
                Log.e(TAG, "onFailure: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }

    fun getUser(): LiveData<LoginResult> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.clearData()
        }
    }
}