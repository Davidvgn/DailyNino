package fr.delcey.dailynino.ui.player

data class PlayerViewState(
    val streamUrl: String,
    val playbackPosition: Long,
    val mediaItemIndex: Int,
    val playWhenReady: Boolean,
)
