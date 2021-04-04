package com.example.tawktest.Api

import android.util.Log
import com.example.tawktest.DataModels.UserDetailModel
import com.example.tawktest.DataModels.UserModel
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.HTTP
import retrofit2.http.Headers
import retrofit2.http.Path
import java.util.concurrent.TimeUnit


interface API {


    @Headers("Content-Type: application/json", "Accept: /")
    @HTTP(method = "GET", path = "/users?since=0")
    fun getUserList(): Observable<Response<MutableList<UserModel>>>

    @Headers("Content-Type: application/json", "Accept: /")
    @HTTP(method = "GET", path = "/users/{userName}")
    fun getUserDetail(@Path("userName") userName: String): Observable<Response<UserDetailModel>>

    companion object APIFactory {
        fun create(): API {
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor {
                    val newRequest: Request = it.request().newBuilder()
                        .addHeader("Connection", "close")
                        .build()
                    it.proceed(newRequest)
                }.build()


            val gson = GsonBuilder()
                .serializeNulls()
                .create()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()

            return retrofit.create(API::class.java)
        }
    }
}