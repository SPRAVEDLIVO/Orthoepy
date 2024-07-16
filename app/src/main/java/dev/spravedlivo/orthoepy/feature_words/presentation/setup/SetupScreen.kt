package dev.spravedlivo.orthoepy.feature_words.presentation.setup

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.roundToInt

val pattern = Regex("^\\d+\$")

@Composable
fun SetupScreen(context: Context, onNavigateMainScreen: (Int) -> Unit) {
    val viewModel = viewModel<SetupScreenViewModel>(factory = SetupScreenViewModel.factory)
    val amountWords = viewModel.amountWords.collectAsState()
    
    Column(verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
        Column {

        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "How many words to train today?")
            Slider(value = amountWords.value.toFloat(), onValueChange = { viewModel.setAmountWords(it.roundToInt()) }, valueRange = 1F..360F, steps = 359)
            Text(text = "Or by input:")
            TextField(value = amountWords.value.toString(), onValueChange = { it: String ->
                if (it.isNotEmpty() && it.matches(pattern)) {
                    val tmp = it.toInt()
                    if (tmp in 1..360) viewModel.setAmountWords(it.toInt())
                }
            }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            Text(text = "Entered amount: ${amountWords.value}")
            Button(onClick = { onNavigateMainScreen(amountWords.value) }) {
                Text(text = "Go")
            }
        }
        Column {
            Text(text = "hi2")
        }
    }
}