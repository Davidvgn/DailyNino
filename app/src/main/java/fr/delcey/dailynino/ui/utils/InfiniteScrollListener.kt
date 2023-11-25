package fr.delcey.dailynino.ui.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InfiniteScrollListener(
    private val layoutManager: LinearLayoutManager,
    private val onReachingFooter: () -> Unit,
) : RecyclerView.OnScrollListener() {

    companion object {
        // The minimum number of items remaining before we should loading more.
        private const val VISIBLE_THRESHOLD = 3
    }

    private var yTotalScrolled = 0
    private var yMax = 0

    private var maxDispatchedEvenItemCount = -1

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        yTotalScrolled += dy

        if (dy < 0) {
            return
        }

        val totalItemCount = layoutManager.itemCount
        if (totalItemCount > maxDispatchedEvenItemCount && yTotalScrolled > yMax) {
            yMax = yTotalScrolled
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            if (lastVisibleItemPosition == RecyclerView.NO_POSITION || lastVisibleItemPosition >= totalItemCount - VISIBLE_THRESHOLD) {
                maxDispatchedEvenItemCount = totalItemCount
                onReachingFooter()
            }
        }
    }

    fun reset() {
        yMax = 0
        maxDispatchedEvenItemCount = -1
    }
}