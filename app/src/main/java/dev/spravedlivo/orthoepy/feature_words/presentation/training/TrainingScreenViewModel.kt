package dev.spravedlivo.orthoepy.feature_words.presentation.training

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.spravedlivo.orthoepy.App
import dev.spravedlivo.orthoepy.core.domain.viewModelFactory
import dev.spravedlivo.orthoepy.feature_words.data.remote.WordsApi
import dev.spravedlivo.orthoepy.feature_words.domain.model.WordInfoItem
import dev.spravedlivo.orthoepy.feature_words.domain.repository.WordInfoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class TrainingScreenViewModel(
    private val wordInfoRepository: WordInfoRepository
) : ViewModel() {

    private val _loadingWords = MutableStateFlow<Boolean>(false)
    val loadingWords = _loadingWords.asStateFlow()

    private val _loadedWords = MutableStateFlow(false)
    val loadedWords = _loadedWords.asStateFlow()

    private val _words = MutableStateFlow<List<WordInfoItem>>(listOf())
    val words = _words.asStateFlow()

    private val _wordIndex = MutableStateFlow(0)
    val wordIndex = _wordIndex.asStateFlow()

    private val _correctHits = MutableStateFlow(0)
    val correctHits = _correctHits.asStateFlow()

    private val _mediaPlayer = MutableStateFlow<MediaPlayer?>(null)
    val mediaPlayer = _mediaPlayer.asStateFlow()

    private val _playedSound = MutableStateFlow(false)
    private val _loadingAudio = MutableStateFlow(false)

    private val _finished = MutableStateFlow(false)
    val finished = _finished.asStateFlow()

    private val _wordIdToAudio = MutableStateFlow(mutableMapOf<Int, File>())


    fun incrementWordIndex(onDelay: () -> Unit) {
        viewModelScope.launch {

            delay(500)
            onDelay()
            _wordIndex.value += 1
            _playedSound.value = false
            _loadingAudio.value = false
            _finished.value = _wordIndex.value >= _words.value.size
        }
    }

    fun incrementCorrectHits() {
        _correctHits.value++
    }

    override fun onCleared() {
        super.onCleared()
        disposePlayer()
    }

    fun disposePlayer() {
        if (_mediaPlayer.value != null) {
            _mediaPlayer.value!!.reset()
            _mediaPlayer.value!!.release()
        }
        _mediaPlayer.value = null
    }

    fun loadAudio() {
        if (_mediaPlayer.value != null || _loadingAudio.value) return
        _loadingAudio.value = true
        disposePlayer()
        val currentWord = _words.value.getOrNull(wordIndex.value) ?: return
        val audioFile = _wordIdToAudio.value.getOrDefault(currentWord.id, null) ?: return

        val inputStream = audioFile.inputStream()


        MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setOnPreparedListener {
                inputStream.close()
                _mediaPlayer.value = it
                _loadingAudio.value = false
            }
            setOnErrorListener { _, _, _ -> inputStream.close(); true }
            setDataSource(inputStream.fd)
            prepareAsync() // might take long! (for buffering, etc)
        }

    }

    fun play() {
        if (_playedSound.value || mediaPlayer.value == null) return
        _mediaPlayer.value!!.start()
        _playedSound.value = true
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
        viewModelScope.launch {
            val it = final[0]
            val downloaded = wordInfoRepository.downloadWordAudio(it)
            if (downloaded != null) _wordIdToAudio.value[it.id] = downloaded
            _loadedWords.value = true
        }


        viewModelScope.launch {
            final.subList(fromIndex = 1, toIndex = final.size).forEach {
                val downloaded = wordInfoRepository.downloadWordAudio(it) ?: return@forEach
                _wordIdToAudio.value[it.id] = downloaded
            }
        }

    }

    companion object {
        val factory = viewModelFactory {
            TrainingScreenViewModel(App.appModule.wordInfoRepository)
        }
    }
}