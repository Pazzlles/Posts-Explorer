package com.example.task3.data.remote

import com.example.task3.data.remote.dto.CountryResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CountryApiService {
    @GET("posts")
    suspend fun getAllCountries(): List<CountryResponseDto>

    @GET("posts")
    suspend fun searchCountriesByName(
        @Query("userId") userId: Int,
    ): List<CountryResponseDto>

    @GET("posts/{id}")
    suspend fun getCountryDetail(
        @retrofit2.http.Path("id") id: Int,
    ): CountryResponseDto
}
