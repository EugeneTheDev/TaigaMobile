package io.eugenethedev.taigamobile.viewmodels

import io.eugenethedev.taigamobile.domain.entities.AuthType
import io.eugenethedev.taigamobile.domain.repositories.IAuthRepository
import io.eugenethedev.taigamobile.ui.screens.login.LoginViewModel
import io.eugenethedev.taigamobile.ui.utils.ErrorResult
import io.eugenethedev.taigamobile.ui.utils.SuccessResult
import io.eugenethedev.taigamobile.viewmodels.utils.accessDeniedException
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs

class LoginViewModelTest : BaseViewModelTest() {
    private val mockAuthRepository: IAuthRepository = mockk(relaxed = true)
    private lateinit var viewModel: LoginViewModel

    @BeforeTest
    fun setup() {
        viewModel = LoginViewModel(mockAppComponent).apply {
            authRepository = mockAuthRepository
        }
    }

    @Test
    fun `test login`(): Unit = runBlocking {
        val password = "password"

        coEvery { mockAuthRepository.auth(any(), any(), neq(password), any()) } throws accessDeniedException

        viewModel.login("", AuthType.Normal, "", password)
        assertIs<SuccessResult<Unit>>(viewModel.loginResult.value)

        viewModel.login("", AuthType.Normal, "", password + "wrong")
        assertIs<ErrorResult<Unit>>(viewModel.loginResult.value)
    }
}
