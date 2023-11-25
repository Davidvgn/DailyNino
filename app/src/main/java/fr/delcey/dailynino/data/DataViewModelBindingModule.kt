package fr.delcey.dailynino.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import fr.delcey.dailynino.data.paging_video.PagingVideoRepositoryInMemory
import fr.delcey.dailynino.domain.paging_video.PagingVideoRepository

@Module
@InstallIn(ViewModelComponent::class)
abstract class DataViewModelBindingModule {

    @ViewModelScoped
    @Binds
    abstract fun bindPagingVideoRepository(impl: PagingVideoRepositoryInMemory): PagingVideoRepository
}