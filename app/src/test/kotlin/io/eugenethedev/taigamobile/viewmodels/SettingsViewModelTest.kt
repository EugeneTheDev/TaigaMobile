package io.eugenethedev.taigamobile.viewmodels

import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.state.ThemeSetting
import io.eugenethedev.taigamobile.ui.screens.settings.SettingsViewModel
import io.eugenethedev.taigamobile.ui.utils.ErrorResult
import kotlinx.coroutines.runBlocking
import io.eugenethedev.taigamobile.ui.utils.SuccessResult
import io.eugenethedev.taigamobile.viewmodels.utils.assertResultEquals
import io.eugenethedev.taigamobile.viewmodels.utils.notFoundException
import io.mockk.*
import io.mockk.coVerify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs


class SettingsViewModelTest : BaseViewModelTest() {
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

    @Test
    fun `test on open`(): Unit = runBlocking {
        viewModel.onOpen()
        assertResultEquals(SuccessResult(mockUser), viewModel.user.value)

        coEvery { mockUsersRepository.getMe() } throws notFoundException
        viewModel.onOpen()
        assertIs<ErrorResult<User>>(viewModel.user.value)
    }

    @Test
    fun `test logout`(): Unit = runBlocking {
        viewModel.logout()
        coVerify { mockSession.reset() }
    }

    @Test
    fun `test switch theme`(): Unit = runBlocking {
        val themeSetting = ThemeSetting.Light
        viewModel.switchTheme(themeSetting)
        coVerify { mockSettings.changeThemeSetting(eq(themeSetting)) }
    }
}
