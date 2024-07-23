package dev.spravedlivo.orthoepy.feature_words.presentation.dictionary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DictionaryScreen(onNavigateMainScreen: () -> Unit) {
    val viewModel = viewModel<DictionaryScreenViewModel>(factory = DictionaryScreenViewModel.factory)
    val loading by viewModel.loading.collectAsState()
    val loaded by viewModel.loaded.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredWords by viewModel.filteredWords.collectAsState()

    Column {
        if (!loading) {
            viewModel.loadWords()
        }
        if (!loaded) {
            Text(text = "Initializing...")
        }
        else {
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
            LazyColumn {

                items(filteredWords, key = { it.id }) {
                    Row {

                        Text(text = it.word)
                    }

                }
            }
        }
    }


}