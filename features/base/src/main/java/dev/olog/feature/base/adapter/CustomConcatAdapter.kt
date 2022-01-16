package dev.olog.feature.base.adapter

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class CustomConcatAdapter(
    delegate: ConcatAdapter
) : DelegatedAdapter<ConcatAdapter, RecyclerView.ViewHolder>(delegate),
    DelegateAdapter<RecyclerView.ViewHolder> {

    constructor(
        config: ConcatAdapter.Config,
        vararg adapters: RecyclerView.Adapter<*>
    ) : this(ConcatAdapter(config, *adapters))

    constructor(
        config: ConcatAdapter.Config,
        adapters: List<RecyclerView.Adapter<*>>
    ) : this(ConcatAdapter(config, *adapters.toTypedArray()))

}