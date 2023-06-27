package com.karsatech.storyapp.data.remote.retrofit

import com.karsatech.storyapp.data.remote.response.LoginResponse
import com.karsatech.storyapp.data.remote.response.RegisterResponse
import com.karsatech.storyapp.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

//    @FormUrlEncoded
//    @POST("register")
//    fun registerUser(
//        @Field("name") name: String,
//        @Field("email") email: String,
//        @Field("password") password: String
//    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("register")
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

//    @FormUrlEncoded
//    @POST("login")
//    fun loginUser(
//        @Field("email") email: String,
//        @Field("password") password: String
//    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories?location=1")
    fun getAllStories(): Call<StoryResponse>

    @GET("stories")
    suspend fun getAllStoriesPagination(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): RegisterResponse
}