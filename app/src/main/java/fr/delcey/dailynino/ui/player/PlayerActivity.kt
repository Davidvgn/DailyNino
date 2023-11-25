package fr.delcey.dailynino.ui.player

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.AndroidEntryPoint
import fr.delcey.dailynino.databinding.PlayerActivityBinding
import fr.delcey.dailynino.ui.player.PlayerViewModel.Companion.ARGS_STREAM_URL
import fr.delcey.dailynino.ui.utils.viewBinding

@AndroidEntryPoint
class PlayerActivity : AppCompatActivity() {

    companion object {
        fun navigate(context: Context, streamUrl: String): Intent = Intent(context, PlayerActivity::class.java).apply {
            putExtra(ARGS_STREAM_URL, streamUrl)
        }
    }

    private val binding by viewBinding { PlayerActivityBinding.inflate(it) }
    private val viewModel by viewModels<PlayerViewModel>()

    private var _exoPlayer: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        viewModel.viewStateLiveData.observe(this) { viewState ->
            getExoPlayer().setMediaItems(
                listOf(MediaItem.fromUri(viewState.streamUrl)),
                viewState.mediaItemIndex,
                viewState.playbackPosition,
            )
            getExoPlayer().playWhenReady = viewState.playWhenReady
        }
    }

    override fun onStart() {
        super.onStart()

        _exoPlayer = ExoPlayer.Builder(this).build()
        binding.playerPlayerView.player = getExoPlayer()
        getExoPlayer().prepare()
    }

    override fun onResume() {
        super.onResume()

        hideSystemUi()
    }

    override fun onStop() {
        super.onStop()

        viewModel.onReleasingPlayer(
            playbackPosition = getExoPlayer().currentPosition,
            mediaItemIndex = getExoPlayer().currentMediaItemIndex,
            playWhenReady = getExoPlayer().playWhenReady,
        )
        getExoPlayer().release()
        binding.playerPlayerView.player = null
        _exoPlayer = null
    }

    private fun getExoPlayer(): ExoPlayer = requireNotNull(_exoPlayer) {
        "Don't try to get ExoPlayer reference before onStart or after onStop. Current lifecycle state: ${lifecycle.currentState}"
    }

    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, binding.playerPlayerView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}