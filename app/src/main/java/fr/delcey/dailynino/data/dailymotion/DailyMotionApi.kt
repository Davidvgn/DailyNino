package fr.delcey.dailynino.data.dailymotion

import fr.delcey.dailynino.data.dailymotion.model.PagedVideosDto
import retrofit2.http.GET
import retrofit2.http.Query

interface DailyMotionApi {
    @GET("videos")
    suspend fun getPagedVideos(
        @Query("page") page: Int,
        @Query("fields") queriedFields: String,
    ): PagedVideosDto
}
