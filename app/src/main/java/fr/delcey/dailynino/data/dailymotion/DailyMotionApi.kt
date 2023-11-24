package fr.delcey.dailynino.data.dailymotion

import fr.delcey.dailynino.data.dailymotion.model.VideosDto
import retrofit2.http.GET
import retrofit2.http.Query

interface DailyMotionApi {
    @GET("videos")
    suspend fun getVideos(
        @Query("fields") queriedFields: String,
    ): VideosDto
}
