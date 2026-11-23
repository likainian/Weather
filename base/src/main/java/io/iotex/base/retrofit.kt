package io.iotex.base

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    fun <T> createApiService(host: String, cls: Class<T>): T {
        val builder = Retrofit.Builder()
            .baseUrl(host)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))

        return builder.build().create(cls)
    }

    fun <T> createOriginalApiService(host: String, cls: Class<T>): T {
        return Retrofit.Builder()
            .baseUrl(host)
            .client(okHttpClient)
            .build().create(cls)
    }
}