package com.practicum.playlistmaker.domain

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.practicum.playlistmaker.R


class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackNameView: TextView = itemView.findViewById(R.id.track_name)
    private val artistNameTextView: TextView = itemView.findViewById(R.id.track_artist)
    private val trackTimeTextView: TextView = itemView.findViewById(R.id.track_time)

    private val trackImageView: ImageView = itemView.findViewById(R.id.track_image)

    fun bind(model: Track) {
        trackNameView.text = model.trackName
        artistNameTextView.text = model.artistName
        trackTimeTextView.text = model.trackTime

        Glide
            .with(itemView.context)
            .load(model.artworkUrl100)
            .centerInside()
            .transform(RoundedCorners(dpToPx(2.0f, itemView.context)))
            .into(trackImageView)
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}