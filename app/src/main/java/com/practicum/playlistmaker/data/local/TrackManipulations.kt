package com.practicum.playlistmaker.data.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.domain.model.Track

// Если вдруг в будущем нам нужно будет хранить это не в shared Prefs, мы
// перенесем это в network слой. Пока что сделал так, чтобы дать интерактивность,
// но работаь с этим в Activity
class TrackManipulations(val sharedPrefs: SharedPreferences) {
    private val tracksLiked: MutableSet<String> = mutableSetOf()

    init {
        restore()
    }

    fun isLiked(track: Track): Boolean {
        return tracksLiked.contains(track.trackId)
    }

    fun tapLikeOnTrack(track: Track) {
        if (tracksLiked.contains(track.trackId)) {
            tracksLiked.remove(track.trackId)
        } else {
            tracksLiked.add(track.trackId)
        }
        save()
    }

    private fun save() {
        val json = Gson().toJson(tracksLiked)
        sharedPrefs.edit().putString(LIKED_TRACKS_KEY, json).apply()
    }

    private fun restore() {
        val json = sharedPrefs.getString(LIKED_TRACKS_KEY, null) ?: return
        val array = Gson().fromJson(json, Array<String>::class.java) ?: return
        tracksLiked.addAll(array)
    }

    companion object {
        private const val LIKED_TRACKS_KEY = "LIKED_TRACKS_KEY"
    }
}