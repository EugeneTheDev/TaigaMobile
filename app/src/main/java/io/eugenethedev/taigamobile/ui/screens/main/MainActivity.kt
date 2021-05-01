package io.eugenethedev.taigamobile.ui.screens.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.google.accompanist.insets.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.ui.components.ContainerBox
import io.eugenethedev.taigamobile.ui.screens.login.LoginScreen
import io.eugenethedev.taigamobile.ui.screens.projectselector.ProjectSelectorScreen
import io.eugenethedev.taigamobile.ui.screens.scrum.ScrumScreen
import io.eugenethedev.taigamobile.ui.screens.sprint.SprintScreen
import io.eugenethedev.taigamobile.ui.screens.commontask.CommonTaskScreen
import io.eugenethedev.taigamobile.ui.screens.createtask.CreateTaskScreen
import io.eugenethedev.taigamobile.ui.screens.epics.EpicsScreen
import io.eugenethedev.taigamobile.ui.screens.issues.IssuesScreen
import io.eugenethedev.taigamobile.ui.screens.settings.SettingsScreen
import io.eugenethedev.taigamobile.ui.screens.team.TeamScreen
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    @ExperimentalPagerApi
    @ExperimentalComposeUiApi
    @ExperimentalAnimatedInsets
    @ExperimentalAnimationApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val scaffoldState = rememberScaffoldState()
            val navController = rememberNavController()
            val systemUiController = rememberSystemUiController()

            TaigaMobileTheme {
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    systemUiController.let {
                        it.setStatusBarColor(Color.Transparent, darkIcons = !isSystemInDarkTheme())
                        it.setNavigationBarColor(Color.Transparent, darkIcons = !isSystemInDarkTheme())
                    }
                    Timber.w(MaterialTheme.colors.primarySurface.toString())

                    Box(
                        Modifier
                            .statusBarsPadding()) {
                        Scaffold(
                            scaffoldState = scaffoldState,
                            snackbarHost = {
                                SnackbarHost(it) {
                                    Snackbar(
                                        snackbarData = it,
                                        backgroundColor = MaterialTheme.colors.surface,
                                        contentColor = contentColorFor(MaterialTheme.colors.surface),
                                        shape = MaterialTheme.shapes.medium
                                    )
                                }
                            },
                            bottomBar = {
                                val items = Screens.values()
                                val routes = items.map { it.route }
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)

                                // hide bottom bar for other screens
                                if (currentRoute !in routes) return@Scaffold

                                Column {
                                    BottomNavigation(
                                        backgroundColor = MaterialTheme.colors.surface,
                                        modifier = Modifier.height(48.dp)
                                    ) {
                                        items.forEach { screen ->
                                            BottomNavigationItem(
                                                modifier = Modifier.offset(y = 4.dp),
                                                selectedContentColor = MaterialTheme.colors.primary,
                                                unselectedContentColor = Color.Gray,
                                                icon = {
                                                    Icon(
                                                        painter = painterResource(screen.iconId),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                },
                                                label = { Text(stringResource(screen.resourceId)) },
                                                selected = currentRoute == screen.route,
                                                onClick = {
                                                    if (screen.route != currentRoute) {
                                                        navController.navigate(screen.route) {
                                                            currentRoute?.let {
                                                                popUpTo(it) {
                                                                    inclusive = true
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            )
                                        }
                                    }

                                    Spacer(
                                        Modifier
                                            .fillMaxWidth()
                                            .background(
                                                LocalElevationOverlay.current?.apply(
                                                    color = MaterialTheme.colors.surface,
                                                    elevation = BottomNavigationDefaults.Elevation
                                                ) ?: MaterialTheme.colors.surface
                                            )
                                            .navigationBarsHeight()
                                    )
                                }
                            },
                            content = {
                                MainScreen(scaffoldState, it, navController)
                            }
                        )
                    }
                }
            }
        }
    }
}

enum class Screens(val route: String, @StringRes val resourceId: Int, @DrawableRes val iconId: Int) {
    Scrum(Routes.scrum, R.string.scrum, R.drawable.ic_scrum),
    Epics(Routes.epics, R.string.epics, R.drawable.ic_epics),
    Issues(Routes.issues, R.string.issues, R.drawable.ic_issues),
    More(Routes.more, R.string.more, R.drawable.ic_more)
}

object Routes {
    const val login = "login"
    const val scrum = "scrum"
    const val epics = "epics"
    const val issues = "issues"
    const val more = "more"
    const val team = "team"
    const val settings = "settings"
    const val projectsSelector = "projectsSelector"
    const val sprint = "sprint"
    const val commonTask = "commonTask"
    const val createTask = "createTask"

    object Arguments {
        const val sprint = "sprint"
        const val sprintId = "sprintId"
        const val commonTaskId = "taskId"
        const val commonTaskType = "taskType"
        const val ref = "ref"
        const val projectSlug = "projectSlug"
        const val parentId = "parentId"
    }
}

@ExperimentalPagerApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun MainScreen(
    scaffoldState: ScaffoldState,
    paddingValues: PaddingValues,
    navController: NavHostController
) {
    val viewModel: MainViewModel = viewModel()

    val onError: @Composable (Int) -> Unit = { message ->
        val strMessage = stringResource(message)
        LaunchedEffect(null) {
            scaffoldState.snackbarHostState.showSnackbar(strMessage)
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
        NavHost(
            navController = navController,
            startDestination = if (viewModel.isLogged) Routes.scrum else Routes.login
        ) {
            composable(Routes.login) {
                LoginScreen(
                    navController = navController,
                    onError = onError
                )
            }

            // start screen
            composable(Routes.scrum) {
                ScrumScreen(
                    navController = navController,
                    onError = onError
                )

                // user must select project first
                LaunchedEffect(null) {
                    if (!viewModel.isProjectSelected) {
                        navController.navigate(Routes.projectsSelector)
                    }
                }
            }

            composable(Routes.epics) {
                EpicsScreen(
                    navController = navController,
                    onError = onError
                )
            }

            composable(Routes.issues) {
                IssuesScreen(
                    navController = navController,
                    onError = onError
                )
            }

            composable(Routes.more) {
                MoreScreen(
                    navController = navController
                )
            }

            composable(Routes.team) {
                TeamScreen(
                    navController = navController,
                    onError = onError
                )
            }

            composable(Routes.settings) {
                SettingsScreen(
                    navController = navController,
                    onError = onError
                )
            }

            composable(Routes.projectsSelector) {
                ProjectSelectorScreen(
                    navController = navController,
                    onError = onError
                )
            }

            composable(Routes.sprint) {
                // this is workaround 'cause navigation component for compose cannot pass Parcelable arguments
                SprintScreen(
                    navController = navController,
                    sprint = navController.previousBackStackEntry?.arguments?.getParcelable(Routes.Arguments.sprint)!!,
                    onError = onError
                )
            }

            composable(
                Routes.Arguments.run { "${Routes.commonTask}/{$commonTaskId}/{$commonTaskType}/{$ref}" },
                arguments = listOf(
                    navArgument(Routes.Arguments.commonTaskType) { type = NavType.StringType },
                    navArgument(Routes.Arguments.commonTaskId) { type = NavType.LongType },
                    navArgument(Routes.Arguments.ref) { type = NavType.IntType },
                )
            ) {
                CommonTaskScreen(
                    navController = navController,
                    commonTaskId = it.arguments!!.getLong(Routes.Arguments.commonTaskId),
                    commonTaskType = CommonTaskType.valueOf(it.arguments!!.getString(Routes.Arguments.commonTaskType, "")),
                    ref = it.arguments!!.getInt(Routes.Arguments.ref),
                    onError = onError
                )
            }

            composable(
                Routes.Arguments.run {"${Routes.createTask}/{$commonTaskType}?$parentId={$parentId}&$sprintId={$sprintId}" },
                arguments = listOf(
                    navArgument(Routes.Arguments.commonTaskType) { type = NavType.StringType },
                    navArgument(Routes.Arguments.parentId) {
                        type = NavType.LongType
                        defaultValue = -1L // long does not allow null values
                    },
                    navArgument(Routes.Arguments.sprintId) {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) {
                CreateTaskScreen(
                    navController = navController,
                    commonTaskType = CommonTaskType.valueOf(it.arguments!!.getString(Routes.Arguments.commonTaskType, "")),
                    parentId = it.arguments!!.getLong(Routes.Arguments.parentId).takeIf { it >= 0 },
                    sprintId = it.arguments!!.getLong(Routes.Arguments.sprintId).takeIf { it >= 0 },
                    onError = onError
                )
            }

        }

    }
}

@Composable
fun MoreScreen(
    navController: NavController
) = Column(Modifier.fillMaxSize()) {
    TopAppBar(
        title = { Text(stringResource(R.string.more)) },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    )

    @Composable
    fun Item(
        @DrawableRes iconId: Int,
        @StringRes nameId: Int,
        route: String
    ) = ContainerBox(onClick = { navController.navigate(route) }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(iconId),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Gray
            )

            Spacer(Modifier.width(8.dp))

            Text(stringResource(nameId))
        }
    }

    @Composable
    fun Margin() = Spacer(Modifier.height(8.dp))

    Item(R.drawable.ic_team, R.string.team, Routes.team)
    Margin()
    Item(R.drawable.ic_settings, R.string.settings, Routes.settings)
}