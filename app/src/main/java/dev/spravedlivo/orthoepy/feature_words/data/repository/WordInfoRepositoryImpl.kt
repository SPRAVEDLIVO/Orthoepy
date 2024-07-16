package dev.spravedlivo.orthoepy.feature_words.data.repository

import android.content.Context
import com.google.gson.Gson
import dev.spravedlivo.orthoepy.R
import dev.spravedlivo.orthoepy.feature_words.domain.model.WordInfoItem
import dev.spravedlivo.orthoepy.feature_words.domain.repository.WordInfoRepository


class WordInfoRepositoryImpl(val context: Context, val gson: Gson) : WordInfoRepository {
    private val _wordInfo: Array<WordInfoItem>? = null
    override fun getWordInfo(): Array<WordInfoItem> {
        if (_wordInfo != null) return _wordInfo
        val text = context.resources.openRawResource(R.raw.words)
            .bufferedReader().use { it.readText() }
        return gson.fromJson(text, Array<WordInfoItem>::class.java)
    }
}