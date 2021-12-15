package io.eugenethedev.taigamobile.viewmodels

import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.ui.screens.settings.SettingsViewModel
import io.eugenethedev.taigamobile.ui.utils.ErrorResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import io.eugenethedev.taigamobile.ui.utils.SuccessResult
import io.eugenethedev.taigamobile.viewmodels.utils.assertResultEquals
import io.eugenethedev.taigamobile.viewmodels.utils.notFoundException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs


class SettingsModelTest : BaseViewModelTest() {
    private lateinit var viewModel: SettingsViewModel

    @BeforeTest
    fun setup() {
        viewModel = SettingsViewModel(mockAppComponent)
    }

    @BeforeTest
    fun settingOfUsers() {
        coEvery { mockUsersRepository.getMe() } returns mockUser
    }

    companion object {
        val mockUser = mockk<User>(relaxed = true)
    }

    fun asserts() {
        assertResultEquals(SuccessResult(mockUser), viewModel.user.value)
    }

    @Test
    fun `test on open`(): Unit = runBlocking {
        viewModel.onOpen()
        asserts()
        coEvery { mockUsersRepository.getMe() } throws notFoundException
        viewModel.onOpen()
        assertIs<ErrorResult<User>>(viewModel.user.value)
    }
}

