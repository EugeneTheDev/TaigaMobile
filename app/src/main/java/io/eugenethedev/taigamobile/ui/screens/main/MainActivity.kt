package io.eugenethedev.taigamobile.ui.screens.main

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    val errorMessage = stringResource(R.string.error_message)
    val coroutineScope = rememberCoroutineScope()

    var taigaServerInput by remember { mutableStateOf(TextFieldValue()) }
    var login by remember { mutableStateOf(TextFieldValue()) }
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
                onValueChange = { taigaServerInput = it },
                labelId = R.string.login_taiga_server,
            )

            LoginTextField(
                value = login,
                onValueChange = { login = it },
                labelId = R.string.login_login,
            )

            LoginTextField(
                value = password,
                onValueChange = { password = it },
                labelId = R.string.login_password,
            )

            Button(
                onClick = { coroutineScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(errorMessage)
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
    onValueChange: (TextFieldValue) -> Unit,
    @StringRes labelId: Int
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