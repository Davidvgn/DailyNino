package fr.delcey.dailynino.data

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.delcey.dailynino.data.dailymotion.DailyMotionApi
import fr.delcey.dailynino.data.dailymotion.DailyMotionApiFactory
import okhttp3.Cache
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Singleton
    @Provides
    fun provideDailyMotionApi(@DailyMotionRetrofitCache cache: Cache): DailyMotionApi = DailyMotionApiFactory.createApi(cache)

    @Singleton
    @Provides
    @DailyMotionRetrofitCache
    fun provideDailyMotionRetrofitCache(application: Application): Cache = DailyMotionApiFactory.createCache(application)
}

@Qualifier
annotation class DailyMotionRetrofitCache