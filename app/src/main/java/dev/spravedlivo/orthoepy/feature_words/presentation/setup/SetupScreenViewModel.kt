package dev.spravedlivo.orthoepy.feature_words.presentation.setup

import androidx.lifecycle.ViewModel
import dev.spravedlivo.orthoepy.App
import dev.spravedlivo.orthoepy.core.domain.viewModelFactory
import dev.spravedlivo.orthoepy.feature_words.domain.repository.WordInfoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SetupScreenViewModel(wordInfoRepository: WordInfoRepository) : ViewModel() {
    private val _amountWords = MutableStateFlow<Int>(10)
    val amountWords = _amountWords.asStateFlow()
    val totalWords = wordInfoRepository.getWordInfo().size

    fun setAmountWords(it: Int) {
        _amountWords.value = it
    }

    companion object {
        val factory = viewModelFactory {
            SetupScreenViewModel(App.appModule.wordInfoRepository)
        }
    }
}