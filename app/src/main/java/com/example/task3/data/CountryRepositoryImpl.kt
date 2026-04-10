package com.example.task3.data

import com.example.task3.data.model.CountryDetail
import com.example.task3.data.model.CountryListItem
import com.example.task3.data.remote.CountryApiService
import com.example.task3.data.remote.dto.toCountryDetail
import com.example.task3.data.remote.dto.toCountryListItemOrNull

class CountryRepositoryImpl(
    private val apiService: CountryApiService,
) : CountryRepository {

    override suspend fun getCountries(query: String): List<CountryListItem> {
        val countries = if (query.isBlank()) {
            apiService.getAllCountries()
        } else {
            query.trim().toIntOrNull()?.let { userId ->
                apiService.searchCountriesByName(userId = userId)
            }.orEmpty()
        }

        return countries
            .mapNotNull { it.toCountryListItemOrNull() }
            .sortedBy { it.id }
    }

    override suspend fun getCountryDetail(code: String): CountryDetail {
        val postId = code.trim().toIntOrNull()
            ?: throw IllegalStateException("Некорректный идентификатор поста: $code")

        return apiService.getCountryDetail(postId).toCountryDetail()
    }
}
