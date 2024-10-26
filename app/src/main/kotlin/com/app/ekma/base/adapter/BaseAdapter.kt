package com.app.ekma.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.app.ekma.common.chunkList

@Suppress("UNCHECKED_CAST")
abstract class BaseAdapter<T, B : ViewBinding>(
    private val binding: (LayoutInflater, ViewGroup, Boolean) -> B
) : BaseAdapterMultiHolder<ItemUI<*>>() {
//    private var adsData: ADSItemData? = null

//    fun setupADS(
//        builder: ADSItemBuilder.() -> Unit
//    ): BaseAdapter<T, B> {
//        adsData = ADSItemBuilder().apply(builder).build()
//        return this
//    }

    fun setData(newData: List<T>, withDiffUtil: Boolean = false, loadCompleted: () -> Unit = {}) {
        if (newData.isEmpty()) return
        super.setData(mapAdsOrNot(newData), withDiffUtil, loadCompleted)
    }

    fun addData(data: List<T>, withDiffUtil: Boolean = false) {
        super.addData(mapAdsOrNot(data), withDiffUtil)
    }

    private fun mapAdsOrNot(items: List<T>): List<ItemUI<*>> {
        val newItems = mutableListOf<ItemUI<*>>()
        return /*adsData?.let { adsItem ->
            items.chunkList(adsItem.rule).mapIndexed { index, ts ->
                newItems.addAll(ts.map { BaseItemHolder(it) })
                if (index == 0) {
                    newItems.add(ADSItem(adsItem))
                } else if (adsItem.isAutoAdd) {
                    newItems.add(ADSItem(adsItem))
                } else {
                }
            }
            newItems
        } ?: */items.map { BaseItemHolder(it) }
    }

    inner class BaseItemHolder(
        val item: T
    ) : ItemUI<B>(binding) {
        override fun onBindViewHolder(binding: B, position: Int) {
            onBindViewHolder(binding, item, position)
        }

        override fun onBindPayloadViewHolder(
            binding: B,
            position: Int,
            payloads: MutableList<Any>
        ) {
            onBindPayloadViewHolder(binding, item, position, payloads)
        }

        override fun doOnUpdatePremiumState(binding: B, isPurchased: Boolean) {
            super.doOnUpdatePremiumState(binding, isPurchased)
            onPurchaseChangedState(binding, item, isPurchased)
        }

        override fun instance(): String {
            return getItemInstance(item)
        }
    }

    abstract fun onBindViewHolder(binding: B, item: T, position: Int)
    open fun onBindPayloadViewHolder(
        binding: B,
        item: T,
        position: Int,
        payloads: MutableList<Any>
    ) {
    }

    open fun onPurchaseChangedState(binding: B, item: T, isPurchased: Boolean) {}

    fun getItems() = data.mapNotNull {
        (it as? BaseAdapter<T, B>.BaseItemHolder)?.item
    }

    fun getRealItems() = data

    fun ItemUI<*>.asItem() = (this as? BaseAdapter<T, B>.BaseItemHolder)?.item

    open fun getItemInstance(item: T): String = this::class.java.name.hashCode().toString()
}