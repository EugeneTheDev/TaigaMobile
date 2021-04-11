package io.eugenethedev.taigamobile.ui.utils

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay


/**
 * Convenient way to dispatch events
 */
class Result<T>(
    val resultStatus: ResultStatus,
    val data: T? = null,
    @StringRes val message: Int? = null
)

enum class ResultStatus {
    SUCCESS,
    ERROR,
    LOADING
}

typealias LiveResult<T> = LiveData<Result<T>>
typealias MutableLiveResult<T> = MutableLiveData<Result<T>>


/**
 * Sometimes little delay is needed to make animations work smooth
 */
suspend fun fixAnimation() = delay(300)
