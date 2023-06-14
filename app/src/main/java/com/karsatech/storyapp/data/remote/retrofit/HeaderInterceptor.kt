package com.karsatech.storyapp.data.remote.retrofit

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.karsatech.storyapp.utils.UserPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class HeaderInterceptor(private val context : Context) : Interceptor {

    private val userPreference: UserPreference = UserPreference.getInstance(context.dataStore)

    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        val token = userPreference.getToken().first()

        Log.d("HeaderInterceptor", token)

        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .header("Connection", "close")
            .removeHeader("Content-Length")
            .build()

        chain.proceed(request)
    }
//    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
//
//        val token = userPreference.getToken().first()
//
//
//        proceed(
//            request().newBuilder()
//                .addHeader("Authorization", "Bearer ")
//                .header("Connection", "close")
//                .removeHeader("Content-Length")
//                .build()
//        )
//    }
}