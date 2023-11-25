package fr.delcey.dailynino.domain.video

import fr.delcey.dailynino.domain.video.model.PagedVideosEntity

interface VideoRepository {
    suspend fun getPagedVideos(page: Int): PagedVideosEntity
    suspend fun resetPagedVideosCache()
}
