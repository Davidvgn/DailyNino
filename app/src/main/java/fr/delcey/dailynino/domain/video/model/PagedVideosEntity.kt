package fr.delcey.dailynino.domain.video.model

sealed class PagedVideosEntity {
    data class Success(
        val videos: List<VideoEntity>?,
        val currentPage: Int,
        val hasMore: Boolean,
    ) : PagedVideosEntity()

    data object Failure : PagedVideosEntity()
}