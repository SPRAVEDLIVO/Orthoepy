package dev.spravedlivo.orthoepy.feature_words.presentation.dictionary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.spravedlivo.orthoepy.feature_words.domain.model.WordRecord
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char

fun resolveWordColor(wordRecord: WordRecord): Color {
    return if (wordRecord.lastIncorrect) Color.Red
    else Color(
        (255 - 2.55F * wordRecord.correctHits * 20) / 255F,
        2.55F * wordRecord.correctHits * 20,
        0F,
        255F
    )
}

val format = LocalDateTime.Format {
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    char(' ')
    dayOfMonth()
    char(' ')
    hour()
    char(':')
    minute()
}

@Composable
fun DictionaryScreen(onNavigateMainScreen: () -> Unit) {
    val viewModel =
        viewModel<DictionaryScreenViewModel>(factory = DictionaryScreenViewModel.factory)
    val loading by viewModel.loading.collectAsState()
    val loaded by viewModel.loaded.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredWords by viewModel.filteredWords.collectAsState()
    val wordRecords by viewModel.wordRecords.collectAsState()

    Column {
        if (!loading) {
            viewModel.loadWords()
        }
        if (!loaded) {
            Text(text = "Initializing...")
        } else {
            TextField(value = searchQuery,
                onValueChange = viewModel::onSearch,
                singleLine = true,
                placeholder = {
                    Text(text = "Search...")
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            )
            val dpToPx = with(LocalDensity.current) { 1.sp.toPx() }
            val textSize = MaterialTheme.typography.bodyMedium.fontSize.value
            LazyColumn {

                items(filteredWords, key = { it.id }) {
                    Row {
                        val wordRecord = wordRecords[it.id]!!
                        Spacer(
                            modifier = Modifier
                                .drawWithContent {

                                    translate(
                                        (textSize.dp * dpToPx / 2).value,
                                        (textSize.dp * dpToPx * 9 / 10).value
                                    ) {
                                        drawCircle(
                                            resolveWordColor(wordRecord),
                                            15f
                                        )
                                    }
                                }
                        )
                        Row(modifier = Modifier.padding(13.dp, 0.dp)) {
                            val correctIndex =
                                it.sensitive.indexOfFirst { it.isUpperCase() }
                            it.word.forEachIndexed { index, c ->
                                Text(
                                    text = c.toString(),
                                    color = if (index == correctIndex) Color.Green else Color.Unspecified
                                )
                            }
                            Text(
                                text = "${wordRecord.lastSeen.format(format)} ${if (wordRecord.lastIncorrect) ", incorrect" else ", correct"}",
                                modifier = Modifier.padding(horizontal = 10.dp),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                    }

                }
            }
        }
    }


}