package io.eugenethedev.taigamobile.ui.utils

import androidx.annotation.StringRes
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.commons.MutableLiveResult
import io.eugenethedev.taigamobile.ui.commons.Result
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import kotlinx.coroutines.delay
import timber.log.Timber

/**
 * Sometimes little delay is needed to make animations work smooth
 */
suspend fun fixAnimation() = delay(200)


inline fun <T> MutableLiveResult<T>.loadOrError(
    @StringRes messageId: Int = R.string.common_error_message,
    preserveValue: Boolean = true,
    showLoading: Boolean = true,
    load: () -> T?
) {
    if (showLoading) {
        value = Result(ResultStatus.Loading, value?.data.takeIf { preserveValue })
    }

    value = try {
        Result(ResultStatus.Success, load())
    } catch (e: Exception) {
        Timber.w(e)
        Result(ResultStatus.Error, message = messageId)
    }
}
