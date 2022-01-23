package dev.olog.feature.base.drag

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.base.adapter.DelegatedAdapter
import dev.olog.feature.base.adapter.Submittable
import dev.olog.feature.base.R
import dev.olog.feature.base.adapter.DelegateAdapter

class DraggableAdapter<Adapter, VH : RecyclerView.ViewHolder, T : Any>(
    delegate: Adapter,
    private val dragListener: IDragListener,
    private val canInteractWithViewHolder: (RecyclerView.ViewHolder) -> Boolean,
    private val onMoved: (RecyclerView.ViewHolder, Int, Int) -> Unit,
    private val onSwipedLeft: (RecyclerView.ViewHolder, T) -> Unit,
    private val afterSwipeLeft: (RecyclerView.ViewHolder) -> Unit,
    private val onSwipedRight: (RecyclerView.ViewHolder, T) -> Unit,
    private val afterSwipeRight: (RecyclerView.ViewHolder) -> Unit,
    private val onClearView: (RecyclerView.ViewHolder) -> Unit,
) : DelegatedAdapter<Adapter, VH>(delegate),
    Submittable<T>,
    TouchableAdapter,
    DelegateAdapter<VH>
    where Adapter : ListAdapter<T, VH> {

    override fun submitList(list: List<T>?) = submitList(list, null)
    override fun submitList(list: List<T>?, commitCallback: Runnable?) {
        delegate.submitList(list, commitCallback)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val vh = super.onCreateViewHolder(parent, viewType)

        vh.itemView.findViewById<View>(R.id.dragHandle)?.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    dragListener.onStartDrag(vh)
                    true
                }
                else -> false
            }
        }

        return vh
    }

    // very important to override this method on order for [bindingAdapterPosition] to return the correct position
    override fun findRelativeAdapterPositionIn(
        adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
        viewHolder: RecyclerView.ViewHolder,
        localPosition: Int
    ): Int {
        return delegate.findRelativeAdapterPositionIn(delegate, viewHolder, localPosition)
    }

    override fun canInteractWithViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return canInteractWithViewHolder.invoke(viewHolder)
    }

    override fun onMoved(viewHolder: RecyclerView.ViewHolder, from: Int, to: Int) {
        this.onMoved.invoke(viewHolder, from, to)
    }

    override fun onSwipedLeft(viewHolder: RecyclerView.ViewHolder) {
        onSwipedLeft.invoke(viewHolder, delegate.currentList[viewHolder.bindingAdapterPosition])
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        afterSwipeLeft.invoke(viewHolder)
    }

    override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {
        onSwipedRight.invoke(viewHolder, delegate.currentList[viewHolder.bindingAdapterPosition])
    }

    override fun afterSwipeRight(viewHolder: RecyclerView.ViewHolder) {
        afterSwipeRight.invoke(viewHolder)
    }

    override fun onClearView(viewHolder: RecyclerView.ViewHolder) {
        onClearView.invoke(viewHolder)
    }
}

fun <Adapter, VH : RecyclerView.ViewHolder,T : Any> Adapter.draggable(
    listener: IDragListener,
    canInteractWithViewHolder: (RecyclerView.ViewHolder) -> Boolean = { true },
    onMoved: (RecyclerView.ViewHolder, Int, Int) -> Unit = { _, _, _ -> },
    onSwipedLeft: (RecyclerView.ViewHolder, T) -> Unit = { _, _ -> },
    afterSwipeLeft: (RecyclerView.ViewHolder) -> Unit = { _, -> },
    onSwipedRight: (RecyclerView.ViewHolder, T) -> Unit = { _, _ -> },
    afterSwipeRight: (RecyclerView.ViewHolder) -> Unit = { _, -> },
    onClearView: (RecyclerView.ViewHolder) -> Unit = { _, -> },
): DraggableAdapter<Adapter, VH, T>
    where Adapter : ListAdapter<T, VH>,
          Adapter : Submittable<T> {
    return DraggableAdapter(
        delegate = this,
        dragListener = listener,
        canInteractWithViewHolder = canInteractWithViewHolder,
        onMoved = onMoved,
        onSwipedLeft = onSwipedLeft,
        afterSwipeLeft = afterSwipeLeft,
        onSwipedRight = onSwipedRight,
        afterSwipeRight = afterSwipeRight,
        onClearView = onClearView
    )
}