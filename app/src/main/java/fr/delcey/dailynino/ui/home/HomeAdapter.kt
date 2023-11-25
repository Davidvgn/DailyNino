package fr.delcey.dailynino.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.delcey.dailynino.databinding.HomeErrorItemBinding
import fr.delcey.dailynino.databinding.HomeLoadingFooterItemBinding
import fr.delcey.dailynino.databinding.HomeVideoItemBinding
import fr.delcey.dailynino.ui.utils.setText
import fr.delcey.dailynino.ui.utils.setTextOrHide

class HomeAdapter : ListAdapter<HomeViewState, RecyclerView.ViewHolder>(HomeDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (HomeViewState.HomeViewStateType.values()[viewType]) {
            HomeViewState.HomeViewStateType.VIDEO -> HomeVideoViewHolder(
                HomeVideoItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            HomeViewState.HomeViewStateType.ERROR -> object : RecyclerView.ViewHolder(
                HomeErrorItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).root
            ) {}
            HomeViewState.HomeViewStateType.LOADING_FOOTER -> object : RecyclerView.ViewHolder(
                HomeLoadingFooterItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).root
            ) {}
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is HomeViewState.Video -> (holder as HomeVideoViewHolder).bind(item)
            // No binding necessary
            is HomeViewState.Error,
            is HomeViewState.LoadingFooter -> Unit
        }
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal

    class HomeVideoViewHolder(private val binding: HomeVideoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeViewState.Video) {
            Glide.with(binding.homeItemImageViewThumbnail).load(item.thumbnailUrl).into(binding.homeItemImageViewThumbnail)
            binding.homeItemTextViewDuration.setText(item.duration)
            binding.homeItemTextViewTitle.setText(item.title)
            binding.homeItemTextViewDescription.setTextOrHide(item.description)
            binding.homeItemTextViewCreatedAt.setText(item.createdAt)
            binding.homeItemMaterialCardView.setOnClickListener {
                item.onClicked()
            }
        }
    }

    object HomeDiffCallback : DiffUtil.ItemCallback<HomeViewState>() {
        override fun areItemsTheSame(oldItem: HomeViewState, newItem: HomeViewState): Boolean =
            oldItem.type == newItem.type &&
                (oldItem.type == HomeViewState.HomeViewStateType.ERROR ||
                    oldItem.type == HomeViewState.HomeViewStateType.LOADING_FOOTER ||
                    (oldItem as HomeViewState.Video).id == (newItem as HomeViewState.Video).id)

        override fun areContentsTheSame(oldItem: HomeViewState, newItem: HomeViewState): Boolean =
            (oldItem as? HomeViewState.Video) == (newItem as? HomeViewState.Video)
    }
}