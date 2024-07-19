package dev.spravedlivo.orthoepy.feature_words.domain.repository

import dev.spravedlivo.orthoepy.feature_words.domain.model.WordInfoItem
import dev.spravedlivo.orthoepy.feature_words.domain.model.WordRecord
import java.io.File

interface WordInfoRepository {
    fun getWordInfo(): Array<WordInfoItem>
    suspend fun downloadWordAudio(wordInfo: WordInfoItem): File?

    suspend fun getWordRecords(ids: List<Int>): List<WordRecord>
    suspend fun upsertWordRecord(wordRecord: WordRecord)
}