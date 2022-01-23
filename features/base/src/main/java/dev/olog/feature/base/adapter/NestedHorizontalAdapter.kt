package dev.olog.feature.base.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.base.R
import dev.olog.shared.android.extensions.inflate

class NestedHorizontalAdapter<Adapter, T : Any>(
    override val delegate: Adapter,
    private val layoutManagerFactory: (Context) -> RecyclerView.LayoutManager,
) : OptionalAdapter<RecyclerView.ViewHolder>(),
    Submittable<T>,
    DelegateAdapter<RecyclerView.ViewHolder>
    where Adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>,
          Adapter : Submittable<T> {

    var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_horizontal_list)
        val list = view.findViewById<RecyclerView>(R.id.list)
        list.adapter = delegate
        list.layoutManager = layoutManagerFactory(view.context).also { layoutManager = it }
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    override fun submitList(list: List<T>?) = submitList(list, null)

    override fun submitList(list: List<T>?, commitCallback: Runnable?) {
        delegate.submitList(list) {
            commitCallback?.run()
            show = list?.isNotEmpty() == true
        }
    }

}

fun <Adapter, T : Any> Adapter.horizontal(
    layoutManagerFactory: (Context) -> RecyclerView.LayoutManager = { context ->
        LinearLayoutManager(context, HORIZONTAL, false)
    },
): NestedHorizontalAdapter<Adapter, T>
    where Adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>,
          Adapter : Submittable<T> {
    return NestedHorizontalAdapter(
        delegate = this,
        layoutManagerFactory = layoutManagerFactory,
    )
}