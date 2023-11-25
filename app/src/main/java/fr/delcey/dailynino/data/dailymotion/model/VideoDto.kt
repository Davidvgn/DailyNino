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

    @field:SerializedName("thumbnail_60_url")
    val thumbnailUrl60: String?,

    @field:SerializedName("thumbnail_62_url")
    val thumbnailUrl62: String?,

    @field:SerializedName("thumbnail_120_url")
    val thumbnailUrl120: String?,

    @field:SerializedName("thumbnail_180_url")
    val thumbnailUrl180: String?,

    @field:SerializedName("thumbnail_240_url")
    val thumbnailUrl240: String?,

    @field:SerializedName("thumbnail_360_url")
    val thumbnailUrl360: String?,

    @field:SerializedName("thumbnail_480_url")
    val thumbnailUrl480: String?,

    @field:SerializedName("thumbnail_720_url")
    val thumbnailUrl720: String?,

    @field:SerializedName("thumbnail_1080_url")
    val thumbnailUrl1080: String?,

    @field:SerializedName("thumbnail_url")
    val thumbnailUrl: String?,

    @field:SerializedName("title")
    val title: String?,
)