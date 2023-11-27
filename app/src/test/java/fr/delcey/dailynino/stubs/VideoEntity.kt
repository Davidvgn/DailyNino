package fr.delcey.dailynino.stubs

import fr.delcey.dailynino.domain.video.model.VideoEntity
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun getDefaultVideoEntity(index: Int = 0) = VideoEntity(
    id = getDefaultVideoEntityId(index),
    title = getDefaultVideoEntityTitle(index),
    description = getDefaultVideoEntityDescription(index),
    duration = getDefaultVideoEntityDuration(index),
    thumbnailUrl = getDefaultVideoEntityThumbnailUrl(index),
    createdAt = getDefaultVideoEntityCreatedAt(index),
)

fun getDefaultVideoEntities(count: Int = 3, indexOffset: Int = 0): List<VideoEntity> = List(count) { index ->
    getDefaultVideoEntity(index + indexOffset)
}

fun getDefaultVideoEntityId(index: Int = 0): String = "DEFAULT_VIDEO_ENTITY_ID_$index"
fun getDefaultVideoEntityTitle(index: Int = 0): String = "DEFAULT_VIDEO_ENTITY_TITLE_$index"
fun getDefaultVideoEntityDescription(index: Int = 0): String = "DEFAULT_VIDEO_ENTITY_DESCRIPTION_$index"
fun getDefaultVideoEntityDuration(index: Int = 0): Duration = index.seconds
fun getDefaultVideoEntityThumbnailUrl(index: Int = 0): String = "DEFAULT_VIDEO_ENTITY_THUMBNAIL_URL_$index"
fun getDefaultVideoEntityCreatedAt(index: Int = 0): ZonedDateTime = ZonedDateTime.now(getDefaultClock()).minusSeconds(index.toLong())