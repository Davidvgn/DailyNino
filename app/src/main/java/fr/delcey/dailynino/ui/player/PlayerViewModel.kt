package fr.delcey.dailynino.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        const val ARGS_STREAM_URL = "ARGS_STREAM_URL"
    }

    private val playerStateMutableStateFlow: MutableStateFlow<PlayerState> = MutableStateFlow(getInitialState())

    val viewStateLiveData: LiveData<PlayerViewState> = liveData {
        playerStateMutableStateFlow.collect { playerState ->
            emit(
                PlayerViewState(
                    streamUrl = playerState.streamUrl,
                    playbackPosition = playerState.playbackPosition,
                    mediaItemIndex = playerState.mediaItemIndex,
                    playWhenReady = playerState.playWhenReady,
                )
            )
        }
    }

    fun onReleasingPlayer(
        playbackPosition: Long,
        mediaItemIndex: Int,
        playWhenReady: Boolean,
    ) {
        playerStateMutableStateFlow.update {
            it.copy(
                playbackPosition = playbackPosition,
                mediaItemIndex = mediaItemIndex,
                playWhenReady = playWhenReady,
            )
        }
    }

    private fun getInitialState() = PlayerState(
        streamUrl = requireNotNull(savedStateHandle[ARGS_STREAM_URL]) { "Use PlayerActivity.navigate()" },
        playbackPosition = 0,
        mediaItemIndex = 0,
        playWhenReady = true,
    )

    private data class PlayerState(
        val streamUrl: String,
        val playbackPosition: Long,
        val mediaItemIndex: Int,
        val playWhenReady: Boolean,
    )
}
