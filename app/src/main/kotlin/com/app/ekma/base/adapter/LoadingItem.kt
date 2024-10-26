package com.app.ekma.base.adapter

import com.app.ekma.databinding.ItemLoadingBinding

class LoadingItem : ItemUI<ItemLoadingBinding>(ItemLoadingBinding::inflate) {
    override fun onBindViewHolder(binding: ItemLoadingBinding, position: Int) {
    }

    override fun itemType() = ITEM_LOADING
}