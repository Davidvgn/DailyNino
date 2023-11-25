package fr.delcey.dailynino.domain.paging_video

import javax.inject.Inject

class IncreaseCurrentVideoPageUseCase @Inject constructor(
    private val pagingVideoRepository: PagingVideoRepository,
) {
    fun invoke() {
        pagingVideoRepository.incrementPage()
    }
}
