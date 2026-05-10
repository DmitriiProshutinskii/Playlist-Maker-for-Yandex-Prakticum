package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.presentation.TrackAdapter
import com.practicum.playlistmaker.presentation.mockTracks

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var adapter: TrackAdapter
    private var searchValue: String = SEARCH_DEF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<MaterialToolbar>(R.id.search_toolbar).setOnClickListener {
            finish()
        }

        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)

        clearButton.setOnClickListener {
            searchEditText.setText("")
            hideKeyboard()
        }

        adapter = TrackAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.search_content)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchEditText.requestFocus()
        showKeyboard()

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // TODO: ВЫПОЛНЯЙТЕ ПОИСКОВЫЙ ЗАПРОС ЗДЕСЬ
                true
            }
            false
        }
        searchEditText.doOnTextChanged { s, _, _, _ ->
            clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            searchValue = s.toString()
            val filtered = if (searchValue.isBlank()) {
                emptyList()
            } else {
                mockTracks.filter {
                    it.trackName.contains(searchValue, ignoreCase = true) ||
                        it.artistName.contains(searchValue, ignoreCase = true)
                }
            }
            adapter.updateTracks(filtered)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_VALUE, searchValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchValue = savedInstanceState.getString(SEARCH_VALUE, SEARCH_DEF)
        searchEditText.setText(searchValue)
    }

    private fun showKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    companion object {
        const val SEARCH_VALUE = "SEARCH_VALUE"
        const val SEARCH_DEF = ""
    }
}