package dev.spravedlivo.orthoepy.feature_words.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.spravedlivo.orthoepy.feature_words.data.local.entity.WordEntity

@Dao
interface WordsDao {
    @Upsert
    fun upsertWordEntity(wordEntity: WordEntity)

    @Query("SELECT * FROM wordentity WHERE id IN(:ids)")
    fun getWordEntities(ids: List<Int>): List<WordEntity>
}