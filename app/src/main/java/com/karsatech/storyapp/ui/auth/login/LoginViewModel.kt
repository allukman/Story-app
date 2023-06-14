package com.karsatech.storyapp.ui.auth.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.karsatech.storyapp.data.remote.response.LoginResponse
import com.karsatech.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class LoginViewModel: ViewModel(), CoroutineScope {
    private lateinit var service: ApiService

    private val _login = MutableLiveData<LoginResponse>()
    val login: LiveData<LoginResponse> = _login

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
        private const val TAG = "LoginViewModel"
    }

    fun loginUser(email: String, password: String) {
        _isLoading.value = true
        launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    service.loginUser(email, password)
                }

                result.enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        _isLoading.value = false
                        if (response.isSuccessful) {
                            _login.value = response.body()
                        } else {
                            _mError.value = response.message()
                            Log.d(TAG, "sini 1")
                            Log.d(TAG, "msg $response")
                            Log.e(TAG, "onFailure: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        _isLoading.value = false
                        Log.e(TAG, "onFailure: ${t.message.toString()}")
                        Log.d(TAG, "sini 2")
                    }

                })
            } catch (e: Throwable) {
                Log.e(TAG, "onFailure: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }
}