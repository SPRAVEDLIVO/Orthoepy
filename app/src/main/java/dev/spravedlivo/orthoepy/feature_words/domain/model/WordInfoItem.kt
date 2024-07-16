package dev.spravedlivo.orthoepy.feature_words.domain.model

data class WordInfoItem(
    val audio_url: String,
    val examable: Boolean,
    val id: Int,
    val incorrect: String,
    val pronounceable: Boolean,
    val sensitive: String,
    val word: String
)