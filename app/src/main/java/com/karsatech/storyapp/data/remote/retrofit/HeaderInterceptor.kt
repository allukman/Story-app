package com.karsatech.storyapp.data.remote.retrofit

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(private val context : Context) : Interceptor {

//    private lateinit var sharedPref : PreferencesHelper

    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
//        sharedPref = PreferencesHelper(context)

//        val tokenAuth = sharedPref.getValueString(Constant.prefUserToken)
        proceed(
            request().newBuilder()
                .addHeader("Authorization", "Bearer ")
                .header("Connection", "close")
                .removeHeader("Content-Length")
                .build()
        )
    }
}