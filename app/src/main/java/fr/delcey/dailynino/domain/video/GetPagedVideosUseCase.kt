package fr.delcey.dailynino.domain.video

import fr.delcey.dailynino.domain.paging_video.PagingVideoRepository
import fr.delcey.dailynino.domain.video.model.PagedVideosEntity
import fr.delcey.dailynino.domain.video.model.VideoEntity
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class GetPagedVideosUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
    private val pagingVideoRepository: PagingVideoRepository,
) {
    fun invoke(): Flow<PagedVideosEntity> = channelFlow {
        val aggregatedVideos = mutableListOf<VideoEntity>()

        pagingVideoRepository.getCurrentPageFlow().collect { page ->
            if (page == 1) {
                aggregatedVideos.clear()
            }
            queryAggregateAndSendVideos(page, aggregatedVideos)
        }
    }

    private suspend fun ProducerScope<PagedVideosEntity>.queryAggregateAndSendVideos(
        page: Int,
        aggregatedVideos: MutableList<VideoEntity>
    ) {
        when (val newPageVideos = videoRepository.getPagedVideos(page)) {
            is PagedVideosEntity.Failure -> {
                // Send error downstream for first page only, the other pages will retry silently
                if (page == 1) {
                    trySend(newPageVideos)
                }
                delay(5.seconds)
                queryAggregateAndSendVideos(page, aggregatedVideos)
            }
            is PagedVideosEntity.Success -> {
                newPageVideos.videos?.let { aggregatedVideos.addAll(it) }
                trySend(
                    newPageVideos.copy(videos = aggregatedVideos)
                )
            }
        }
    }
}
