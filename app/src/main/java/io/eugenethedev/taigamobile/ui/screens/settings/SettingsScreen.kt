package io.eugenethedev.taigamobile.ui.screens.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.glide.rememberGlidePainter
import com.google.accompanist.insets.navigationBarsHeight
import io.eugenethedev.taigamobile.BuildConfig
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.ThemeSetting
import io.eugenethedev.taigamobile.ui.components.ConfirmActionAlert
import io.eugenethedev.taigamobile.ui.components.containers.ContainerBox
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithBackButton
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.clickableUnindicated
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError
import timber.log.Timber

@Composable
fun SettingsScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {}
) {
    val viewModel: SettingsViewModel = viewModel()
    remember {
        viewModel.start()
        null
    }

    val user by viewModel.user.observeAsState()
    user?.subscribeOnError(onError)

    val themeSetting by viewModel.themeSetting.observeAsState()

    SettingsScreenContent(
        avatarUrl = user?.data?.avatarUrl,
        displayName = user?.data?.displayName.orEmpty(),
        username = user?.data?.username.orEmpty(),
        serverUrl = viewModel.serverUrl,
        navigateBack = navController::popBackStack,
        logout = {
            viewModel.logout()
            navController.navigate(Routes.login) {
                popUpTo(Routes.settings) { inclusive = true }
            }
        },
        themeSetting = themeSetting ?: ThemeSetting.System,
        switchTheme = viewModel::switchTheme
    )
}

@Composable
fun SettingsScreenContent(
    avatarUrl: String?,
    displayName: String,
    username: String,
    serverUrl: String,
    navigateBack: () -> Unit = {},
    logout: () -> Unit = {},
    themeSetting: ThemeSetting = ThemeSetting.System,
    switchTheme: (ThemeSetting) -> Unit = {}
) = ConstraintLayout(
    modifier = Modifier.fillMaxSize()
) {
    val (topBar, avatar, logoutIcon, userInfo, settings, appVersion) = createRefs()

    AppBarWithBackButton(
        title = { Text(stringResource(R.string.settings)) },
        navigateBack = navigateBack,
        modifier = Modifier.constrainAs(topBar) {
            top.linkTo(parent.top)
        }
    )

    Image(
        painter = rememberGlidePainter(
            request = avatarUrl ?: R.drawable.default_avatar,
            fadeIn = true,
            requestBuilder = { error(R.drawable.default_avatar) },
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(120.dp)
            .clip(MaterialTheme.shapes.large)
            .constrainAs(avatar) {
                top.linkTo(topBar.bottom, margin = 20.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
    )

    // logout
    var isAlertVisible by remember { mutableStateOf(false) }
    if (isAlertVisible) {
        ConfirmActionAlert(
            title = stringResource(R.string.logout_title),
            text = stringResource(R.string.logout_text),
            onConfirm = {
                isAlertVisible = false
                logout()
            },
            onDismiss = { isAlertVisible = false }
        )
    }

    IconButton(
        onClick = { isAlertVisible = true },
        modifier = Modifier.constrainAs(logoutIcon) {
            top.linkTo(avatar.top)
            start.linkTo(avatar.end)
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_logout),
            contentDescription = null,
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(28.dp)
        )
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.constrainAs(userInfo) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(avatar.bottom, 8.dp)
        }
    ) {
        Text(
            text = displayName,
            style = MaterialTheme.typography.h6
        )

        Text(
            text = stringResource(R.string.username_template).format(username),
            style = MaterialTheme.typography.subtitle1
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = serverUrl,
            style = MaterialTheme.typography.body2,
            color = Color.Gray
        )
    }

    // settings itself
    Column (
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.constrainAs(settings) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(userInfo.bottom, 24.dp)
            }
    ) {
        // appearance
        SettingsBlock(
            titleId = R.string.appearance,
            items = listOf {
                SettingItem(
                    textId = R.string.theme_title,
                    itemWeight = 0.4f
                ) {
                    var isMenuExpanded by remember { mutableStateOf(false) }
                    val transitionState = remember { MutableTransitionState(isMenuExpanded) }
                    transitionState.targetState = isMenuExpanded

                    @Composable
                    fun titleForThemeSetting(themeSetting: ThemeSetting) = stringResource(
                        when (themeSetting) {
                            ThemeSetting.System -> R.string.theme_system
                            ThemeSetting.Light -> R.string.theme_light
                            ThemeSetting.Dark -> R.string.theme_dark
                        }
                    )

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickableUnindicated {
                                isMenuExpanded = !isMenuExpanded
                            }
                        ) {

                            Text(
                                text = titleForThemeSetting(themeSetting),
                                style = MaterialTheme.typography.subtitle1,
                                color = MaterialTheme.colors.primary
                            )

                            val arrowRotation by updateTransition(
                                transitionState,
                                label = "arrow"
                            ).animateFloat { if (it) -180f else 0f }

                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_down),
                                contentDescription = null,
                                tint = MaterialTheme.colors.primary,
                                modifier = Modifier.rotate(arrowRotation)
                            )
                        }

                        DropdownMenu(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false }
                        ) {
                            ThemeSetting.values().forEach {
                                DropdownMenuItem(
                                    onClick = {
                                        isMenuExpanded = false
                                        switchTheme(it)
                                    }
                                ) {
                                    Text(
                                        text = titleForThemeSetting(it),
                                        style = MaterialTheme.typography.body1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )

        // help
        val activity = LocalContext.current as Activity
        SettingsBlock(
            titleId = R.string.help,
            items = listOf {
                SettingItem(
                    textId = R.string.submit_report,
                    onClick = {
                        activity.startActivity(
                            Intent(Intent.ACTION_SEND).also {
                                it.type = "text/plain"

                                (activity.application as TaigaApp).currentLogFile?.let { file ->
                                    it.putExtra(
                                        Intent.EXTRA_STREAM,
                                        FileProvider.getUriForFile(activity, "${activity.packageName}.provider", file)
                                    )
                                }

                                it.putExtra(Intent.EXTRA_SUBJECT, "Report. Version ${BuildConfig.VERSION_NAME}")
                                it.putExtra(Intent.EXTRA_TEXT, "Android: ${Build.VERSION.RELEASE}\nDevice: ${Build.MODEL}\nDescribe in details your problem:")
                            }
                        )
                    }
                )
            }
        )

    }


    Column(
        modifier = Modifier.constrainAs(appVersion) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom, 16.dp)
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.credits_message),
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(horizontal = mainHorizontalScreenPadding),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.app_name_with_version_template).format(
                stringResource(R.string.app_name), BuildConfig.VERSION_NAME
            ),
            style = MaterialTheme.typography.body1.merge(TextStyle(fontSize = 18.sp)),
            color = Color.Gray,
        )

        val activity = LocalContext.current as Activity
        val githubUrl = stringResource(R.string.github_url)
        Text(
            text = stringResource(R.string.source_code),
            style = MaterialTheme.typography.body1.merge(SpanStyle(color = MaterialTheme.colors.primary, textDecoration = TextDecoration.Underline)),
            modifier = Modifier.clickableUnindicated { activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))) }
        )

        Spacer(Modifier.navigationBarsHeight())
    }
}

@Composable
private fun SettingsBlock(
    @StringRes titleId: Int,
    items: List<@Composable () -> Unit>
) {
    val verticalMargin = 2.dp

    Text(
        text = stringResource(titleId),
        style = MaterialTheme.typography.subtitle2,
        color = MaterialTheme.colors.primary,
        modifier = Modifier.padding(horizontal = mainHorizontalScreenPadding)
    )

    Spacer(Modifier.height(verticalMargin))

    items.forEach { it() }

    Spacer(Modifier.height(verticalMargin * 4))
}

@Composable
private fun SettingItem(
    @StringRes textId: Int,
    itemWeight: Float = 0.2f,
    onClick: () -> Unit = {},
    item: @Composable BoxScope.() -> Unit = {}
) = ContainerBox(
    verticalPadding = 10.dp,
    onClick = onClick
) {
    assert(itemWeight > 0 && itemWeight < 1) { Timber.w("Item weight must be between 0 and 1") }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(textId),
            modifier = Modifier.weight(1 - itemWeight, fill = false)
        )

        Box(
            modifier = Modifier.weight(itemWeight),
            contentAlignment = Alignment.CenterEnd,
            content = item
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun SettingsScreenPreview() = TaigaMobileTheme {
    SettingsScreenContent(
        avatarUrl = null,
        displayName = "Cool Name",
        username = "username",
        serverUrl = "https://sample.server/"
    )
}