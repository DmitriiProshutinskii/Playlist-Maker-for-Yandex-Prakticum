package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.practicum.playlistmaker.data.local.SearchHistory
import com.practicum.playlistmaker.domain.model.Track
import com.practicum.playlistmaker.data.mapper.toDomain
import com.practicum.playlistmaker.data.network.NetworkService
import com.practicum.playlistmaker.data.network.dto.TrackListDto
import retrofit2.Callback
import com.practicum.playlistmaker.presentation.TrackAdapter
import retrofit2.Call
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var adapter: TrackAdapter
    private var searchValue: String = SEARCH_DEF

    private lateinit var searchHistory: SearchHistory
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var historyLayout: View

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

        searchHistory = SearchHistory(getSharedPreferences(PLAYLIST_PREFERENCES, MODE_PRIVATE))

        // Список результатов поиска. При тапе по треку кладём его в историю
        adapter = TrackAdapter { track ->
            searchHistory.addToHistory(track)
            openTrack(track)
        }
        val recyclerView = findViewById<RecyclerView>(R.id.search_content)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Список истории: свой адаптер, но та же вёрстка item'а и тот же тап
        historyAdapter = TrackAdapter { track ->
            searchHistory.addToHistory(track)
            openTrack(track)
        }
        historyLayout = findViewById(R.id.search_history_layout)
        val historyRecycler = findViewById<RecyclerView>(R.id.search_history)
        historyRecycler.layoutManager = LinearLayoutManager(this)
        historyRecycler.adapter = historyAdapter

        findViewById<MaterialButton>(R.id.clear_history_button).setOnClickListener {
            searchHistory.clear()
            historyLayout.visibility = View.GONE
        }

        searchEditText.setOnFocusChangeListener { _, _ ->
            renderHistory()
        }

        searchEditText.requestFocus()
        showKeyboard()

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
            }
            false
        }
        searchEditText.doOnTextChanged { s, _, _, _ ->
            clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            searchValue = s.toString()
            if (searchValue.isBlank()) {
                adapter.updateTracks(emptyList())
            }
            renderHistory()
        }
        val updateButton = findViewById<MaterialButton>(R.id.update_button)
        updateButton.setOnClickListener {
            searchTrack()
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

    private fun searchTrack() {
        if (!searchValue.isBlank()) {
            NetworkService.tracksApiService.search(searchValue).enqueue(object : Callback<TrackListDto>{
                override fun onResponse(call: Call<TrackListDto>, response: Response<TrackListDto>) {
                    // Получили ответ от сервера
                    if (response.isSuccessful) {
                        // Наш запрос был удачным, получаем наши объекты
                        val tracks = response.body()
                        if (tracks == null) {
                            changeState(SearchScreenStates.FAILURE)
                        } else {
                            if (tracks.resultCount == 0) {
                                changeState(SearchScreenStates.NOT_FOUND)
                            } else {
                                changeState(SearchScreenStates.SUCCESS)
                                adapter.updateTracks(tracks.results.map { it.toDomain() })
                            }
                        }

                    } else {
                        // Сервер отклонил наш запрос с ошибкой
                        changeState(SearchScreenStates.FAILURE)
                    }
                }

                override fun onFailure(call: Call<TrackListDto>, t: Throwable) {
                    // Не смогли присоединиться к серверу
                    // Выводим ошибку в лог, что-то пошло не так
                    t.printStackTrace()
                    changeState(SearchScreenStates.FAILURE)
                }
            })
        }
    }

    private fun changeState(state: SearchScreenStates) {
        val historyView = findViewById<View>(R.id.search_history_layout)
        val recyclerView = findViewById<RecyclerView>(R.id.search_content)
        val placeholderNotFound = findViewById<LinearLayout>(R.id.search_placeholderNotFound)
        val placeholderError = findViewById<LinearLayout>(R.id.search_placeholderError)

        when(state) {
            SearchScreenStates.EMPTY -> {
                historyView.visibility = View.GONE
                recyclerView.visibility = View.GONE
                placeholderNotFound.visibility = View.GONE
                placeholderError.visibility = View.GONE
            }
            SearchScreenStates.HISTORY -> {
                historyView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                placeholderNotFound.visibility = View.GONE
                placeholderError.visibility = View.GONE
            }
            SearchScreenStates.SUCCESS -> {
                historyView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                placeholderNotFound.visibility = View.GONE
                placeholderError.visibility = View.GONE
            }
            SearchScreenStates.NOT_FOUND -> {
                historyView.visibility = View.GONE
                recyclerView.visibility = View.GONE
                placeholderNotFound.visibility = View.VISIBLE
                placeholderError.visibility = View.GONE
            }
            SearchScreenStates.FAILURE -> {
                historyView.visibility = View.GONE
                recyclerView.visibility = View.GONE
                placeholderNotFound.visibility = View.GONE
                placeholderError.visibility = View.VISIBLE
            }
        }
    }

    private fun renderHistory() {
        val history = searchHistory.getSearchHistory()
        val canShowHistory = searchEditText.text.isNullOrEmpty()
                && searchEditText.hasFocus()
                && history.isNotEmpty()

        when {
            // поле пустое, в фокусе, история есть — показываем историю
            canShowHistory -> {
                historyAdapter.updateTracks(history.reversed())
                changeState(SearchScreenStates.HISTORY)
            }
            // поле пустое, но истории нет или фокус ушёл — чистый экран
            searchEditText.text.isNullOrEmpty() -> changeState(SearchScreenStates.EMPTY)
            // поле НЕ пустое: если была видна история — убираем её (пользователь
            // начал печатать). Результаты поиска при этом не трогаем.
            historyLayout.visibility == View.VISIBLE -> changeState(SearchScreenStates.EMPTY)
        }
    }

    private fun openTrack(track: Track) {
        val intent = Intent(this, TrackActivity::class.java)
        intent.putExtra(TrackActivity.EXTRA_TRACK, Gson().toJson(track))
        startActivity(intent)
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

enum class SearchScreenStates {
    EMPTY,
    HISTORY,
    SUCCESS,
    NOT_FOUND,
    FAILURE
}