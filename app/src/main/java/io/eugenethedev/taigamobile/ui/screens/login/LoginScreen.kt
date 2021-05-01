package io.eugenethedev.taigamobile.ui.screens.login

import android.app.Activity
import android.view.WindowManager
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
import androidx.compose.ui.platform.LocalContext
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
import androidx.navigation.compose.navigate
import androidx.navigation.compose.popUpTo
import com.google.accompanist.insets.imePadding
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.commons.ResultStatus

@ExperimentalMaterialApi
@Composable
fun LoginScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: LoginViewModel = viewModel()
    val activity = LocalContext.current as Activity
    LaunchedEffect(null) {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    val loginResult by viewModel.loginResult.observeAsState()
    loginResult?.apply {
        when(resultStatus) {
            ResultStatus.ERROR -> onError(message!!)
            ResultStatus.SUCCESS -> {
                navController.navigate(Routes.scrum) {
                    popUpTo(Routes.login) { inclusive = true }
                    activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                }
            }
            else -> {}
        }
    }

    var taigaServerInput by remember { mutableStateOf(TextFieldValue()) }
    var loginInput by remember { mutableStateOf(TextFieldValue()) }
    var passwordInput by remember { mutableStateOf(TextFieldValue()) }

    LoginScreenContent(
        taigaServerInput = taigaServerInput,
        usernameInput = loginInput,
        passwordInput = passwordInput,
        onTaigaServerInputChange = { taigaServerInput = it },
        onUsernameInputChange = { loginInput = it },
        onPasswordInputChange = { passwordInput = it },
        onContinueClick = {
            if (taigaServerInput.text.isNotBlank() && loginInput.text.isNotBlank() && passwordInput.text.isNotBlank()) {
                viewModel.login(
                    taigaServerInput.text.trim(),
                    loginInput.text.trim(),
                    passwordInput.text.trim()
                )
            }
        },
        isLoadingValue = loginResult?.resultStatus in listOf(ResultStatus.LOADING, ResultStatus.SUCCESS)
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
        }.imePadding()
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
            onValueChange = onTaigaServerInputChange
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
    keyboardType: KeyboardType = KeyboardType.Text
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
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
    )
}

@ExperimentalMaterialApi
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun LoginScreenPreview() = TaigaMobileTheme {
    LoginScreenContent()
}