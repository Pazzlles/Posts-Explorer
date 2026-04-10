package com.example.task3.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.task3.ui.detail.CountryDetailContentState
import com.example.task3.ui.detail.CountryDetailEvent
import com.example.task3.ui.detail.CountryDetailRoute
import com.example.task3.ui.detail.CountryDetailScreen
import com.example.task3.ui.detail.CountryDetailUiState
import com.example.task3.ui.list.CountriesRoute

private object Routes {
    const val countries = "countries"
    const val countryDetail = "country/{countryCode}"

    fun countryDetail(countryCode: String): String = "country/$countryCode"
}

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.countries,
        modifier = modifier,
    ) {
        composable(Routes.countries) {
            CountriesRoute(
                onOpenCountry = { countryCode ->
                    navController.navigate(Routes.countryDetail(countryCode))
                },
            )
        }

        composable(
            route = Routes.countryDetail,
            arguments = listOf(
                navArgument("countryCode") {
                    type = NavType.StringType
                },
            ),
        ) { backStackEntry ->
            val countryCode = backStackEntry.arguments?.getString("countryCode")

            if (countryCode.isNullOrBlank()) {
                CountryDetailScreen(
                    state = CountryDetailUiState(
                        countryCode = "",
                        content = CountryDetailContentState.Error(
                            message = "Не удалось открыть экран: отсутствует id поста в маршруте.",
                        ),
                    ),
                    onEvent = { event ->
                        if (event is CountryDetailEvent.BackClicked) {
                            navController.popBackStack()
                        }
                    },
                )
            } else {
                CountryDetailRoute(
                    countryCode = countryCode,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
