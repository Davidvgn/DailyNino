package fr.delcey.dailynino.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.delcey.dailynino.data.dailymotion.DailyMotionApi
import fr.delcey.dailynino.data.dailymotion.DailyMotionApiFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Singleton
    @Provides
    fun provideDailyMotionApi(): DailyMotionApi = DailyMotionApiFactory.create()
}