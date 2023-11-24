package fr.delcey.dailynino.ui.home

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import fr.delcey.dailynino.databinding.HomeActivityBinding
import fr.delcey.dailynino.ui.utils.viewBinding

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val binding by viewBinding { HomeActivityBinding.inflate(it) }
    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        val adapter = HomeAdapter()
        binding.homeRecyclerView.adapter = adapter

        // Remove blinking of existing items when list is appended with new items
        binding.homeRecyclerView.itemAnimator = null
        // Optimize RecyclerView layout phases since its size is not dependant on items
        binding.homeRecyclerView.setHasFixedSize(true)

        viewModel.viewStateLiveData.observe(this) {
            adapter.submitList(it)
        }
    }
}