package fr.delcey.dailynino.domain.video.model

import java.time.ZonedDateTime
import kotlin.time.Duration

data class VideoEntity(
    val id: String,
    val title: String,
    val description: String,
    val duration: Duration?,
    val thumbnailUrl: String,
    val createdAt: ZonedDateTime,
)
