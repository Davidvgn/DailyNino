package fr.delcey.dailynino.data.video

import android.app.Application
import fr.delcey.dailynino.data.DailyMotionRetrofitCache
import fr.delcey.dailynino.data.dailymotion.DailyMotionApi
import fr.delcey.dailynino.data.dailymotion.model.VideoDto
import fr.delcey.dailynino.domain.video.VideoRepository
import fr.delcey.dailynino.domain.video.model.PagedVideosEntity
import fr.delcey.dailynino.domain.video.model.VideoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.Cache
import java.io.IOException
import java.time.Instant
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class VideoRepositoryRetrofit @Inject constructor(
    private val application: Application,
    @DailyMotionRetrofitCache
    private val cache: Cache,
    private val dailyMotionApi: DailyMotionApi,
) : VideoRepository {
    companion object {
        private const val VIDEOS_QUERY_FIELDS =
            "id,created_time,description,duration,thumbnail_60_url,thumbnail_62_url,thumbnail_120_url,thumbnail_180_url,thumbnail_240_url,thumbnail_360_url,thumbnail_480_url,thumbnail_720_url,thumbnail_1080_url,thumbnail_url,title"

        /**
         * See [selectBestThumbnailSize]
         */
        private const val SCREEN_WIDTH_TO_THUMBNAIL_HEIGHT_RATIO = (0.3 * 9) / 16
    }

    override suspend fun getPagedVideos(page: Int): PagedVideosEntity = withContext(Dispatchers.IO) {
        try {
            val pagedVideosDto = dailyMotionApi.getPagedVideos(page, VIDEOS_QUERY_FIELDS)
            val screenWidth = application.resources.displayMetrics.widthPixels
            val targetThumbnailHeight = (screenWidth * SCREEN_WIDTH_TO_THUMBNAIL_HEIGHT_RATIO).toInt()

            PagedVideosEntity.Success(
                videos = mapVideoEntities(pagedVideosDto.videos, targetThumbnailHeight),
                currentPage = page,
                hasMore = pagedVideosDto.hasMore == true
            )
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            e.printStackTrace()
            PagedVideosEntity.Failure
        }
    }

    override suspend fun resetPagedVideosCache() = withContext(Dispatchers.IO) {
        try {
            cache.evictAll()
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    private fun mapVideoEntities(
        videoDtos: List<VideoDto>?,
        targetThumbnailHeight: Int,
    ): List<VideoEntity>? = videoDtos?.mapNotNull { videoDto ->
        VideoEntity(
            id = videoDto.id ?: return@mapNotNull null,
            title = videoDto.title ?: return@mapNotNull null,
            description = videoDto.description ?: return@mapNotNull null,
            duration = videoDto.duration?.seconds,
            thumbnailUrl = selectBestThumbnailSize(videoDto, targetThumbnailHeight) ?: return@mapNotNull null,
            createdAt = videoDto.createdTimeEpochSeconds?.let {
                Instant.ofEpochSecond(it.toLong()).atZone(ZoneOffset.UTC)
            } ?: return@mapNotNull null
        )
    }

    /**
     * On Home page, video thumbnails take up to 30% of screen width.
     * With a constraint ratio of 16:9, it means the height of the thumbnail won't be higher than width * 0.3 * 9 / 16.
     * Given backend thumbnail possible height presets are 60, 62, 120, 180, 240, 360, 480, 720 and 1080,
     * we can compute from the current screen width the optimal preset to use.
     */
    private fun selectBestThumbnailSize(videoDto: VideoDto, targetThumbnailHeight: Int): String? =
        when (targetThumbnailHeight) {
            in 0..60 -> videoDto.thumbnailUrl60
            in 61..62 -> videoDto.thumbnailUrl62
            in 63..120 -> videoDto.thumbnailUrl120
            in 121..180 -> videoDto.thumbnailUrl180
            in 181..240 -> videoDto.thumbnailUrl240
            in 241..360 -> videoDto.thumbnailUrl360
            in 361..480 -> videoDto.thumbnailUrl480
            in 481..720 -> videoDto.thumbnailUrl720
            in 721..1080 -> videoDto.thumbnailUrl1080
            else -> videoDto.thumbnailUrl
        }
}