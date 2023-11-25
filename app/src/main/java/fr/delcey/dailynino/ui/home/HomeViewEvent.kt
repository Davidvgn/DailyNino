package fr.delcey.dailynino.ui.home

sealed class HomeViewEvent {
    data class PlayVideo(
        val streamUrl: String,
    ) : HomeViewEvent()
}
