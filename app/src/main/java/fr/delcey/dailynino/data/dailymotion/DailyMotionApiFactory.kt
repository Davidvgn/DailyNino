package fr.delcey.dailynino.data.dailymotion

import android.app.Application
import android.util.Log
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.io.File

object DailyMotionApiFactory {
    private const val CACHE_SIZE = (5 * 1024 * 1024).toLong() // 5 Mb
    private const val CACHE_DIRECTORY_NAME = "dailymotion_videos_cache"

    fun createApi(cache: Cache): DailyMotionApi = Retrofit.Builder()
        .baseUrl("https://api.dailymotion.com")
        .client(
            OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    }
                )
                .cache(cache)
                .build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create()

    fun createCache(application: Application) = Cache(
        File(application.cacheDir, CACHE_DIRECTORY_NAME).apply {
            if (!exists()) {
                if (mkdir()) {
                    Log.e("DailyMotionApiFactory", "couldn't create '$CACHE_DIRECTORY_NAME' folder, API calls won't be cached.")
                }
            }
        },
        CACHE_SIZE
    )
}
