package dev.spravedlivo.orthoepy.feature_words.presentation.training

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay


enum class ColorState {
    DEFAULT, RED, GREEN
}

val VOWELS = listOfNotNull("а", "и", "е", "ё", "о", "у", "ы", "э", "ю", "я").map { it.uppercase() }

@Composable
fun resolveColor(colorState: ColorState, defaultColor: Color): Color {
    return when (colorState) {
        ColorState.DEFAULT -> defaultColor
        ColorState.RED -> MaterialTheme.colorScheme.error
        ColorState.GREEN -> Color.Green
    }
}

@Composable
fun TrainingScreen(
    amountWords: Int,
    onNavigateMainScreen: () -> Unit,
    onNavigateSetupScreen: () -> Unit
) {
    val viewModel = viewModel<TrainingScreenViewModel>(factory = TrainingScreenViewModel.factory)

    val loadingWords = viewModel.loadingWords.collectAsState()
    val loadedWords = viewModel.loadedWords.collectAsState()
    val words = viewModel.words.collectAsState()
    val wordIndex = viewModel.wordIndex.collectAsState()
    val correctHits = viewModel.correctHits.collectAsState()
    val mediaPlayer = viewModel.mediaPlayer.collectAsState()
    val finished by viewModel.finished.collectAsState()
    val incorrectWords by viewModel.incorrectWords.collectAsState()
    val wordRecords by viewModel.wordRecords.collectAsState()

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (!loadingWords.value) {
            viewModel.loadWords(amountWords)
        } else if (!loadedWords.value) {
            Text(text = "Initializing...")
        } else {
            var ticks by remember {
                mutableIntStateOf(0)
            }
            LaunchedEffect(finished) {
                while (!finished) {
                    delay(1000)
                    ticks++
                }
            }
            val minutes = ticks.div(60).toString().padStart(2, '0')
            val seconds = ticks.mod(60).toString().padStart(2, '0')

            val time by remember(minutes, seconds) {
                mutableStateOf("$minutes:$seconds")
            }

            if (!finished) {
                val defaultColor = ButtonDefaults.filledTonalButtonColors().containerColor
                val wordInfo = words.value[wordIndex.value]
                val wordRecord = wordRecords[wordInfo.id]!!
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = time)
                        Text(text = "${wordIndex.value} / ${words.value.size}")
                    }
                    LinearProgressIndicator(
                        progress = { wordIndex.value / words.value.size.toFloat() },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (wordRecord.lastIncorrect) {
                            Text(
                                text = "This is a difficult word for you", color = resolveColor(
                                    colorState = ColorState.RED,
                                    defaultColor = defaultColor
                                )
                            )
                        } else if (wordRecord.correctHits >= 5) {
                            Text(
                                text = "This is a learned word", color = resolveColor(
                                    colorState = ColorState.GREEN,
                                    defaultColor = defaultColor
                                )
                            )
                        }
                        if (wordInfo.examable) {
                            FilledTonalButton(onClick = {}) {
                                Text(text = "FIPI")
                            }
                        }
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    viewModel.loadAudio()


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
                                targetValue = resolveColor(
                                    colorState = colors[i - 1].value,
                                    defaultColor
                                ), label = "ColorAnimation"
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
                                                val correct = correctIndex == index
                                                if (correct) {
                                                    colors[index].value = ColorState.GREEN
                                                    viewModel.incrementCorrectHits()
                                                } else {
                                                    colors[index].value = ColorState.RED
                                                    colors[correctIndex].value =
                                                        ColorState.GREEN
                                                }

                                                viewModel.incrementWordIndex(correct, index) {
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
                                        colors = ButtonDefaults.filledTonalButtonColors().copy(
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Training finished!")
                    Text(text = "Got ${correctHits.value} correct out of $amountWords")
                    Text(text = "Time: $time")
                    FilledTonalButton(onClick = onNavigateSetupScreen::invoke) {
                        Text(text = "Another training")
                    }
                    Button(onClick = onNavigateMainScreen::invoke) {
                        Text(text = "Main menu")
                    }
                    if (incorrectWords.isNotEmpty()) {
                        Column {
                            HorizontalDivider()
                            Text(text = "Mistakes:")
                            LazyColumn {
                                incorrectWords.forEach { (key, value) ->
                                    item(key = key.id) {
                                        Row {
                                            val defaultColor = MaterialTheme.colorScheme.onSurface
                                            val correctIndex =
                                                key.sensitive.indexOfFirst { it.isUpperCase() }
                                            key.word.forEachIndexed { index, char ->
                                                Text(
                                                    text = char.toString(),
                                                    color = when (index == value) {
                                                        true -> resolveColor(
                                                            colorState = ColorState.RED,
                                                            defaultColor = defaultColor
                                                        )

                                                        false -> if (index == correctIndex) resolveColor(
                                                            colorState = ColorState.GREEN,
                                                            defaultColor = defaultColor
                                                        ) else defaultColor
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                            }
                        }

                    }


                }
                Column {}


            }
        }
    }

    Column {

    }


}