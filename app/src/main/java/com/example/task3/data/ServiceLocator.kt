package com.example.task3.data

import com.example.task3.data.remote.CountryApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

object ServiceLocator {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val countryApiService: CountryApiService by lazy {
        retrofit.create(CountryApiService::class.java)
    }

    val countryRepository: CountryRepository by lazy {
        CountryRepositoryImpl(countryApiService)
    }
}
