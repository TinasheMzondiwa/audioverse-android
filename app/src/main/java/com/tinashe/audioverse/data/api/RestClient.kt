package com.tinashe.audioverse.data.api

import com.google.gson.GsonBuilder
import com.tinashe.audioverse.BuildConfig
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RestClient {

    private const val TIMEOUT_SECONDS = 60L
    private const val AUTHORIZATION_HEADER = "Authorization"

    private val client: OkHttpClient
        get() {
            val clientBuilder = OkHttpClient.Builder()
            clientBuilder.readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            clientBuilder.connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)

            if (BuildConfig.DEBUG) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                clientBuilder.addInterceptor(interceptor)
            }

            clientBuilder.addInterceptor {

                val original = it.request()
                val request = original.newBuilder()

                request.addHeader(AUTHORIZATION_HEADER, "Basic ${BuildConfig.API_TOKEN}")

                request.method(original.method(), original.body())
                it.proceed(request.build())
            }

            return clientBuilder.build()
        }

    private val retrofit: Retrofit
        get() {
            val gson = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create()
            return Retrofit.Builder()
                    .baseUrl(BuildConfig.URL_BASE)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .client(client)
                    .build()
        }

    fun <T> createService(service: Class<T>): T {
        return retrofit.create(service)
    }
}