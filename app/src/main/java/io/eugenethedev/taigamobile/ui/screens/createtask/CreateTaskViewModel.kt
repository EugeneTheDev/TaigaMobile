package io.eugenethedev.taigamobile.ui.screens.createtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.ui.commons.ScreensState
import io.eugenethedev.taigamobile.ui.commons.MutableLiveResult
import io.eugenethedev.taigamobile.ui.commons.Result
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class CreateTaskViewModel : ViewModel() {
    @Inject lateinit var tasksRepository: ITasksRepository
    @Inject lateinit var screensState: ScreensState

    init {
        TaigaApp.appComponent.inject(this)
    }

    val creationResult = MutableLiveResult<CommonTask>()

    fun createTask(
        commonTaskType: CommonTaskType,
        title: String,
        description: String,
        parentId: Long? = null,
        sprintId: Long? = null
    ) = viewModelScope.launch {
        creationResult.value = Result(ResultStatus.Loading)

        creationResult.value = try {
            Result(ResultStatus.Success, tasksRepository.createCommonTask(commonTaskType, title, description, parentId, sprintId)).also {
                screensState.modify()
            }
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, message = R.string.permission_error)
        }
    }
}