package fr.delcey.dailynino.domain.paging_video

import kotlinx.coroutines.flow.Flow

interface PagingVideoRepository {
    fun getCurrentPageFlow(): Flow<Int>
    fun incrementPage()
    fun resetPage()
}
