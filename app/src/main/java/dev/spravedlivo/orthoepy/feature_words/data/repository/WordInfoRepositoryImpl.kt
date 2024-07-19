package dev.spravedlivo.orthoepy.feature_words.data.repository

import android.content.Context
import android.net.http.HttpException
import com.google.gson.Gson
import dev.spravedlivo.orthoepy.R
import dev.spravedlivo.orthoepy.feature_words.data.local.WordsDao
import dev.spravedlivo.orthoepy.feature_words.data.remote.WordsApi
import dev.spravedlivo.orthoepy.feature_words.domain.model.WordInfo
import dev.spravedlivo.orthoepy.feature_words.domain.model.WordInfoItem
import dev.spravedlivo.orthoepy.feature_words.domain.model.WordRecord
import dev.spravedlivo.orthoepy.feature_words.domain.repository.WordInfoRepository
import java.io.File


class WordInfoRepositoryImpl(private val context: Context,
                             private val gson: Gson,
                             private val wordsApi: WordsApi,
                             private val wordsDao: WordsDao
) : WordInfoRepository {
    private val _wordInfo: Array<WordInfoItem>? = null
    override fun getWordInfo(): Array<WordInfoItem> {
        if (_wordInfo != null) return _wordInfo
        val text = context.resources.openRawResource(R.raw.words)
            .bufferedReader().use { it.readText() }
        return gson.fromJson(text, Array<WordInfoItem>::class.java)
    }

    override suspend fun downloadWordAudio(wordInfo: WordInfoItem): File? {
        val file = File(context.filesDir, "${wordInfo.id}.mp3")

        if (!file.exists()) {
            try {
                val arr = wordsApi.getAudioFromUrl(wordInfo.audio_url)
                file.writeBytes(arr)
            }
            catch (e: HttpException) {
                return null
            }

        }

        return file
    }

    override suspend fun getWordRecords(ids: List<Int>): List<WordRecord> = wordsDao.getWordEntities(ids).map { it.toWordRecord() }
    override suspend fun upsertWordRecord(wordRecord: WordRecord) = wordsDao.upsertWordEntity(wordRecord.toWordEntity())


}