package dev.spravedlivo.orthoepy.feature_words.domain.model

import dev.spravedlivo.orthoepy.feature_words.data.local.entity.WordEntity
import kotlinx.datetime.LocalDateTime

data class WordRecord(
    val id: Int,
    var correctHits: Int,
    var lastIncorrect: Boolean,
    var lastSeen: LocalDateTime
) {
    fun toWordEntity(): WordEntity {
        return WordEntity(id, correctHits, lastIncorrect, lastSeen)
    }
}