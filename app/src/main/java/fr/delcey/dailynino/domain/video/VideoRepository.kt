package fr.delcey.dailynino.domain.video

import fr.delcey.dailynino.domain.video.model.VideoEntity

interface VideoRepository {
    suspend fun getVideos(): List<VideoEntity>?
}
