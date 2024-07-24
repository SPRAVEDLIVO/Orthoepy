package dev.spravedlivo.orthoepy.feature_words.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

interface WordsApi {
    suspend fun getAudioFromUrl(url: String): ByteArray
}

class WordsApiImpl(
    private val client: HttpClient
) : WordsApi {
    override suspend fun getAudioFromUrl(url: String): ByteArray {
        return client.get(url).body()
    }

}