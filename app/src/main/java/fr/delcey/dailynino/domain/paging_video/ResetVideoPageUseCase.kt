package fr.delcey.dailynino.domain.paging_video

import fr.delcey.dailynino.domain.video.VideoRepository
import javax.inject.Inject

class ResetVideoPageUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
    private val pagingVideoRepository: PagingVideoRepository,
) {
    suspend fun invoke() {
        videoRepository.resetPagedVideosCache()
        pagingVideoRepository.resetPage()
    }
}
