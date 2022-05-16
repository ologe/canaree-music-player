package dev.olog.platform.adapter.drag

import android.graphics.Canvas
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dev.olog.platform.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TouchHelperAdapterCallback(
    private val coroutineScope: CoroutineScope,
    private val adapter: TouchableAdapter,
    horizontalDirections: Int,
    private val animation: TouchHelperAdapterAnimation,
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    horizontalDirections
) {

    companion object {
        private const val SWIPE_DURATION = DEFAULT_SWIPE_ANIMATION_DURATION.toLong() - 50
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (adapter.canInteractWithViewHolder(viewHolder) &&
            adapter.canInteractWithViewHolder(target)
        ) {
            adapter.onMoved(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }
        return false
    }

    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (adapter.canInteractWithViewHolder(viewHolder)) {
            return super.getSwipeDirs(recyclerView, viewHolder)
        }
        return 0
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (adapter.canInteractWithViewHolder(viewHolder)) {
            when (direction) {
                ItemTouchHelper.RIGHT -> {
                    coroutineScope.launch {
                        adapter.onSwipedRight(viewHolder)
                        delay(SWIPE_DURATION)
                        adapter.afterSwipeRight(viewHolder)
                    }
                }
                ItemTouchHelper.LEFT -> {
                    coroutineScope.launch {
                        adapter.onSwipedLeft(viewHolder)
                        delay(SWIPE_DURATION)
                        adapter.afterSwipeLeft(viewHolder)
                    }
                }
            }
        }
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        when (actionState) {
            ItemTouchHelper.ACTION_STATE_SWIPE -> {
                animation.onSwipe(viewHolder, dX, dY)

                getDefaultUIUtil().onDraw(
                    canvas,
                    recyclerView,
                    adapter.contentViewFor(viewHolder),
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
            ItemTouchHelper.ACTION_STATE_DRAG -> drawOnMove(
                recyclerView,
                viewHolder.itemView,
                isCurrentlyActive,
                dY
            )
        }
    }

    private fun drawOnMove(
        recyclerView: RecyclerView,
        view: View,
        isCurrentlyActive: Boolean,
        dY: Float
    ) {
        if (isCurrentlyActive) {
            var originalElevation: Any? = view.getTag(androidx.recyclerview.R.id.item_touch_helper_previous_elevation)
            if (originalElevation == null) {
                originalElevation = ViewCompat.getElevation(view)
                val newElevation = 5f + findMaxElevation(recyclerView, view)
                ViewCompat.setElevation(view, newElevation)
                view.setTag(androidx.recyclerview.R.id.item_touch_helper_previous_elevation, originalElevation)
            }
        }
        view.translationY = dY
    }

    private fun findMaxElevation(recyclerView: RecyclerView, itemView: View): Float {
        val childCount = recyclerView.childCount
        var max = 0f
        for (i in 0 until childCount) {
            val child = recyclerView.getChildAt(i)
            if (child === itemView) {
                continue
            }
            val elevation = ViewCompat.getElevation(child)
            if (elevation > max) {
                max = elevation
            }
        }
        return max
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        getDefaultUIUtil().clearView(viewHolder.itemView.findViewById(R.id.content))
        getDefaultUIUtil().clearView(viewHolder.itemView)
        adapter.onClearView()
    }


    override fun isLongPressDragEnabled(): Boolean = false

}