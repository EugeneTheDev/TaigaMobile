package io.eugenethedev.taigamobile.ui.commons

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Convenient way to dispatch events
 */
sealed class Result<T>(
    val data: T? = null,
    @StringRes val message: Int? = null
)

class SuccessResult<T>(data: T?) : Result<T>(data = data)
class ErrorResult<T>(@StringRes message: Int? = null) : Result<T>(message = message)
class LoadingResult<T>(data: T? = null) : Result<T>(data = data)
class NothingResult<T> : Result<T>()

typealias MutableResultFlow<T> = MutableStateFlow<Result<T>>
fun <T> MutableResultFlow(value: Result<T> = NothingResult()) = MutableStateFlow(value)
typealias ResultFlow<T> = StateFlow<Result<T>>
