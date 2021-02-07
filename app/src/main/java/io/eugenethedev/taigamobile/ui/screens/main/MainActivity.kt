package io.eugenethedev.taigamobile.ui.screens.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.setContent
import io.eugenethedev.taigamobile.ui.screens.login.LoginScreen
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
                    bodyContent = { MainScreen(scaffoldState, it) }
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun MainScreen(scaffoldState: ScaffoldState, paddingValues: PaddingValues) {
    val coroutineScope = rememberCoroutineScope()

    val onError: (String) -> Unit = { message -> coroutineScope.launch { scaffoldState.snackbarHostState.showSnackbar(message) } }
    LoginScreen(
        onError = onError
    )
}