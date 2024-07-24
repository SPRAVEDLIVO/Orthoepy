package dev.spravedlivo.orthoepy.feature_words.presentation.training

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.spravedlivo.orthoepy.App
import dev.spravedlivo.orthoepy.core.domain.viewModelFactory
import dev.spravedlivo.orthoepy.feature_words.data.local.WordsDao
import dev.spravedlivo.orthoepy.feature_words.domain.model.WordInfoItem
import dev.spravedlivo.orthoepy.feature_words.domain.model.WordRecord
import dev.spravedlivo.orthoepy.feature_words.domain.repository.WordInfoRepository
import dev.spravedlivo.orthoepy.feature_words.domain.use_case.GetWordRecords
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class TrainingScreenViewModel(
    private val wordInfoRepository: WordInfoRepository,
    private val getWordRecords: GetWordRecords,
    private val wordsDao: WordsDao
) : ViewModel() {

    private val _loadingWords = MutableStateFlow(false)
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

    private val _incorrectWords = MutableStateFlow(mutableMapOf<WordInfoItem, Int>())
    val incorrectWords = _incorrectWords.asStateFlow()

    private val _wordRecords = MutableStateFlow(mapOf<Int, WordRecord>())
    val wordRecords = _wordRecords.asStateFlow()


    fun incrementWordIndex(correct: Boolean, pressIndex: Int, onDelay: () -> Unit) {
        viewModelScope.launch {
            val currentWord = _words.value[_wordIndex.value]
            if (!correct) {
                _incorrectWords.value[currentWord] = pressIndex
            }


            delay(500)
            onDelay()
            _wordIndex.value += 1
            _playedSound.value = false
            _loadingAudio.value = false
            _finished.value = _wordIndex.value >= _words.value.size

            _wordRecords.value[currentWord.id].let {
                it ?: return@let
                it.apply {
                    when (correct) {
                        true -> {
                            it.lastIncorrect = false; it.correctHits += 1
                        }

                        false -> {
                            it.lastIncorrect = true; it.correctHits = 0
                        }
                    }
                }
                wordsDao.upsertWordEntity(it.toWordEntity())
            }
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
            _wordRecords.value = getWordRecords(final)
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
            TrainingScreenViewModel(
                App.appModule.wordInfoRepository,
                App.appModule.getWordRecords,
                App.appModule.wordsDb.dao
            )
        }
    }
}