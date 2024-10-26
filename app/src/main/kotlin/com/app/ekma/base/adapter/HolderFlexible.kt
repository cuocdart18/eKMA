package com.app.ekma.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface HolderFlexible {
    fun onViewAttached(holder: RecyclerView.ViewHolder) {}
    fun onViewDetached(holder: RecyclerView.ViewHolder) {}
    fun onViewRecycled(holder: RecyclerView.ViewHolder) {}
    fun onViewStatePause(holder: RecyclerView.ViewHolder) {}
    fun onViewStateResume(holder: RecyclerView.ViewHolder) {}
    fun onViewStateDestroy(holder: RecyclerView.ViewHolder) {}
    fun getItemViewType(): Int
    fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    fun itemType(): Int = ITEM_NORMAL
}