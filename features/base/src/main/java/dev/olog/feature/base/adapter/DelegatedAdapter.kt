package dev.olog.feature.base.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class DelegatedAdapter<Adapter : RecyclerView.Adapter<VH>, VH: RecyclerView.ViewHolder>(
    override val delegate: Adapter
) : RecyclerView.Adapter<VH>(),
    DelegateAdapter<VH> {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return delegate.onCreateViewHolder(parent, viewType)
    }

    override fun getItemCount(): Int {
        return delegate.itemCount
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        delegate.onBindViewHolder(holder, position)
    }

    override fun onBindViewHolder(
        holder: VH,
        position: Int,
        payloads: MutableList<Any>
    ) {
        delegate.onBindViewHolder(holder, position, payloads)
    }

    override fun findRelativeAdapterPositionIn(
        adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
        viewHolder: RecyclerView.ViewHolder,
        localPosition: Int
    ): Int {
        return delegate.findRelativeAdapterPositionIn(adapter, viewHolder, localPosition)
    }

    override fun getItemViewType(position: Int): Int {
        return delegate.getItemViewType(position)
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        delegate.setHasStableIds(hasStableIds)
    }

    override fun getItemId(position: Int): Long {
        return delegate.getItemId(position)
    }

    override fun onViewRecycled(holder: VH) {
        delegate.onViewRecycled(holder)
    }

    override fun onFailedToRecycleView(holder: VH): Boolean {
        return delegate.onFailedToRecycleView(holder)
    }

    override fun onViewAttachedToWindow(holder: VH) {
        delegate.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: VH) {
        delegate.onViewDetachedFromWindow(holder)
    }

    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        delegate.registerAdapterDataObserver(observer)
    }

    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        delegate.unregisterAdapterDataObserver(observer)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        delegate.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        delegate.onDetachedFromRecyclerView(recyclerView)
    }

    override fun setStateRestorationPolicy(strategy: StateRestorationPolicy) {
        delegate.stateRestorationPolicy = strategy
    }

}