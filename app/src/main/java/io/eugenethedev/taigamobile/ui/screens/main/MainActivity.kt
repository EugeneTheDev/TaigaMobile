package io.eugenethedev.taigamobile.ui.screens.main

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scaffoldState = rememberScaffoldState()
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
                    bodyContent = { LoginScreen(scaffoldState) }
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun LoginScreen(scaffoldState: ScaffoldState) {
    val mainViewModel: MainViewModel = viewModel()

    val errorMessage = stringResource(R.string.error_message)
    val coroutineScope = rememberCoroutineScope()

    var taigaServerInput by remember { mutableStateOf(TextFieldValue()) }
    var username by remember { mutableStateOf(TextFieldValue()) }
    var password by remember { mutableStateOf(TextFieldValue()) }

    ConstraintLayout(
        modifier = Modifier.fillMaxSize(),
    ) {
        val (logo, loginForm) = createRefs()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.constrainAs(logo) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(loginForm.top)
            }
        ) {

            Image(
                imageVector = vectorResource(R.drawable.ic_taiga_logo),
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
            verticalArrangement = Arrangement.Center,
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
                onValueChange = { taigaServerInput = it },
            )

            LoginTextField(
                value = username,
                labelId = R.string.login_username,
                onValueChange = { username = it },
            )

            LoginTextField(
                value = password,
                labelId = R.string.login_password,
                onValueChange = { password = it },
                visualTransformation = PasswordVisualTransformation(),
                keyboardType = KeyboardType.Password
            )

            Button(
                onClick = { coroutineScope.launch {
                    mainViewModel.onContinueClick(taigaServerInput.text, username.text, password.text)
                    // scaffoldState.snackbarHostState.showSnackbar(errorMessage)
                }},
                modifier = Modifier.padding(top = 24.dp),
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
        }
    )
}

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TaigaMobileTheme {
        LoginScreen(rememberScaffoldState())
    }
}