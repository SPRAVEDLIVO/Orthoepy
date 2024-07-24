package dev.spravedlivo.orthoepy.core.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import dev.spravedlivo.orthoepy.feature_words.data.local.WordsDatabase
import dev.spravedlivo.orthoepy.feature_words.data.remote.WordsApi
import dev.spravedlivo.orthoepy.feature_words.data.remote.WordsApiImpl
import dev.spravedlivo.orthoepy.feature_words.data.repository.WordInfoRepositoryImpl
import dev.spravedlivo.orthoepy.feature_words.domain.repository.WordInfoRepository
import dev.spravedlivo.orthoepy.feature_words.domain.use_case.GetWordRecords
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android

interface AppModule {
    val gson: Gson
    val wordInfoRepository: WordInfoRepository
    val ktor: HttpClient
    val wordsApi: WordsApi
    val wordsDb: WordsDatabase
    val getWordRecords: GetWordRecords
}

class AppModuleImpl(val applicationContext: Context) : AppModule {
    override val gson by lazy {
        Gson()
    }

    override val wordsApi: WordsApi by lazy {
        WordsApiImpl(ktor)
    }

    override val wordInfoRepository: WordInfoRepository by lazy {
        WordInfoRepositoryImpl(applicationContext, gson, wordsApi, wordsDb.dao)
    }


    override val ktor: HttpClient by lazy {
        HttpClient(Android) {

        }
    }

    override val wordsDb: WordsDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            WordsDatabase::class.java, "words_database.db"
        ).build()
    }

    override val getWordRecords: GetWordRecords by lazy {
        GetWordRecords(wordsDb.dao)
    }


}