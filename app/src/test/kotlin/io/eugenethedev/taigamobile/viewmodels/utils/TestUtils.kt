package io.eugenethedev.taigamobile.viewmodels.utils

import androidx.paging.compose.LazyPagingItems
import io.eugenethedev.taigamobile.domain.paging.CommonPagingSource
import io.eugenethedev.taigamobile.ui.utils.Result
import io.mockk.MockKMatcherScope
import io.mockk.coEvery
import io.mockk.mockkClass
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import kotlin.math.min
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertFails


val accessDeniedException = HttpException(Response.error<String>(403, "".toResponseBody(null)))
val notFoundException = HttpException(Response.error<String>(404, "".toResponseBody(null)))
val createDeniedException = HttpException(Response.error<String>(405, "".toResponseBody(null)))

fun <T> assertResultEquals(expected: Result<T>, actual: Result<T>) {
    assertEquals(expected::class, actual::class)
    assertEquals(expected.data, actual.data)
    assertEquals(expected.message, actual.message)
}

/**
 * This little thingy helps us test LazyPagingItems (that everything loads exactly like it should)
 * Do not call this function directly, look at the next one
 */
fun <T : Any> testLazyPagingItems(klass: KClass<T>, items: LazyPagingItems<T>, stubBlock: suspend MockKMatcherScope.() -> List<T>) {
    val pages = List(CommonPagingSource.PAGE_SIZE * 4) { mockkClass(klass, relaxed = true) }

    coEvery(stubBlock) answers {
        firstArg<Int>().let {
            pages.subList((it - 1) * CommonPagingSource.PAGE_SIZE, min(it * CommonPagingSource.PAGE_SIZE, pages.size))
        }
    }
    items.refresh()

    pages.forEachIndexed { i, item -> assertEquals(item, items[i]) }
    assertFails { items[pages.size] }
}

inline fun <reified T : Any> testLazyPagingItems(items: LazyPagingItems<T>, noinline stubBlock: suspend MockKMatcherScope.() -> List<T>) =
    testLazyPagingItems(T::class, items, stubBlock)

