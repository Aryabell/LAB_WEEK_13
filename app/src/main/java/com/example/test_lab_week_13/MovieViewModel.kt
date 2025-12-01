package com.example.test_lab_week_13

import androidx.lifecycle.ViewModel
import com.example.test_lab_week_13.model.Movie
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {
    // define the StateFlow in replace of the LiveData
    // a StateFlow is an observable Flow that emits state updates to the collectors
    // MutableStateFlow is a StateFlow that you can change the value
    private val _popularMovies = MutableStateFlow(
        emptyList<Movie>()
    )
    val popularMovies: StateFlow<List<Movie>> = _popularMovies
    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    init {
        fetchPopularMovies()
    }

    // fetch movies from the API
    private fun fetchPopularMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            movieRepository.fetchMovies()
                .catch { e ->
                    _error.value = "An exception occurred: ${e.message}"
                }
                .collect { movies ->
                    val sortedMovies = movies.sortedByDescending { it.popularity }

                    // update StateFlow
                    _popularMovies.value = sortedMovies
                }
        }
    }
}