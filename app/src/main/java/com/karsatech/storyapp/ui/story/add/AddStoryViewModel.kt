package com.karsatech.storyapp.ui.story.add

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.karsatech.storyapp.data.remote.response.RegisterResponse
import com.karsatech.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class AddStoryViewModel : ViewModel(), CoroutineScope {

    private lateinit var service: ApiService

    private val _success = MutableLiveData<RegisterResponse>()
    val success: LiveData<RegisterResponse> = _success

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
        private const val TAG = "AddStoryViewModel"
    }

    fun addStory(photo: MultipartBody.Part, desc: RequestBody) {
        _isLoading.value = true
        launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    service.addNewStory(photo, desc)
                }

                result.enqueue(object : Callback<RegisterResponse> {
                    override fun onResponse(
                        call: Call<RegisterResponse>,
                        response: Response<RegisterResponse>
                    ) {
                        _isLoading.value = false
                        if (response.isSuccessful) {
                            _success.value = response.body()
                        } else {
                            _mError.value = response.message()
                            Log.e(TAG, "onFailure: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
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
}