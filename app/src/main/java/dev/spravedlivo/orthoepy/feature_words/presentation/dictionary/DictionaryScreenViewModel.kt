package dev.spravedlivo.orthoepy.feature_words.presentation.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.spravedlivo.orthoepy.App
import dev.spravedlivo.orthoepy.core.domain.viewModelFactory
import dev.spravedlivo.orthoepy.feature_words.data.local.WordsDao
import dev.spravedlivo.orthoepy.feature_words.domain.model.WordInfoItem
import dev.spravedlivo.orthoepy.feature_words.domain.model.WordRecord
import dev.spravedlivo.orthoepy.feature_words.domain.repository.WordInfoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation

class DictionaryScreenViewModel(
    private val wordsDao: WordsDao,
    private val wordInfoRepository: WordInfoRepository
): ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _loaded = MutableStateFlow(false)
    val loaded = _loaded.asStateFlow()

    private val _allWords = MutableStateFlow(listOf<WordInfoItem>())
    private val _filteredWords = MutableStateFlow(listOf<WordInfoItem>())
    val filteredWords = _filteredWords.asStateFlow()
    private val _wordRecords = MutableStateFlow(mapOf<Int, WordRecord>())


    private var searchJob: Job? = null

    fun loadWords() {
        viewModelScope.launch {
            suspend {
                _loading.value = true
                _filteredWords.value = wordInfoRepository.getWordInfo().toList()
                _allWords.value = wordInfoRepository.getWordInfo().toList()
                _wordRecords.value = wordsDao.getAllWordEntities().associate { it.id to it.toWordRecord() }
                _loaded.value = true
            }()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun searchWords(query: String) = suspendCancellableCoroutine { continuation ->
        val queryTransformed = query.lowercase()
        val it2 = _allWords.value.filter {
            it.word.lowercase().startsWith(queryTransformed) && _wordRecords.value.containsKey(it.id)
        }.sortedBy {
            _wordRecords.value[it.id]!!.lastIncorrect
        }
        continuation.resume(it2) {}
    }


    fun onSearch(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            _filteredWords.value = if (_searchQuery.value.isBlank())
                _allWords.value else
                    searchWords(_searchQuery.value)
        }
    }

    companion object {
        val factory = viewModelFactory {
            DictionaryScreenViewModel(App.appModule.wordsDb.dao, App.appModule.wordInfoRepository)
        }
    }
}