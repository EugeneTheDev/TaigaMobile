package io.eugenethedev.taigamobile.ui.screens.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.navigation.compose.popUpTo
import com.google.accompanist.glide.rememberGlidePainter
import io.eugenethedev.taigamobile.BuildConfig
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.components.ConfirmActionAlert
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.clickableUnindicated
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError

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

    val isScrumScreenExpandStatuses by viewModel.isScrumScreenExpandStatuses.observeAsState()
    val isSprintScreenExpandStatuses by viewModel.isSprintScreenExpandStatuses.observeAsState()

    SettingsScreenContent(
        avatarUrl = user?.data?.avatarUrl,
        displayName = user?.data?.displayName.orEmpty(),
        username = user?.data?.username.orEmpty(),
        serverUrl = viewModel.serverUrl,
        logout = {
            viewModel.logout()
            navController.navigate(Routes.login) {
                popUpTo(Routes.settings) { inclusive = true }
            }
        },
        isScrumScreenExpandStatuses = isScrumScreenExpandStatuses ?: false,
        switchScrumScreenExpandStatuses = viewModel::switchScrumScreenExpandStatuses,
        isSprintScreenExpandStatuses = isSprintScreenExpandStatuses ?: false,
        switchSprintScreenExpandStatuses = viewModel::switchSprintScreenExpandStatuses
    )
}

@Composable
fun SettingsScreenContent(
    avatarUrl: String?,
    displayName: String,
    username: String,
    serverUrl: String,
    logout: () -> Unit = {},
    isScrumScreenExpandStatuses: Boolean = false,
    switchScrumScreenExpandStatuses: (Boolean) -> Unit = { _ -> },
    isSprintScreenExpandStatuses: Boolean = false,
    switchSprintScreenExpandStatuses: (Boolean) -> Unit = { _ -> }
) = ConstraintLayout(
    modifier = Modifier.fillMaxSize()
) {
    val (topBar, avatar, logoutIcon, userInfo, settings, appVersion) = createRefs()

    TopAppBar(
        modifier = Modifier.constrainAs(topBar) {
            top.linkTo(parent.top)
        },
        title = { Text(stringResource(R.string.settings)) },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
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

    Column (
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(horizontal = mainHorizontalScreenPadding)
            .constrainAs(settings) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(userInfo.bottom, 24.dp)
            }
    ) {
        Text(
            text = stringResource(R.string.expand_statuses),
            style = MaterialTheme.typography.subtitle1
        )

        Spacer(Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.scrum_expand_statuses),
                modifier = Modifier.weight(0.8f, fill = false)
            )

            Switch(
                checked = isScrumScreenExpandStatuses,
                onCheckedChange = switchScrumScreenExpandStatuses,
                modifier = Modifier.weight(0.2f, fill = false)
            )
        }

        Spacer(Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.sprint_expand_statuses),
                modifier = Modifier.weight(0.8f, fill = false)
            )

            Switch(
                checked = isSprintScreenExpandStatuses,
                onCheckedChange = switchSprintScreenExpandStatuses,
                modifier = Modifier.weight(0.2f, fill = false)
            )
        }
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