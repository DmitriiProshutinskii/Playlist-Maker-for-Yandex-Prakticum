package com.practicum.playlistmaker.data.mapper

import com.practicum.playlistmaker.data.network.dto.TrackDto
import com.practicum.playlistmaker.domain.model.Track

fun TrackDto.toDomain(): Track = Track(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    trackTimeMillis = trackTimeMillis,
    artworkUrl100 = artworkUrl100,
)
