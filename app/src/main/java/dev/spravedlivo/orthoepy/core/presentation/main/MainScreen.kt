package dev.spravedlivo.orthoepy.core.presentation.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.spravedlivo.orthoepy.R

@Composable
fun MainScreen(
    onNavigateSetupScreen: () -> Unit,
    onNavigateDictionaryScreen: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
            FilledTonalButton(onClick = { onNavigateSetupScreen() }) {
                Text(text = "Train")
            }
            FilledTonalButton(onClick = { onNavigateDictionaryScreen() }) {
                Text(text = "Dictionary")
            }
        }

    }
}