package com.app.ekma.base.listeners

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationScrollListener(private val linearLayoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition()
        val visibleItemCount = linearLayoutManager.childCount
        val totalItemCount = linearLayoutManager.itemCount

        if (isLoading() or isLastPage()) return

        if ((lastVisibleItemPosition < totalItemCount - 1) and (lastVisibleItemPosition - visibleItemCount <= 0)) {
            loadMore()
        }
    }

    abstract fun loadMore()
    abstract fun isLoading(): Boolean
    abstract fun isLastPage(): Boolean
}