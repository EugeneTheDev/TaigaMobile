package io.eugenethedev.taigamobile.ui.commons

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
    Loading,
    Nothing
}

typealias MutableResultFlow<T> = MutableStateFlow<Result<T>>
fun <T> MutableResultFlow(value: Result<T> = Result(ResultStatus.Nothing)) = MutableStateFlow(value)
typealias ResultFlow<T> = StateFlow<Result<T>>
