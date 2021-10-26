package io.eugenethedev.taigamobile.domain.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import timber.log.Timber

class CommonPagingSource<T : Any>(
    private val loadBackend: suspend (Int) -> List<T>
) : PagingSource<Int, T>() {
    companion object {
        const val PAGE_SIZE = 20
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            val page = anchorPage?.prevKey?.plus(1)
            page
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: 1
        return try {
            val response = loadBackend(page)
            LoadResult.Page(
                data = response,
                prevKey = (page - 1).takeIf { it > 0 },
                nextKey = if (response.isNotEmpty()) page + 1 else null
            )
        } catch (e: Exception) {
            Timber.w(e)
            LoadResult.Error(e)
        }
    }
}
