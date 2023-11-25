package fr.delcey.dailynino.ui.home

import fr.delcey.dailynino.ui.utils.NativeText

sealed class HomeViewState(val type: HomeViewStateType) {

    enum class HomeViewStateType {
        VIDEO,
        ERROR,
        LOADING_FOOTER,
    }

    data class Video(
        val id: String,
        val title: NativeText,
        val description: NativeText,
        val duration: NativeText?,
        val thumbnailUrl: String,
        val createdAt: NativeText,
    ) : HomeViewState(HomeViewStateType.VIDEO)

    data object Error : HomeViewState(HomeViewStateType.ERROR)

    data object LoadingFooter : HomeViewState(HomeViewStateType.LOADING_FOOTER)
}
