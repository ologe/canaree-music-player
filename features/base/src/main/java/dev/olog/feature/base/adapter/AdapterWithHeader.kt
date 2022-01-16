package dev.olog.feature.base.adapter

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class AdapterWithHeader<Adapter, VH : RecyclerView.ViewHolder, T : Any>(
    private val headerAdapter: OptionalAdapter<RecyclerView.ViewHolder>,
    private val adapter: Adapter,
    config: ConcatAdapter.Config = ConcatAdapter.Config.DEFAULT,
) : CustomConcatAdapter(
    config,
    headerAdapter,
    adapter
), Submittable<T>
    where Adapter : RecyclerView.Adapter<VH>,
          Adapter : Submittable<T> {

    override fun submitList(list: List<T>?) = submitList(list, null)

    override fun submitList(list: List<T>?, commitCallback: Runnable?) {
        adapter.submitList(list) {
            commitCallback?.run()
            headerAdapter.show = list?.isNotEmpty() == true
        }
    }
}