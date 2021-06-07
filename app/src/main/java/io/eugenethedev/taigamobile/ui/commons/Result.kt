package io.eugenethedev.taigamobile.ui.commons

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Convenient way to dispatch events
 */
class Result<T>(
    val resultStatus: ResultStatus,
    val data: T? = null,
    @StringRes val message: Int? = null
)

enum class ResultStatus {
    Success,
    Error,
    Loading
}

typealias LiveResult<T> = LiveData<Result<T>?>
typealias MutableLiveResult<T> = MutableLiveData<Result<T>?>
