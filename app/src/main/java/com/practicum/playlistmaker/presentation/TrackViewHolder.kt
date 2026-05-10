package com.practicum.playlistmaker.presentation

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale


class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackNameView: TextView = itemView.findViewById(R.id.track_name)
    private val artistNameTextView: TextView = itemView.findViewById(R.id.track_artist)
    private val trackTimeTextView: TextView = itemView.findViewById(R.id.track_time)

    private val trackImageView: ImageView = itemView.findViewById(R.id.track_image)

    private val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
    fun bind(model: Track) {
        trackNameView.text = model.trackName
        artistNameTextView.text = model.artistName
        trackTimeTextView.text = formatter.format(model.trackTimeMillis)


        Glide
            .with(itemView.context)
            .load(model.artworkUrl100)
            .centerInside()
            .transform(RoundedCorners(dpToPx(2.0f, itemView.context)))
            .placeholder(R.drawable.placeholder)
            .into(trackImageView)
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}