package fr.delcey.dailynino.data.dailymotion.model

import com.google.gson.annotations.SerializedName

data class VideoDto(

    @field:SerializedName("duration")
    val duration: Int?,

    @field:SerializedName("created_time")
    val createdTimeEpochSeconds: Int?,

    @field:SerializedName("description")
    val description: String?,

    @field:SerializedName("id")
    val id: String?,

    @field:SerializedName("thumbnail_url")
    val thumbnailUrl: String?,

    @field:SerializedName("title")
    val title: String?,
)