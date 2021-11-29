package io.eugenethedev.taigamobile.viewmodels

import androidx.lifecycle.viewmodel.compose.viewModel
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.testdata.TestData
import io.eugenethedev.taigamobile.ui.screens.kanban.KanbanViewModel
import io.eugenethedev.taigamobile.ui.utils.SuccessResult
import io.eugenethedev.taigamobile.viewmodels.utils.toTeamMember
import io.mockk.coEvery
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class KanbanViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel: KanbanViewModel

    @BeforeTest
    fun setup() {
        viewModel = KanbanViewModel(mockAppComponent)
    }

    private fun <T> checkListsSize(expectedList: List<T>?, actualList: List<T>?) {
        assertEquals(
            expected = expectedList?.size,
            actual = actualList?.size
        )
    }

    @Test
    fun `test on open`(): Unit = runBlocking {
        val testStatuses =
            mockTaskRepository.getStatuses(CommonTaskType.UserStory).sortedBy { it.name }
        val testTeam = TestData.users.sortedBy { it.username }.mapIndexed { index, user ->
            user.toTeamMember(
                id = index.toLong(),
                name = "testName${index}",
                role = "testRole${index}",
                totalPower = index
            )
        }
        val testSwimlanes = listOf(Swimlane(1, "test swimlane", 1))

        coEvery { mockTaskRepository.getStatuses(any()) } returns testStatuses
        coEvery { mockUsersRepository.getTeam() } returns testTeam
        coEvery { mockTaskRepository.getSwimlanes() } returns testSwimlanes

        viewModel.onOpen()
        assertIs<SuccessResult<List<Status>>>(viewModel.statuses.value)
        assertIs<SuccessResult<List<User>>>(viewModel.team.value)
        assertIs<SuccessResult<List<CommonTaskExtended>>>(viewModel.stories.value)
        assertIs<SuccessResult<List<Swimlane?>>>(viewModel.swimlanes.value)

        checkListsSize(testStatuses, viewModel.statuses.value.data)
        viewModel.statuses.value.data?.let { statuses ->
            statuses.sortedBy { it.name }.forEachIndexed { index, status ->
                assertEquals(
                    expected = testStatuses[index].name,
                    actual = status.name
                )
                assertEquals(
                    expected = testStatuses[index].color,
                    actual = status.color
                )
            }
        }

        checkListsSize(testTeam, viewModel.team.value.data)
        viewModel.team.value.data?.let { users ->
            users.sortedBy { it.username }.forEachIndexed { index, user ->
                assertEquals(
                    expected = testTeam[index].id,
                    actual = user.id
                )
                assertEquals(
                    expected = testTeam[index].username,
                    actual = user.username
                )
            }
        }

        val testSwimlanesWithNull = (listOf(null) + testSwimlanes).sortedBy { it?.id }
        checkListsSize(testSwimlanesWithNull, viewModel.swimlanes.value.data)
        viewModel.swimlanes.value.data?.let { swimlanes ->
            swimlanes.sortedBy { it?.id }.forEachIndexed { index, swimlane ->
                assertEquals(
                    expected = testSwimlanesWithNull[index]?.id,
                    actual = swimlane?.id
                )
                assertEquals(
                    expected = testSwimlanesWithNull[index]?.name,
                    actual = swimlane?.name
                )
                assertEquals(
                    expected = testSwimlanesWithNull[index]?.order,
                    actual = swimlane?.order
                )
            }
        }
    }

    @Test
    fun `test select swimlane`() = runBlocking {
        val swimlane = Swimlane(1, "test name", 1)

        viewModel.selectSwimlane(swimlane)
        assertEquals(
            expected = swimlane,
            actual = viewModel.selectedSwimlane.value
        )

        viewModel.selectSwimlane(null)
        assertEquals(
            expected = null,
            actual = viewModel.selectedSwimlane.value
        )
    }
}