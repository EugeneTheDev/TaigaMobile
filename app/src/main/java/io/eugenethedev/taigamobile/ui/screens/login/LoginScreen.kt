package io.eugenethedev.taigamobile.ui.screens.login

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.navigation.compose.popUpTo
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.utils.Status

@ExperimentalMaterialApi
@Composable
fun LoginScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: LoginViewModel = viewModel()

    val loginResult by viewModel.loginResult.observeAsState()
    loginResult?.apply {
        when(status) {
            Status.ERROR -> onError(message!!)
            Status.SUCCESS -> {
                navController.navigate(Routes.stories) {
                    popUpTo(Routes.login) { inclusive = true }
                }
            }
            else -> {}
        }
    }

    var taigaServerInput by remember { mutableStateOf(TextFieldValue()) }
    var usernameInput by remember { mutableStateOf(TextFieldValue()) }
    var passwordInput by remember { mutableStateOf(TextFieldValue()) }

    var isServerInputErrorValue by remember { mutableStateOf(false) }

    fun String.validateServerInput() = this.matches(Regex("^https://.+"))

    LoginScreenContent(
        taigaServerInput = taigaServerInput,
        usernameInput = usernameInput,
        passwordInput = passwordInput,
        onTaigaServerInputChange = {
            taigaServerInput = it
            if (isServerInputErrorValue) isServerInputErrorValue = false
        },
        onUsernameInputChange = { usernameInput = it },
        onPasswordInputChange = { passwordInput = it },
        onContinueClick = {
            if (!taigaServerInput.text.validateServerInput()) {
                isServerInputErrorValue = true
            } else if (usernameInput.text.isNotBlank() && passwordInput.text.isNotBlank()) {
                viewModel.onContinueClick(
                    taigaServerInput.text.trimEnd('/'),
                    usernameInput.text,
                    passwordInput.text
                )
            }
        },
        isServerInputErrorValue = isServerInputErrorValue,
        isLoadingValue = loginResult?.status === Status.LOADING
    )
}

@Composable
fun LoginScreenContent(
    taigaServerInput: TextFieldValue = TextFieldValue(),
    usernameInput: TextFieldValue = TextFieldValue(),
    passwordInput: TextFieldValue = TextFieldValue(),
    onTaigaServerInputChange: (TextFieldValue) -> Unit = {},
    onUsernameInputChange: (TextFieldValue) -> Unit = {},
    onPasswordInputChange: (TextFieldValue) -> Unit = {},
    onContinueClick: () -> Unit = {},
    isServerInputErrorValue: Boolean = false,
    isLoadingValue: Boolean = false,
) = ConstraintLayout(
    modifier = Modifier.fillMaxSize(),
) {
    val (logo, loginForm, button) = createRefs()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.constrainAs(logo) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(parent.top)
            bottom.linkTo(loginForm.top)
        }
    ) {

        Image(
            imageVector = vectorResource(R.drawable.ic_taiga_logo),
            contentDescription = "",
            modifier = Modifier
                .size(130.dp)
                .padding(bottom = 8.dp)
        )

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.h5,
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.constrainAs(loginForm) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        }
    ) {
        LoginTextField(
            value = taigaServerInput,
            labelId = R.string.login_taiga_server,
            onValueChange = onTaigaServerInputChange,
            isErrorValue = isServerInputErrorValue
        )

        LoginTextField(
            value = usernameInput,
            labelId = R.string.login_username,
            onValueChange = onUsernameInputChange,
        )

        LoginTextField(
            value = passwordInput,
            labelId = R.string.login_password,
            onValueChange = onPasswordInputChange,
            visualTransformation = PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.constrainAs(button) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(loginForm.bottom, 24.dp)
        }
    ) {
        if (isLoadingValue) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        } else {
            Button(
                onClick = onContinueClick,
                contentPadding = PaddingValues(start = 40.dp, end = 40.dp)
            ) {
                Text(
                    text = stringResource(R.string.login_continue)
                )
            }
        }

    }
}

@Composable
fun LoginTextField(
    value: TextFieldValue,
    @StringRes labelId: Int,
    onValueChange: (TextFieldValue) -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    isErrorValue: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp)
            .padding(bottom = 6.dp),
        textStyle = MaterialTheme.typography.subtitle1,
        singleLine = true,
        label = { Text(stringResource(labelId)) },
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Done),
        onImeActionPerformed = { action, controller ->
            if (action == ImeAction.Done) {
                controller?.hideSoftwareKeyboard()
            }
        },
        isErrorValue = isErrorValue
    )
}

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun LoginPreview() = TaigaMobileTheme {
    LoginScreenContent()
}