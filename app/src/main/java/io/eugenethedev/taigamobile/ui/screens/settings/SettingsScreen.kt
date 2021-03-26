package io.eugenethedev.taigamobile.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.navigation.compose.popUpTo
import dev.chrisbanes.accompanist.glide.GlideImage
import io.eugenethedev.taigamobile.BuildConfig
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.components.ConfirmActionAlert
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
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
        }
    )
}

@Composable
fun SettingsScreenContent(
    avatarUrl: String?,
    displayName: String,
    username: String,
    serverUrl: String,
    logout: () -> Unit = {}
) = ConstraintLayout(
    modifier = Modifier.fillMaxSize()
) {
    val (topBar, avatar, logoutIcon, userInfo, appVersion) = createRefs()

    TopAppBar(
        modifier = Modifier.constrainAs(topBar) {
            top.linkTo(parent.top)
        },
        title = { Text(stringResource(R.string.settings)) },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    )

    GlideImage(
        data = avatarUrl ?: R.drawable.default_avatar,
        contentDescription = null,
        fadeIn = true,
        requestBuilder = { error(R.drawable.default_avatar) },
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

    Text(
        text = stringResource(R.string.app_name_with_version_template).format(
            stringResource(R.string.app_name), BuildConfig.VERSION_NAME
        ),
        style = MaterialTheme.typography.body1.merge(TextStyle(fontSize = 18.sp)),
        color = Color.Gray,
        modifier = Modifier.constrainAs(appVersion) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom, 16.dp)
        }
    )
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