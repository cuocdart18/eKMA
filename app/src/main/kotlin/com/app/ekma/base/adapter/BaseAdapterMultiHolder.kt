package com.app.ekma.base.adapter

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ekma.base.fragment.FragmentLifeCycleObservable
import com.app.ekma.base.fragment.FragmentLifecycleObserver
import com.app.ekma.common.createThemeInflater
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import kotlin.reflect.KMutableProperty1

const val ITEM_ADS = -1
const val ITEM_LOADING = -2
const val ITEM_NORMAL = -3
private const val PAYLOAD_VIEW_STATE_RESUME = "PAYLOAD_VIEW_STATE_RESUME"
private const val PAYLOAD_VIEW_STATE_PAUSE = "PAYLOAD_VIEW_STATE_PAUSE"
private const val PAYLOAD_VIEW_STATE_DESTROY = "PAYLOAD_VIEW_STATE_DESTROY"
private const val PAYLOAD_UPDATE_PURCHASED = "PAYLOAD_UPDATE_PURCHASED"

@Suppress("UNCHECKED_CAST")
abstract class BaseAdapterMultiHolder<T : ItemUI<*>> :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), FragmentLifecycleObserver {
    protected val handler = Handler(Looper.getMainLooper())
    protected var isUserPremium = false
    val data = mutableListOf<T>()
    private val mTypeInstances = mutableMapOf<Int, T>()
    private val job = CoroutineScope(Dispatchers.Main) + Job()

    fun setLifecycleOwner(owner: FragmentLifeCycleObservable) {
        owner.addObserver(this)
    }

    fun clear() {
        data.clear()
        handler.post(::notifyDataSetChanged)
    }

    fun attachRecycler(recycler: RecyclerView) {
        recycler.layoutManager?.let { layoutManager ->
            runCatching {
                if (layoutManager is GridLayoutManager) {
                    layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return data[position].spanSize()
                        }
                    }
                }
            }
        }
    }

    fun setupLoadingItem(layoutManager: RecyclerView.LayoutManager?) {
        runCatching {
            (layoutManager as? GridLayoutManager)?.let {
                it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (data[position].isLoadingItem()) {
                            it.spanCount
                        } else 1
                    }
                }
            }
        }
    }

    fun removeItem(item: T) = runCatching {
        val position = data.indexOf(item)
        if (position != -1) {
            data.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun removeLoading(): Boolean {
        return if (data.isNotEmpty() && data.last().isLoadingItem()) {
            val last = this.data.lastIndex
            this.data.removeAt(last)
            notifyItemRemoved(last)
            true
        } else false
    }

    fun addLoading(doWork: () -> Unit = {}) {
        runCatching {
            if (data.isEmpty()) return
            removeLoading()
            data.add(LoadingItem() as T)
            notifyItemInserted(data.lastIndex)

            doWork.invoke()
        }
    }

    fun lastPosition() = data.lastIndex

    fun lastItem() = data.last()

    fun <T> getDataItems() = data.mapNotNull { it as? T }

    inline fun <reified I : ItemUI<*>> getRealData(): List<I> {
        val items = mutableListOf<I>()
        data.filter { it.isNormalItem() }.map {
            if (it is I) {
                items.add(it as I)
            }
        }

        return items
    }

    fun setData(vararg newData: T, withDiffUtil: Boolean = false, loadCompleted: () -> Unit = {}) {
        setData(newData.toList(), withDiffUtil, loadCompleted)
    }

    open fun setData(
        newData: List<T>,
        withDiffUtil: Boolean = false,
        loadCompleted: () -> Unit = {}
    ) = job.launch {
        runCatching {
            if (newData.isEmpty()) return@launch
            if (withDiffUtil) {
                val diffCallback = AppDiffUtil(data, newData)
                val diffResult = DiffUtil.calculateDiff(diffCallback)
                data.clear()
                data.addAll(newData)
                updatePremiumState()

                diffResult.dispatchUpdatesTo(this@BaseAdapterMultiHolder)
                loadCompleted.invoke()
            } else {
                data.clear()
                data.addAll(newData)

                updatePremiumState()

                withContext(Dispatchers.Main) {
                    notifyData(loadCompleted)
                }
            }
        }
    }

    private fun updatePremiumState() {
        data.map {
            it.setUserPremium(isUserPremium)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyData(loadCompleted: () -> Unit = {}) {
        runCatching {
            notifyDataSetChanged()
            loadCompleted.invoke()
        }
    }

    open fun addData(newData: List<T>, withDiffUtil: Boolean = false) = job.launch {
        runCatching {
            removeLoading()
            if (newData.isEmpty()) return@runCatching
            if (withDiffUtil) {
                val diffCallback = AppDiffUtil(data, newData)
                val diffResult = DiffUtil.calculateDiff(diffCallback)
                data.addAll(newData)
                updatePremiumState()

                diffResult.dispatchUpdatesTo(this@BaseAdapterMultiHolder)
            } else {
                data.addAll(newData)
                updatePremiumState()
                handler.post(::notifyDataSetChanged)
            }
        }
    }

    open fun addData(newData: T, withDiffUtil: Boolean = false) = job.launch {
        runCatching {
            removeLoading()
            if (withDiffUtil) {
                val diffCallback = AppDiffUtil(data, listOf(newData))
                val diffResult = DiffUtil.calculateDiff(diffCallback)
                data.add(newData)
                updatePremiumState()

                diffResult.dispatchUpdatesTo(this@BaseAdapterMultiHolder)
            } else {
                data.add(newData)
                updatePremiumState()
                handler.post(::notifyDataSetChanged)
            }
        }
    }

    private suspend fun runMainThread(doWork: () -> Unit) = withContext(Dispatchers.Main) {
        doWork.invoke()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val item = getViewTypeInstance(viewType)
        val inflater = createThemeInflater(
            parent.context
        )
        if (item == null) {
            throw IllegalStateException(
                String.format(
                    "ViewType instance not found for viewType %s. You should implement the AutoMap properly.",
                    viewType
                )
            )
        }
        return item.createViewHolder(inflater, parent, viewType)
    }

    private fun getViewTypeInstance(viewType: Int): T? {
        return mTypeInstances[viewType]
    }

    protected open fun getMaxItemCount() = -1

    override fun getItemCount(): Int = if (getMaxItemCount() == -1) data.size else {
        if (data.size >= getMaxItemCount()) getMaxItemCount() else data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        data[position].onBindItemView(holder, position)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            when (payloads[0]) {
                PAYLOAD_VIEW_STATE_RESUME -> data[position].onViewStateResume(holder)
                PAYLOAD_VIEW_STATE_PAUSE -> data[position].onViewStatePause(holder)
                PAYLOAD_VIEW_STATE_DESTROY -> data[position].onViewStateDestroy(holder)
                PAYLOAD_UPDATE_PURCHASED -> data[position].onBindViewWithPurchaseStateChange(holder)
                else -> data[position].onBindPayloadItemView(holder, position, payloads)
            }
        } else super.onBindViewHolder(holder, position, payloads)
    }

    private fun updatePaddingBottom(view: View, pd: Int) {
        view.apply {
            updateLayoutParams<RecyclerView.LayoutParams> {
                bottomMargin = pd
            }
        }
    }

    fun onAddPadding() {
        notifyItemChanged(data.lastIndex)
    }

    protected fun isLast(position: Int) = position == data.size - 1

//    fun onResumeAdapterAds() = filterAds().map {
//        it.resume()
//    }
//
//    fun onPauseAdapterAds() = filterAds().map {
//        it.pause()
//    }
//
//    fun onDestroyAdapterAds() = filterAds().map {
//    }

    @CallSuper
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        runCatching {
            data[holder.absoluteAdapterPosition].setItemVisible(true)
            data[holder.absoluteAdapterPosition].onViewAttached(holder)
        }
        super.onViewAttachedToWindow(holder)
    }

    @CallSuper
    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        runCatching {
            data[holder.absoluteAdapterPosition].setItemVisible(false)
            data[holder.absoluteAdapterPosition].onViewDetached(holder)
        }
        super.onViewDetachedFromWindow(holder)
    }

    @CallSuper
    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        runCatching {
            data[holder.absoluteAdapterPosition].onViewRecycled(holder)
        }
        super.onViewRecycled(holder)
    }

    fun getItem(position: Int): T? {
        return data.getOrNull(position)
    }

//    private fun filterAds(): List<ADSItem> = data.mapNotNull {
//        if (it.isAdsItem() && it is ADSItem) {
//            it
//        } else null
//    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position) ?: return 0
        mapViewTypeFrom(item)
        return item.getItemViewType()
    }

    private fun mapViewTypeFrom(item: T) {
        if (!mTypeInstances.containsKey(item.getItemViewType())) {
            mTypeInstances[item.getItemViewType()] = item
        }
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        notifyPayloadAll(PAYLOAD_VIEW_STATE_RESUME)
    }

    override fun onFragmentPause() {
        super.onFragmentPause()
        notifyPayloadAll(PAYLOAD_VIEW_STATE_PAUSE)
    }

    override fun onFragmentDestroy() {
        super.onFragmentDestroy()
        notifyPayloadAll(PAYLOAD_VIEW_STATE_DESTROY)
    }

    fun notifyPayloadAll(payload: Any) {
        List(data.size) { index ->
            notifyPayloadItem(index, payload)
        }
    }

    fun notifyPayloadItem(position: Int, payload: Any) {
        if (position >= 0 && position < data.size) {
            notifyItemChanged(position, payload)
        }
    }

    fun <V> callOnUpdateStates(value: V) = runCatching {
        data.map {
            it.updateState(value)
        }
    }

    fun <F, V> callUpdateItems(item: KMutableProperty1<F, V>, newData: V) = runCatching {
        this.data.mapIndexed { index, t ->
            t.updateData(item, newData)

            notifyItemChanged(index)
        }
    }

    fun onUpdatePremiumState() = job.launch {
        runCatching {
            val isUserPremium = isPurchased()

            if (this@BaseAdapterMultiHolder.isUserPremium != isUserPremium) {
                this@BaseAdapterMultiHolder.isUserPremium = isUserPremium
                data.mapIndexed { i, t ->
                    t.setUserPremium(isUserPremium)
                    notifyItemChanged(i, PAYLOAD_UPDATE_PURCHASED)
                }
            }
        }
    }

    private suspend fun isPurchased(): Boolean {
        val resultFuture = CompletableDeferred<Boolean>()
        resultFuture.complete(true)
//            resultFuture.complete(false)
        return resultFuture.await()
    }
}