package fr.delcey.dailynino.data.video

import fr.delcey.dailynino.data.dailymotion.DailyMotionApi
import fr.delcey.dailynino.data.dailymotion.model.VideosDto
import fr.delcey.dailynino.domain.video.VideoRepository
import fr.delcey.dailynino.domain.video.model.VideoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class VideoRepositoryRetrofit @Inject constructor(
    private val dailyMotionApi: DailyMotionApi
) : VideoRepository {
    companion object {
        private const val VIDEOS_QUERY_FIELDS = "id,created_time,description,duration,thumbnail_url,title"
    }

    override suspend fun getVideos(): List<VideoEntity>? = withContext(Dispatchers.IO) {
        try {
            mapVideoEntities(dailyMotionApi.getVideos(VIDEOS_QUERY_FIELDS))
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            e.printStackTrace()
            null
        }
    }

    private fun mapVideoEntities(videosDto: VideosDto): List<VideoEntity> = videosDto.videos?.mapNotNull { videoDto ->
        VideoEntity(
            id = videoDto.id ?: return@mapNotNull null,
            title = videoDto.title ?: return@mapNotNull null,
            description = videoDto.description ?: return@mapNotNull null,
            duration = videoDto.duration?.seconds,
            thumbnailUrl = videoDto.thumbnailUrl ?: return@mapNotNull null,
            createdAt = videoDto.createdTimeEpochSeconds?.let {
                Instant.ofEpochSecond(it.toLong()).atZone(ZoneOffset.UTC)
            } ?: return@mapNotNull null
        )
    } ?: emptyList()
}