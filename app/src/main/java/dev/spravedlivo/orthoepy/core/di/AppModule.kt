package dev.spravedlivo.orthoepy.core.di

import android.content.Context
import com.google.gson.Gson
import dev.spravedlivo.orthoepy.feature_words.data.repository.WordInfoRepositoryImpl
import dev.spravedlivo.orthoepy.feature_words.domain.repository.WordInfoRepository

interface AppModule {
    val gson: Gson
    val wordInfoRepository: WordInfoRepository
}

class AppModuleImpl(val applicationContext: Context): AppModule {
    override val gson by lazy {
        Gson()
    }
    override val wordInfoRepository: WordInfoRepository by lazy {
        WordInfoRepositoryImpl(applicationContext, gson)
    }
}