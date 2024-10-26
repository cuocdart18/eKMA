package com.app.ekma.base.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KMutableProperty1

@Suppress("UNCHECKED_CAST")
abstract class ItemUI<B : ViewBinding>(
    private val _binding: (LayoutInflater, ViewGroup, Boolean) -> B
) : HolderFlexible {
    //    private var nativeAds: NativeAds<*>? = null
    private var isItemVisible = false

    private var _isUserPremium = false

    fun isUserPremium() = _isUserPremium

    fun setUserPremium(isUserPremium: Boolean) {
        this._isUserPremium = isUserPremium
    }

    override fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return ItemHolder(binding = _binding(inflater, parent, false))
    }

    abstract fun onBindViewHolder(binding: B, position: Int)
    protected open fun onBindPayloadViewHolder(
        binding: B,
        position: Int,
        payloads: MutableList<Any>
    ) {
    }

    fun onBindItemView(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("BaseAdapter", "onBindViewHolder (${position})")
        runCatching {
            (holder as? ItemUI<B>.ItemHolder)?.let {
                onBindViewHolder(it.binding, position)
                doOnUpdatePremiumState(it.binding, isUserPremium())
            }
        }.onFailure {
            Log.d("BaseAdapterError", "${it.message}")
        }
    }

    fun onBindPayloadItemView(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        runCatching {
            (holder as? ItemUI<B>.ItemHolder)?.let {
                onBindPayloadViewHolder(it.binding, position, payloads)
            }
        }
    }

    fun onBindViewWithPurchaseStateChange(holder: RecyclerView.ViewHolder) {
        runCatching {
            (holder as? ItemUI<B>.ItemHolder)?.let {
                doOnUpdatePremiumState(it.binding, isUserPremium())
            }
        }
    }

    protected fun FrameLayout.loadNative(act: FragmentActivity, id: String) {
        /*nativeAds = AdsUtils.loadNativeAds(act, this, id, object : LoadAdsCallback() {
            override fun onLoadSuccess() {
                super.onLoadSuccess()
                removeAllViews()

                nativeAds?.showAds(this@loadNative)
            }

            override fun onLoadFailed(message: String?) {
                super.onLoadFailed(message)
            }
        })*/
    }

    private inner class ItemHolder(val binding: B) : RecyclerView.ViewHolder(binding.root)

    fun isNormalItem() = itemType() == ITEM_NORMAL

    fun isAdsItem() = itemType() == ITEM_ADS

    fun isLoadingItem() = itemType() == ITEM_LOADING

    fun isItemVisible() = isItemVisible

    fun setItemVisible(isVisible: Boolean) {
        this.isItemVisible = isVisible
    }

    protected fun RecyclerView.ViewHolder.asBinding(): B? {
        return (this as? ItemUI<B>.ItemHolder)?.binding
    }

    open fun <V> updateState(value: V) {}

    open fun doOnUpdatePremiumState(binding: B, isPurchased: Boolean) {}

    open fun spanSize() = 1

    override fun getItemViewType(): Int {
        return this::class.java.name.hashCode()
    }

    open fun <T, V> updateData(item: KMutableProperty1<T, V>, just: V) {}

    open fun instance(): String = this::class.java.name.hashCode().toString()
    open fun instanceContent(): String = this::class.java.name.hashCode().toString()
}
