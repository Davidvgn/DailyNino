package fr.delcey.dailynino.data.paging_video

import dagger.hilt.android.scopes.ViewModelScoped
import fr.delcey.dailynino.domain.paging_video.PagingVideoRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@ViewModelScoped
class PagingVideoRepositoryInMemory @Inject constructor() : PagingVideoRepository {

    companion object {
        // DailyMotion API's documentation states that first page is 1
        // https://developers.dailymotion.com/api/#response-lists
        private const val DAILYMOTION_INITIAL_PAGE_VALUE = 1
    }

    private val currentPageMutableSharedFlow = MutableSharedFlow<Int>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST).apply {
        tryEmit(DAILYMOTION_INITIAL_PAGE_VALUE)
    }

    override fun getCurrentPageFlow(): Flow<Int> = currentPageMutableSharedFlow

    override fun incrementPage() {
        currentPageMutableSharedFlow.tryEmit(currentPageMutableSharedFlow.replayCache.first() + 1)
    }

    override fun resetPage() {
        currentPageMutableSharedFlow.tryEmit(DAILYMOTION_INITIAL_PAGE_VALUE)
    }
}