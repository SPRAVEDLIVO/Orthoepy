package dev.spravedlivo.orthoepy.feature_words.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.spravedlivo.orthoepy.feature_words.data.local.entity.WordEntity

@Dao
interface WordsDao {
    @Upsert
    suspend fun upsertWordEntity(wordEntity: WordEntity)

    @Query("SELECT * FROM wordentity WHERE id IN(:ids)")
    suspend fun getWordEntities(ids: List<Int>): List<WordEntity>

    @Query("SELECT * FROM wordentity")
    suspend fun getAllWordEntities(): List<WordEntity>
}