package fr.delcey.dailynino.data.dailymotion.model

import com.google.gson.annotations.SerializedName

data class VideosDto(

    @field:SerializedName("explicit")
    val explicit: Boolean?,

    @field:SerializedName("total")
    val total: Int?,

    @field:SerializedName("limit")
    val limit: Int?,

    @field:SerializedName("page")
    val page: Int?,

    @field:SerializedName("has_more")
    val hasMore: Boolean?,

    @field:SerializedName("list")
    val videos: List<VideoDto>?,
)