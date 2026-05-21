package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.network.api.TrackApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object NetworkService {
    private const val BASE_URL = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val tracksApiService = retrofit.create<TrackApi>()
}