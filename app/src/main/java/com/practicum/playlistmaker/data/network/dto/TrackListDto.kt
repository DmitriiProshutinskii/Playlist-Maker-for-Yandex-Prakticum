package com.practicum.playlistmaker.data.network.dto

data class TrackListDto(val resultCount: Int, val results: List<TrackDto>)

data class TrackDto(val trackName: String, val artistName: String, val trackTimeMillis: Int, val artworkUrl100: String)