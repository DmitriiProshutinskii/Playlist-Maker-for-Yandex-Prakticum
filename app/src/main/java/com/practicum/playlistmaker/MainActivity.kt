package com.practicum.playlistmaker

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

        val searchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на поиск!", Toast.LENGTH_SHORT).show()
            }
        }
        search.setOnClickListener(searchClickListener)

        media.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на Медиа!", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.settings_button -> {
                Toast.makeText(this, "Нажали на настройки!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}