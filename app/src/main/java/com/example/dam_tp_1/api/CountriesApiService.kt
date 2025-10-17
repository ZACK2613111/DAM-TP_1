package com.example.dam_tp_1.api

import com.example.dam_tp_1.data.Country
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface CountriesApiService {
    @GET("all?fields=name,cca2,flags")
    suspend fun getAllCountries(): List<Country>
    companion object {
        private const val BASE_URL = "https://restcountries.com/v3.1/"

        fun create(): CountriesApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CountriesApiService::class.java)
        }
    }
}
