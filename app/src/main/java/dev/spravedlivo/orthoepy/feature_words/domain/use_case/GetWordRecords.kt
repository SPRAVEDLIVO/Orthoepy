package dev.spravedlivo.orthoepy.feature_words.domain.use_case

import dev.spravedlivo.orthoepy.feature_words.data.local.WordsDao
import dev.spravedlivo.orthoepy.feature_words.data.local.entity.WordEntity
import dev.spravedlivo.orthoepy.feature_words.domain.model.WordInfoItem
import dev.spravedlivo.orthoepy.feature_words.domain.model.WordRecord
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime

fun getNow(): kotlinx.datetime.LocalDateTime {
    return LocalDateTime.now().toKotlinLocalDateTime()
}

fun defaultWordEntity(id: Int, now: kotlinx.datetime.LocalDateTime? = null): WordEntity {
    return WordEntity(id, 0, false, now ?: getNow())
}

class GetWordRecords(
    private val wordsDao: WordsDao
) {
    suspend operator fun invoke(words: List<WordInfoItem>): Map<Int, WordRecord> {
        val records = wordsDao.getWordEntities(words.map { it.id })
        val now = getNow()
        val transform = words.map {
            records.find { out -> it.id == out.id } ?: defaultWordEntity(it.id, now)
        }

        transform.forEach {
            wordsDao.upsertWordEntity(it)
        }

        return wordsDao.getWordEntities(words.map { it.id }).associate {
            it.id to it.toWordRecord()
        }
    }
}