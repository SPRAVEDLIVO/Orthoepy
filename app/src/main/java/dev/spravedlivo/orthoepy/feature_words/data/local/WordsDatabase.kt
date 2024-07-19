package dev.spravedlivo.orthoepy.feature_words.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.spravedlivo.orthoepy.feature_words.data.local.entity.WordEntity

@Database(
    entities = [WordEntity::class],
    version = 1,
    autoMigrations = [],
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class WordsDatabase : RoomDatabase() {
    abstract val dao: WordsDao
}