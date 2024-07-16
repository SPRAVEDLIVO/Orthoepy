package dev.spravedlivo.orthoepy.feature_words.presentation.training

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay


enum class ColorState() {
    DEFAULT, RED, GREEN
}

val VOWELS = listOfNotNull("а", "и", "е", "ё", "о", "у", "ы", "э", "ю", "я").map { it.uppercase() }

@Composable
fun TrainingScreen(amountWords: Int, onNavigateSetupScreen: () -> Unit) {
    val viewModel = viewModel<TrainingScreenViewModel>(factory = TrainingScreenViewModel.factory)

    val loadingWords = viewModel.loadingWords.collectAsState()
    val loadedWords = viewModel.loadedWords.collectAsState()
    val words = viewModel.words.collectAsState()
    val wordIndex = viewModel.wordIndex.collectAsState()
    val correctHits = viewModel.correctHits.collectAsState()
    val mediaPlayer = viewModel.mediaPlayer.collectAsState()

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (!loadingWords.value) {
            viewModel.loadWords(amountWords)
        } else if (!loadedWords.value) {

        } else {
            if (wordIndex.value < words.value.size) {
                var ticks by remember {
                    mutableIntStateOf(0)
                }
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(1000)
                        ticks++
                    }
                }
                Column {
                    val minutes = ticks.div(60).toString().padStart(2, '0')
                    val seconds = ticks.mod(60).toString().padStart(2, '0')
//                    val time = remember(minutes, seconds) {
//                        mutableListOf("$minutes:$seconds")
//                    }
                    Text(text = "$minutes:$seconds")
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    val wordInfo = words.value[wordIndex.value]
                    println(wordInfo.word)
                    viewModel.loadAudio()

                    val defaultColor = ButtonDefaults.filledTonalButtonColors().contentColor


                    val colors = mutableListOf<MutableState<ColorState>>()
                    colors.apply {

                        clear()
                        for (i in 1..wordInfo.word.length) {
                            add(remember {
                                mutableStateOf(ColorState.DEFAULT)
                            })
                        }
                    }

                    val animatedColors = mutableListOf<State<Color>>().apply {
                        for (i in 1..wordInfo.word.length) add(
                            animateColorAsState(
                                targetValue = when (colors[i - 1].value) {
                                    ColorState.DEFAULT -> defaultColor
                                    ColorState.RED -> MaterialTheme.colorScheme.error
                                    ColorState.GREEN -> Color.Green
                                }, label = "ColorAnimation"
                            )
                        )
                    }

                    LazyVerticalGrid(
                        columns = GridCells.FixedSize(50.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()

                    ) {
                        words.value.getOrNull(wordIndex.value)?.also { wordInfo ->


                            wordInfo.word.forEachIndexed { index, chr ->
                                item {
                                    FilledTonalButton(
                                        onClick = {
                                            if (VOWELS.contains(chr.uppercase())) {
                                                val correctIndex =
                                                    wordInfo.sensitive.indexOfFirst { it.isUpperCase() }
                                                if (correctIndex == index) {
                                                    colors[index].value = ColorState.GREEN
                                                    viewModel.incrementCorrectHits()
                                                } else {
                                                    colors[index].value = ColorState.RED
                                                    colors[correctIndex].value =
                                                        ColorState.GREEN
                                                }

                                                viewModel.incrementWordIndex {
                                                    viewModel.play()
                                                    mediaPlayer.value?.setOnCompletionListener {
                                                        viewModel.disposePlayer()
                                                        viewModel.loadAudio()
                                                    }
                                                    colors.forEach {
                                                        it.value = ColorState.DEFAULT
                                                    }
                                                }
                                            }

                                        },
                                        modifier = Modifier.size(50.dp),
                                        contentPadding = PaddingValues(0.dp),
                                        colors = ButtonDefaults.buttonColors().copy(
                                            containerColor = animatedColors.getOrNull(index)?.value
                                                ?: defaultColor
                                        )
                                    ) {
                                        Text(text = chr.uppercase())
                                    }

                                }
                            }
                        }
                    }
                }
                Column {

                }
            } else {
                Column {}
                Column {
                    Text(text = "Training finished!")
                    Text(text = "Got ${correctHits.value} correct out of $amountWords")
                    Button(onClick = onNavigateSetupScreen::invoke) {
                        Text(text = "Main menu")
                    }
                }
                Column {}


            }
        }
    }

    Column {

    }


}