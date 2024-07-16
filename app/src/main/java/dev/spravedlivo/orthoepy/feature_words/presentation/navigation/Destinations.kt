package dev.spravedlivo.orthoepy.feature_words.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object SetupScreen

@Serializable
data class TrainingScreen(val amountWords: Int)