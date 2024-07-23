package dev.spravedlivo.orthoepy.core.presentation.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.spravedlivo.orthoepy.core.presentation.main.MainScreen
import dev.spravedlivo.orthoepy.feature_words.presentation.dictionary.DictionaryScreen
import dev.spravedlivo.orthoepy.feature_words.presentation.navigation.DictionaryScreenDestination
import dev.spravedlivo.orthoepy.feature_words.presentation.navigation.SetupScreenDestination
import dev.spravedlivo.orthoepy.feature_words.presentation.navigation.TrainingScreenDestination
import dev.spravedlivo.orthoepy.feature_words.presentation.setup.SetupScreen
import dev.spravedlivo.orthoepy.feature_words.presentation.training.TrainingScreen
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
    NavHost(navController = navController, startDestination = MainScreenDestination) {
        composable<MainScreenDestination> {
            MainScreen(
                onNavigateDictionaryScreen = {
                    navController.navigate(DictionaryScreenDestination)
                },
                onNavigateSetupScreen = {
                    navController.navigate(SetupScreenDestination)
                }
            )
        }
        composable<SetupScreenDestination> {
            SetupScreen(context, onNavigateMainScreen = { amountWords ->
                navController.navigate(
                    TrainingScreenDestination(
                        amountWords
                    )
                )
            })
        }
        composable<TrainingScreenDestination> {
            val args = it.toRoute<TrainingScreenDestination>()
            TrainingScreen(
                amountWords = args.amountWords,
                onNavigateSetupScreen = {
                    navController.navigate(SetupScreenDestination)
                },
                onNavigateMainScreen = {
                    navController.navigate(MainScreenDestination)
                })
        }

        composable<DictionaryScreenDestination> {
            DictionaryScreen {
                navController.navigate(MainScreenDestination)
            }
        }
    }
}
