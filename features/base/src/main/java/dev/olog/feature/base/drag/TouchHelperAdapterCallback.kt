package dev.olog.feature.base.drag

import android.graphics.Canvas
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.base.R
import kotlinx.android.synthetic.main.dummy.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

class TouchHelperAdapterCallback(
    private val adapter: TouchableAdapter,
    horizontalDirections: Int

) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    horizontalDirections
) {

    companion object {
        @JvmStatic
        private val TAG = "P:${TouchHelperAdapterCallback::class.java.simpleName}"
        private const val SWIPE_DURATION = DEFAULT_SWIPE_ANIMATION_DURATION.toLong() - 50
    }

    private val scope: CoroutineScope = MainScope()
    private val animationsController = TouchHelperAnimationController()

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (isInteractable(viewHolder) && isInteractable(target)) {
            adapter.onMoved(viewHolder, viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
            return true
        }
        return false
    }

    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (isInteractable(viewHolder)) {
            return super.getSwipeDirs(recyclerView, viewHolder)
        }
        return 0
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (!isInteractable(viewHolder)) {
            return
        }

        when (direction) {
            ItemTouchHelper.RIGHT -> {
                scope.launch {
                    adapter.onSwipedRight(viewHolder)
                    delay(SWIPE_DURATION)
                    adapter.afterSwipeRight(viewHolder)
                }
            }
            ItemTouchHelper.LEFT -> {
                scope.launch {
                    adapter.onSwipedLeft(viewHolder)
                    delay(SWIPE_DURATION)
                    adapter.afterSwipeLeft(viewHolder)
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
        if (!isInteractable(viewHolder)) {
            return
        }

        when (actionState) {
            ItemTouchHelper.ACTION_STATE_SWIPE -> {
                val viewWidth = viewHolder.itemView.width
                if (abs(dX) > (viewWidth * 0.35f)) {
                    animationsController.drawCircularReveal(viewHolder, dX)
                } else if (abs(dX) < (viewWidth * 0.05f)) {
                    animationsController.setAnimationIdle()
                } else if (abs(dX) < (viewWidth * 0.35f)) {
                    animationsController.initializeSwipe(viewHolder, dX)
                }

                getDefaultUIUtil().onDraw(
                    canvas,
                    recyclerView,
                    viewHolder.itemView.content, // TODO test
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
            var originalElevation: Any? = view.getTag(com.google.android.material.R.id.item_touch_helper_previous_elevation)
            if (originalElevation == null) {
                originalElevation = ViewCompat.getElevation(view)
                val newElevation = 5f + findMaxElevation(recyclerView, view)
                ViewCompat.setElevation(view, newElevation)
                view.setTag(com.google.android.material.R.id.item_touch_helper_previous_elevation, originalElevation)
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
        if (!isInteractable(viewHolder)) {
            return
        }
        getDefaultUIUtil().clearView(viewHolder.itemView.findViewById(R.id.content))
        getDefaultUIUtil().clearView(viewHolder.itemView)
        adapter.onClearView(viewHolder)
    }

    private fun isInteractable(viewHolder: RecyclerView.ViewHolder): Boolean {
        return viewHolder.bindingAdapter is DraggableAdapter<*, *, *> &&
            adapter.canInteractWithViewHolder(viewHolder)
    }

    override fun isLongPressDragEnabled(): Boolean = false

}