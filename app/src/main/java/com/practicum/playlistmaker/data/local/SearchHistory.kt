package com.practicum.playlistmaker.data.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.domain.model.Track

const val searchHistoryKey = "SEARCH_HISTORY_KEY"

class SearchHistory(val sharedPref: SharedPreferences) {
    val history: MutableList<Track> = mutableListOf()

    init {
        restore()
    }

    fun getSearchHistory() : List<Track> {
        return history
    }

    fun addToHistory(track: Track) {
        history.removeIf { it.trackId == track.trackId }
        history.add(track)
        if (history.size > 10) {
            history.removeAt(0)
        }
        save()
    }

    fun clear() {
        history.clear()
        save()
    }

    private fun save() {
        val json = Gson().toJson(history, Array<Track>::class.java)
        sharedPref.edit().putString(searchHistoryKey, json).apply()
    }

    private fun restore() {
        val json = sharedPref.getString(searchHistoryKey, null) ?: return
        val array = Gson().fromJson(json, Array<Track>::class.java) ?: return
        history.addAll(array)
    }
}