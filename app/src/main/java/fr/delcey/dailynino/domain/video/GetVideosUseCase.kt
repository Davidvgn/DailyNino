package fr.delcey.dailynino.domain.video

import fr.delcey.dailynino.domain.video.model.VideoEntity
import javax.inject.Inject

class GetVideosUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    suspend fun invoke(): List<VideoEntity>? = videoRepository.getVideos()
}
