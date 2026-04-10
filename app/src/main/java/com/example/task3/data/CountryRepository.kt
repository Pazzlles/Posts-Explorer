package com.example.task3.data

import com.example.task3.data.model.CountryDetail
import com.example.task3.data.model.CountryListItem

interface CountryRepository {
    suspend fun getCountries(query: String): List<CountryListItem>
    suspend fun getCountryDetail(code: String): CountryDetail
}
