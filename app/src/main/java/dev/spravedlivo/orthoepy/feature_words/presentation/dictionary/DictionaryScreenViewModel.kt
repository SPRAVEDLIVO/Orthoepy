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

class WordFilterPredicate(
    private val wordRecords: Map<Int, WordRecord>,
    private val query: String
) {
    private fun visited(record: WordRecord): Boolean {
        return record.lastIncorrect || record.correctHits != 0
    }

    fun filterBlankQuery(it: WordInfoItem): Boolean {
        val record = wordRecords.getOrDefault(it.id, null) ?: return false
        return visited(record)
    }

    fun filterNotBlank(it: WordInfoItem): Boolean {
        val record = wordRecords.getOrDefault(it.id, null) ?: return false
        return visited(record) && it.word.lowercase().startsWith(query)
    }
}

class DictionaryScreenViewModel(
    private val wordsDao: WordsDao,
    private val wordInfoRepository: WordInfoRepository
) : ViewModel() {

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
    val wordRecords = _wordRecords.asStateFlow()

    private var searchJob: Job? = null

    fun loadWords() {
        viewModelScope.launch {
            suspend {
                _loading.value = true
                _allWords.value = wordInfoRepository.getWordInfo().toList()
                _wordRecords.value =
                    wordsDao.getAllWordEntities().associate { it.id to it.toWordRecord() }
                _filteredWords.value = searchWords("")
                _loaded.value = true
            }()
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun searchWords(query: String) = suspendCancellableCoroutine { continuation ->
        val queryTransformed = query.lowercase()
        val instance = WordFilterPredicate(_wordRecords.value, queryTransformed)
        val predicate = when (query.isBlank()) {
            true -> instance::filterBlankQuery
            false -> instance::filterNotBlank
        }
        _allWords.value.filter(predicate).groupBy {
            _wordRecords.value[it.id]!!.lastIncorrect
        }.apply {
            val final = mutableListOf<WordInfoItem>()
            final.addAll((this[true] ?: listOf()).sortedByDescending {
                _wordRecords.value[it.id]!!.lastSeen
            })
            final.addAll((this[false] ?: listOf()).sortedByDescending {
                _wordRecords.value[it.id]!!.lastSeen
            })
            continuation.resume(final) {}
        }

    }


    fun onSearch(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            _filteredWords.value = searchWords(_searchQuery.value)
        }
    }

    companion object {
        val factory = viewModelFactory {
            DictionaryScreenViewModel(App.appModule.wordsDb.dao, App.appModule.wordInfoRepository)
        }
    }
}