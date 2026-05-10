package com.practicum.playlistmaker.data.mapper

import com.practicum.playlistmaker.data.network.dto.TrackDto
import com.practicum.playlistmaker.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

fun TrackDto.toDomain(): Track = Track(
    trackName = trackName,
    artistName = artistName,
    trackTimeMillis = trackTimeMillis,
    artworkUrl100 = artworkUrl100,
)
