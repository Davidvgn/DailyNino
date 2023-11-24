package fr.delcey.dailynino.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.delcey.dailynino.data.video.VideoRepositoryRetrofit
import fr.delcey.dailynino.domain.video.VideoRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindingModule {

    @Singleton
    @Binds
    abstract fun bindVideoRepository(impl: VideoRepositoryRetrofit): VideoRepository
}