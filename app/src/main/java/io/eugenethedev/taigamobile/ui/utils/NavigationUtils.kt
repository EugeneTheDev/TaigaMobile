package io.eugenethedev.taigamobile.ui.utils

import android.os.Parcelable
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.ui.screens.main.Routes

/**
 * Since navigating to some screens requires several arguments, here are some utils
 * to make navigation code more readable
 */

typealias NavigateToTask = (id: Long, type: CommonTaskType, ref: Int) -> Unit
fun NavController.navigateToTaskScreen(id: Long, type: CommonTaskType, ref: Int)
    = navigate("${Routes.commonTask}/$id/$type/$ref")

typealias NavigateToCreateTask = (type: CommonTaskType, parentId: Long?, sprintId: Long?, statusId: Long, swimlaneId: Long?) -> Unit
fun NavController.navigateToCreateTaskScreen(
    type: CommonTaskType,
    parentId: Long? = null,
    sprintId: Long? = null,
    statusId: Long? = null,
    swimlaneId: Long? = null
) = Routes.Arguments.let { navigate("${Routes.createTask}/$type?${it.parentId}=${parentId ?: -1}&${it.sprintId}=${sprintId ?: -1}&${it.statusId}=${statusId ?: -1}&${it.swimlaneId}=${swimlaneId ?: -1}") }

typealias NavigateToSprint = (sprintId: Long) -> Unit
fun NavController.navigateToSprint(sprintId: Long) = navigate("${Routes.sprint}/$sprintId")
