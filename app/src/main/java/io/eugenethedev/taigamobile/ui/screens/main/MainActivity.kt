package io.eugenethedev.taigamobile.ui.screens.main

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.*
import androidx.navigation.compose.*
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.screens.login.LoginScreen
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scaffoldState = rememberScaffoldState()
            val navController = rememberNavController()
            val screensWithoutBottomBar = listOf(Routes.login)

            TaigaMobileTheme {
                Scaffold(
                    scaffoldState = scaffoldState,
                    snackbarHost = {
                        SnackbarHost(it) {
                            Snackbar(
                                snackbarData = it,
                                backgroundColor = MaterialTheme.colors.surface,
                                contentColor = contentColorFor(MaterialTheme.colors.surface)
                            )
                        }
                    },
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)

                        if (currentRoute == null || currentRoute in screensWithoutBottomBar) return@Scaffold

                        BottomNavigation(backgroundColor = MaterialTheme.colors.surface) {
                            listOf(Screen.Stories, Screen.Team).forEach { screen ->
                                BottomNavigationItem(
                                    selectedContentColor = MaterialTheme.colors.primary,
                                    unselectedContentColor = Color.Gray,
                                    icon = { Icon(imageVector = vectorResource(screen.iconId), contentDescription = "") },
                                    label = { Text(stringResource(screen.resourceId)) },
                                    selected = currentRoute == screen.route,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            // Pop up to the start destination of the graph to
                                            // avoid building up a large stack of destinations
                                            // on the back stack as users select items
                                            popUpTo = navController.graph.startDestination
                                            // Avoid multiple copies of the same destination when
                                            // reselecting the same item
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                        }
                    },
                    bodyContent = { MainScreen(scaffoldState, it, navController) }
                )
            }
        }
    }
}

sealed class Screen(val route: String, @StringRes val resourceId: Int, @DrawableRes val iconId: Int) {
    object Stories : Screen(Routes.stories, R.string.stories, R.drawable.ic_stories)
    object Team : Screen(Routes.team, R.string.team, R.drawable.ic_team)
}

object Routes {
    const val login = "login"
    const val stories = "stories"
    const val team = "team"
}

@ExperimentalMaterialApi
@Composable
fun MainScreen(
    scaffoldState: ScaffoldState,
    paddingValues: PaddingValues,
    navController: NavHostController
) {
    val coroutineScope = rememberCoroutineScope()

    val onError: @Composable (Int) -> Unit = { message ->
        val strMessage = stringResource(message)
        coroutineScope.launch { scaffoldState.snackbarHostState.showSnackbar(strMessage) }
    }

    Box(Modifier.fillMaxSize().padding(paddingValues)) {
        NavHost(
            navController = navController,
            startDestination = Routes.login
        ) {
            composable(Routes.login) {
                LoginScreen(
                    navController = navController,
                    onError = onError
                )
            }

            composable(Routes.stories) {

            }

            composable(Routes.team) {

            }
        }

    }
}