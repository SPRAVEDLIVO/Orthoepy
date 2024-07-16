package dev.spravedlivo.orthoepy.feature_words.domain.repository

import dev.spravedlivo.orthoepy.feature_words.domain.model.WordInfo
import dev.spravedlivo.orthoepy.feature_words.domain.model.WordInfoItem

interface WordInfoRepository {
    fun getWordInfo(): Array<WordInfoItem>
}