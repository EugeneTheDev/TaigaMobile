package io.eugenethedev.taigamobile.ui.screens.login

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.insets.imePadding
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.commons.ResultStatus

@Composable
fun LoginScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: LoginViewModel = viewModel()

    val loginResult by viewModel.loginResult.observeAsState()
    loginResult?.apply {
        when(resultStatus) {
            ResultStatus.Error -> onError(message!!)
            ResultStatus.Success -> {
                navController.navigate(Routes.dashboard) {
                    popUpTo(Routes.login) { inclusive = true }
                }
            }
            else -> {}
        }
    }

    LoginScreenContent(
        onContinueClick = viewModel::login,
        isLoadingValue = loginResult?.resultStatus in listOf(ResultStatus.Loading, ResultStatus.Success)
    )
}

@Composable
fun LoginScreenContent(
    onContinueClick: (server: String, login: String, password: String) -> Unit = { _, _, _ -> },
    isLoadingValue: Boolean = false,
) = ConstraintLayout(
    modifier = Modifier.fillMaxSize(),
) {
    val taigaGlobalHost = stringResource(R.string.global_taiga_host)
    var taigaServerInput by remember { mutableStateOf(TextFieldValue(taigaGlobalHost)) }
    var loginInput by remember { mutableStateOf(TextFieldValue()) }
    var passwordInput by remember { mutableStateOf(TextFieldValue()) }

    var isServerInputError by remember { mutableStateOf(false) }
    var isLoginInputError by remember { mutableStateOf(false) }
    var isPasswordInputError by remember { mutableStateOf(false) }

    val (logo, loginForm, button) = createRefs()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.constrainAs(logo) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(parent.top)
            bottom.linkTo(loginForm.top)
        }.imePadding().padding(bottom = 24.dp)
    ) {

        Image(
            painter = painterResource(R.drawable.ic_taiga_tree),
            contentDescription = null,
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
        }.imePadding()
    ) {
        LoginTextField(
            value = taigaServerInput,
            labelId = R.string.login_taiga_server,
            onValueChange = {
                isServerInputError = false
                taigaServerInput = it
            },
            isError = isServerInputError
        )

        LoginTextField(
            value = loginInput,
            labelId = R.string.login_username,
            onValueChange = {
                isLoginInputError = false
                loginInput = it
            },
            isError = isLoginInputError
        )

        LoginTextField(
            value = passwordInput,
            labelId = R.string.login_password,
            onValueChange = {
                isPasswordInputError = false
                passwordInput = it
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password,
            isError = isPasswordInputError
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
                onClick = {
                    isServerInputError = !taigaServerInput.text.matches(Regex("""([\w\d-]+\.)+[\w\d-]+(:\d+)?"""))
                    isLoginInputError = loginInput.text.isBlank()
                    isPasswordInputError = passwordInput.text.isBlank()

                    if (!(isServerInputError || isLoginInputError || isPasswordInputError)) {
                        onContinueClick(
                            taigaServerInput.text.trim(),
                            loginInput.text.trim(),
                            passwordInput.text.trim()
                        )
                    }
                },
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
    isError: Boolean = false
) {
    val focusManager = LocalFocusManager.current

    val textStyle = MaterialTheme.typography.subtitle1.merge(TextStyle(fontWeight = FontWeight.Normal))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp)
            .padding(bottom = 6.dp),
        textStyle = textStyle,
        singleLine = true,
        label = { Text(text = stringResource(labelId), style = textStyle) },
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        isError = isError
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun LoginScreenPreview() = TaigaMobileTheme {
    LoginScreenContent()
}