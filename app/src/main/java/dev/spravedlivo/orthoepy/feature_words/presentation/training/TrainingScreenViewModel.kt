package dev.spravedlivo.orthoepy.feature_words.presentation.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.spravedlivo.orthoepy.App
import dev.spravedlivo.orthoepy.core.domain.viewModelFactory
import dev.spravedlivo.orthoepy.feature_words.domain.model.WordInfoItem
import dev.spravedlivo.orthoepy.feature_words.domain.repository.WordInfoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrainingScreenViewModel(
   val wordInfoRepository: WordInfoRepository
) : ViewModel() {

    private val _loadingWords = MutableStateFlow<Boolean>(false)
    val loadingWords = _loadingWords.asStateFlow()

    private val _loadedWords = MutableStateFlow(false)
    val loadedWords = _loadedWords.asStateFlow()

    private val _words = MutableStateFlow<List<WordInfoItem>>( listOf() )
    val words = _words.asStateFlow()

    private val _wordIndex = MutableStateFlow( 0 )
    val wordIndex = _wordIndex.asStateFlow()

    private val _correctHits = MutableStateFlow(0)
    val correctHits = _correctHits.asStateFlow()


    fun incrementWordIndex(onDelay: () -> Unit) {
        viewModelScope.launch {

            delay(500)
            onDelay()
            _wordIndex.value += 1
        }
    }

    fun incrementCorrectHits() {
        _correctHits.value++
    }

    fun loadWords(amount: Int) {
        _loadingWords.value = true
        val tmpWords = wordInfoRepository.getWordInfo()
        tmpWords.shuffle()
        val final = mutableListOf<WordInfoItem>()
        for (i in 1..amount) {
            final.add(tmpWords[i])
        }
        _words.value = final
        _loadedWords.value = true

    }

    companion object {
        val factory = viewModelFactory {
            TrainingScreenViewModel(App.appModule.wordInfoRepository)
        }
    }
}