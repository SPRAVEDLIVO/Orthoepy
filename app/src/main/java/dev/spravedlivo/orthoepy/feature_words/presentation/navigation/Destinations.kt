package dev.spravedlivo.orthoepy.feature_words.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object SetupScreenDestination

@Serializable
data class TrainingScreenDestination(val amountWords: Int)

@Serializable
object DictionaryScreenDestination