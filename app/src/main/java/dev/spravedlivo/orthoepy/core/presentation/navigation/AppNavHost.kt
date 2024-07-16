package dev.spravedlivo.orthoepy.core.presentation.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.spravedlivo.orthoepy.feature_words.presentation.navigation.SetupScreen
import dev.spravedlivo.orthoepy.feature_words.presentation.navigation.TrainingScreen
import dev.spravedlivo.orthoepy.feature_words.presentation.setup.SetupScreen
import kotlin.reflect.KClass

fun NavHostController.navigateSingleTopTo(
    route: KClass<Any>,
    stateSaving: Boolean = true,
    stateRestoring: Boolean = true
) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = stateSaving
        }
        launchSingleTop = true
        restoreState = stateRestoring
    }


@Composable
fun AppNavHost(context: Context) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = SetupScreen) {
        composable<SetupScreen> {
            SetupScreen(context, onNavigateMainScreen = { amountWords ->
                navController.navigate(
                    dev.spravedlivo.orthoepy.feature_words.presentation.navigation.TrainingScreen(
                        amountWords
                    )
                )
            })
        }
        composable<TrainingScreen> {
            val args = it.toRoute<TrainingScreen>()
            dev.spravedlivo.orthoepy.feature_words.presentation.training.TrainingScreen(
                amountWords = args.amountWords,
                onNavigateSetupScreen = {
                    navController.navigate(SetupScreen)
                })
        }
    }
}
