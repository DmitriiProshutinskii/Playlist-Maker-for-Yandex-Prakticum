package com.practicum.playlistmaker

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
                if (searchValue.isBlank()) {
                    adapter.updateTracks(emptyList())
                } else {


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
                            val errorJson = response.errorBody()?.string()
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
            false
        }
        searchEditText.doOnTextChanged { s, _, _, _ ->
            clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            searchValue = s.toString()
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

    private fun changeState(state: SearchScreenStates) {
        val recyclerView = findViewById<RecyclerView>(R.id.search_content)
        val placeholderNotFound = findViewById<LinearLayout>(R.id.search_placeholderNotFound)
        val placeholderError = findViewById<LinearLayout>(R.id.search_placeholderError)

        when(state) {
            SearchScreenStates.SUCCESS -> {
                recyclerView.visibility = View.VISIBLE
                placeholderNotFound.visibility = View.GONE
                placeholderError.visibility = View.GONE
            }
            SearchScreenStates.NOT_FOUND -> {
                recyclerView.visibility = View.GONE
                placeholderNotFound.visibility = View.VISIBLE
                placeholderError.visibility = View.GONE
            }
            SearchScreenStates.FAILURE -> {
                recyclerView.visibility = View.GONE
                placeholderNotFound.visibility = View.GONE
                placeholderError.visibility = View.VISIBLE
            }
        }
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
    SUCCESS,
    NOT_FOUND,
    FAILURE
}