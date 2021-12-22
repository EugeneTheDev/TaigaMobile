package io.eugenethedev.taigamobile.ui.screens.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.navigationBarsHeight
import io.eugenethedev.taigamobile.BuildConfig
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.state.ThemeSetting
import io.eugenethedev.taigamobile.ui.components.dialogs.ConfirmActionDialog
import io.eugenethedev.taigamobile.ui.components.DropdownSelector
import io.eugenethedev.taigamobile.ui.components.containers.ContainerBox
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithBackButton
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.theme.shapes
import io.eugenethedev.taigamobile.ui.utils.activity
import io.eugenethedev.taigamobile.ui.utils.clickableUnindicated
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError
import timber.log.Timber

@Composable
fun SettingsScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {}
) {
    val viewModel: SettingsViewModel = viewModel()
    LaunchedEffect(Unit) {
        viewModel.onOpen()
    }

    val serverUrl by viewModel.serverUrl.collectAsState()

    val user by viewModel.user.collectAsState()
    user.subscribeOnError(onError)

    val themeSetting by viewModel.themeSetting.collectAsState()

    SettingsScreenContent(
        avatarUrl = user.data?.avatarUrl,
        displayName = user.data?.displayName.orEmpty(),
        username = user.data?.username.orEmpty(),
        serverUrl = serverUrl,
        navigateBack = navController::popBackStack,
        logout = {
            viewModel.logout()
            navController.navigate(Routes.login) {
                popUpTo(Routes.settings) { inclusive = true }
            }
        },
        themeSetting = themeSetting,
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
        painter = rememberImagePainter(
            data = avatarUrl ?: R.drawable.default_avatar,
            builder = {
                error(R.drawable.default_avatar)
                crossfade(true)
            },
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(120.dp)
            .clip(shapes.large)
            .constrainAs(avatar) {
                top.linkTo(topBar.bottom, margin = 20.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
    )

    // logout
    var isAlertVisible by remember { mutableStateOf(false) }
    if (isAlertVisible) {
        ConfirmActionDialog(
            title = stringResource(R.string.logout_title),
            text = stringResource(R.string.logout_text),
            onConfirm = {
                isAlertVisible = false
                logout()
            },
            onDismiss = { isAlertVisible = false },
            iconId = R.drawable.ic_logout
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
            tint = MaterialTheme.colorScheme.primary,
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
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = stringResource(R.string.username_template).format(username),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = serverUrl,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
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
                    @Composable
                    fun titleForThemeSetting(themeSetting: ThemeSetting) = stringResource(
                        when (themeSetting) {
                            ThemeSetting.System -> R.string.theme_system
                            ThemeSetting.Light -> R.string.theme_light
                            ThemeSetting.Dark -> R.string.theme_dark
                        }
                    )

                    DropdownSelector(
                        items = ThemeSetting.values().toList(),
                        selectedItem = themeSetting,
                        onItemSelected = { switchTheme(it) },
                        itemContent = {
                            Text(
                                text = titleForThemeSetting(it),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        selectedItemContent = {
                            Text(
                                text = titleForThemeSetting(it),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }
            }
        )

        // help
        val activity = LocalContext.current.activity
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
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = mainHorizontalScreenPadding),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.app_name_with_version_template).format(
                stringResource(R.string.app_name), BuildConfig.VERSION_NAME
            ),
            style = MaterialTheme.typography.bodyLarge.merge(TextStyle(fontSize = 18.sp)),
            color = MaterialTheme.colorScheme.outline,
        )

        val activity = LocalContext.current.activity
        val githubUrl = stringResource(R.string.github_url)
        Text(
            text = stringResource(R.string.source_code),
            style = MaterialTheme.typography.bodyLarge.merge(SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)),
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
    val verticalPadding = 2.dp

    Text(
        text = stringResource(titleId),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = mainHorizontalScreenPadding)
    )

    Spacer(Modifier.height(verticalPadding))

    items.forEach { it() }

    Spacer(Modifier.height(verticalPadding * 4))
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
