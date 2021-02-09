package io.eugenethedev.taigamobile.ui.utils

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class Result<T>(
    val status: Status,
    val data: T? = null,
    @StringRes val message: Int? = null
)

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

typealias LiveResult<T> = LiveData<Result<T>>
typealias MutableLiveResult<T> = MutableLiveData<Result<T>>
