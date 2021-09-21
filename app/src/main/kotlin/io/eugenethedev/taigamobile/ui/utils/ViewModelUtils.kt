package io.eugenethedev.taigamobile.ui.utils

import androidx.annotation.StringRes
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredFunctions
import timber.log.Timber
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.commons.*

/**
 * Sometimes little delay is needed to make animations work smooth
 */
suspend fun fixAnimation() = delay(200)

inline fun <T> MutableStateFlow<Result<T>>.loadOrError(
    @StringRes messageId: Int = R.string.common_error_message,
    preserveValue: Boolean = true,
    showLoading: Boolean = true,
    load: () -> T?
) {
    if (showLoading) {
        value = LoadingResult(value.data.takeIf { preserveValue })
    }

    value = try {
        SuccessResult(load())
    } catch (e: Exception) {
        Timber.wtf(e)
        ErrorResult(messageId)
    }
}

/**
 * Convert Flow to instance of LazyPagingItems
 * TODO fix of https://issuetracker.google.com/issues/177245496
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any> Flow<PagingData<T>>.asLazyPagingItems(scope: CoroutineScope) = cachedIn(scope).let { flow ->
    // yep, working with instance of LazyPagingItems via reflection
    LazyPagingItems::class.constructors.toList().first().run {
        call(flow) as LazyPagingItems<T>
    }.also { items ->
        scope.launch {
            LazyPagingItems::class.declaredFunctions.find { it.name == "collectPagingData" }!!.apply {
                callSuspend(items)
            }
        }
        scope.launch {
            LazyPagingItems::class.declaredFunctions.find { it.name == "collectLoadState" }!!.apply {
                callSuspend(items)
            }
        }
    }
}