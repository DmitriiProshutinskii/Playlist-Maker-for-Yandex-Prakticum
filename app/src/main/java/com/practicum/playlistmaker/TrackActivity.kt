package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackActivity : AppCompatActivity() {

    private val durationFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_track)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.track_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }

        val trackJson = intent.getStringExtra(EXTRA_TRACK)
        if (trackJson == null) {
            finish()
            return
        }
        bind(Gson().fromJson(trackJson, Track::class.java))
    }

    private fun bind(track: Track) {
        Glide
            .with(this)
            .load(track.getCoverArtwork())
            .transform(CenterCrop(), RoundedCorners((8 * resources.displayMetrics.density).toInt()))
            .placeholder(R.drawable.placeholder)
            .into(findViewById(R.id.track_artwork))

        findViewById<TextView>(R.id.track_name).text = track.trackName
        findViewById<TextView>(R.id.track_artist).text = track.artistName

        bindInfoRow(R.id.duration_row, R.string.trackDuration, durationFormatter.format(track.trackTimeMillis))
        bindInfoRow(R.id.album_row, R.string.trackAlbum, track.collectionName)
        bindInfoRow(R.id.year_row, R.string.trackYear, track.releaseDate?.take(4))
        bindInfoRow(R.id.genre_row, R.string.trackGenre, track.primaryGenreName)
        bindInfoRow(R.id.country_row, R.string.trackCountry, track.country)
    }

    // Поле может прийти пустым, поэтому заполняем visibility через этот метод
    private fun bindInfoRow(rowId: Int, labelRes: Int, value: String?) {
        val row = findViewById<View>(rowId)
        if (value.isNullOrBlank()) {
            row.visibility = View.GONE
        } else {
            row.visibility = View.VISIBLE
            row.findViewById<TextView>(R.id.info_label).text = getString(labelRes)
            row.findViewById<TextView>(R.id.info_value).text = value
        }
    }

    companion object {
        const val EXTRA_TRACK = "EXTRA_TRACK"
    }
}
