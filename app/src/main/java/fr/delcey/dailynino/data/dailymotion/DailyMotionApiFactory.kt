package fr.delcey.dailynino.data.dailymotion

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object DailyMotionApiFactory {
    fun create(): DailyMotionApi = Retrofit.Builder()
        .baseUrl("https://api.dailymotion.com")
        .client(
            OkHttpClient.Builder().apply {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    }
                )
            }.build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create()
}
