package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val PLAYLIST_THEME = "playlist_theme"

class App : Application() {
    var darkTheme = false
    private val sharedPrefs by lazy { getSharedPreferences(PLAYLIST_THEME, MODE_PRIVATE) }

    private fun android.content.Context.isDarkTheme(): Boolean {
        return resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    override fun onCreate() {
        super.onCreate()
        darkTheme = if(sharedPrefs.contains(PLAYLIST_THEME)) {
            sharedPrefs.getBoolean(PLAYLIST_THEME, false)
        } else {
            isDarkTheme()
        }
        // Применяем сохранённую тему при каждом запуске
        applyTheme(darkTheme)
    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        applyTheme(darkThemeEnabled)
        sharedPrefs
            .edit()
            .putBoolean(PLAYLIST_THEME, darkThemeEnabled)
            .apply()
    }
}