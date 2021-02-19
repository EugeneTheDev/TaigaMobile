package io.eugenethedev.taigamobile.ui.utils

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

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
