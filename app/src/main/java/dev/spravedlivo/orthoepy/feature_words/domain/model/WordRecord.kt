package dev.spravedlivo.orthoepy.feature_words.domain.model

import dev.spravedlivo.orthoepy.feature_words.data.local.entity.WordEntity
import kotlinx.datetime.LocalDateTime

data class WordRecord(
    val id: Int,
    val correctHits: Int,
    val lastSeen: LocalDateTime
) {
    fun toWordEntity(): WordEntity {
        return WordEntity(id, correctHits, lastSeen)
    }
}