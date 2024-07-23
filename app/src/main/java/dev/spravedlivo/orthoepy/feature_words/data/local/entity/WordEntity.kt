package dev.spravedlivo.orthoepy.feature_words.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.spravedlivo.orthoepy.feature_words.domain.model.WordRecord
import kotlinx.datetime.LocalDateTime

@Entity
data class WordEntity(@PrimaryKey val id: Int,
                      val correctHits: Int,
                      val lastIncorrect: Boolean,
                      val lastSeen: LocalDateTime
) {
    fun toWordRecord(): WordRecord {
        return WordRecord(id, correctHits, lastIncorrect, lastSeen)
    }
}