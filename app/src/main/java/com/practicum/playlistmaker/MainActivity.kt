package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val search = findViewById<MaterialButton>(R.id.search_button)
        val media = findViewById<MaterialButton>(R.id.media_button)
        val settings = findViewById<MaterialButton>(R.id.settings_button)

        val searchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val searchIntent = Intent(v?.context, Search::class.java)
                startActivity(searchIntent)
            }
        }
        search.setOnClickListener(searchClickListener)

        media.setOnClickListener {
            val mediaIntent = Intent(this, Media::class.java)
            startActivity(mediaIntent)
        }

        settings.setOnClickListener {
            val settingsIntent = Intent(this, Settings::class.java)
            startActivity(settingsIntent)
        }

    }

    override fun onClick(p0: View?) {
//        when (p0?.id) {
//            R.id.settings_button -> {
//                val settingsIntent = Intent(this, Settings::class.java)
//                startActivity(settingsIntent)
//            }
//        }
    }
}