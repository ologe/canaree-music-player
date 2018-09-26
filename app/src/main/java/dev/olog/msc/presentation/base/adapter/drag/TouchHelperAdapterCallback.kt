package dev.olog.msc.presentation.base.adapter.drag

import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import dev.olog.msc.R
import kotlin.math.abs

class TouchHelperAdapterCallback(
        private val adapter : TouchableAdapter,
        horizontalDirections: Int

) : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        horizontalDirections
) {

    private var startSwiping = false
    private var circularRevealDrawn = false
    private val handler = Handler(Looper.getMainLooper())

    private val animationsController = TouchHelperAnimationController()

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        if (adapter.canInteractWithViewHolder(viewHolder.itemViewType)!! && adapter.canInteractWithViewHolder(target.itemViewType)!!){
            adapter.onMoved(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }
        return false
    }

    override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        if (adapter.canInteractWithViewHolder(viewHolder.itemViewType)!!){
            return super.getSwipeDirs(recyclerView, viewHolder)
        }
        return 0
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (adapter.canInteractWithViewHolder(viewHolder.itemViewType)!!){
            when (direction){
                ItemTouchHelper.RIGHT -> adapter.onSwipedRight(viewHolder.adapterPosition)
                ItemTouchHelper.LEFT -> {
                    handler.postDelayed({
                        adapter.onSwipedLeft(viewHolder)
                    }, 200)
                }
            }
        }
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView,
                             viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float,
                             actionState: Int, isCurrentlyActive: Boolean) {

        when (actionState){
            ItemTouchHelper.ACTION_STATE_SWIPE -> {
                val viewWidth = viewHolder.itemView.width
                if (abs(dX) < viewWidth * 0.2f){
                    startSwiping = false
                    circularRevealDrawn = false
                } else if (isCurrentlyActive && (abs(dX) > (viewWidth * 0.35f)) && !circularRevealDrawn){
                    circularRevealDrawn = true
                    animationsController.drawCircularReveal(viewHolder, dX)
                }
                if (isCurrentlyActive && !startSwiping){
                    startSwiping = true
                    animationsController.initializeSwipe(viewHolder, dX)
                }

                getDefaultUIUtil().onDraw(c, recyclerView,
                        viewHolder.itemView.findViewById(R.id.content), dX, dY, actionState, isCurrentlyActive)
            }
            ItemTouchHelper.ACTION_STATE_DRAG -> drawOnMove(recyclerView, viewHolder.itemView, isCurrentlyActive, dY)
        }
    }

    private fun drawOnMove(recyclerView: RecyclerView, view: View, isCurrentlyActive: Boolean, dY: Float){
        if (isCurrentlyActive) {
            var originalElevation: Any? = view.getTag(R.id.item_touch_helper_previous_elevation)
            if (originalElevation == null) {
                originalElevation = ViewCompat.getElevation(view)
                val newElevation = 5f + findMaxElevation(recyclerView, view)
                ViewCompat.setElevation(view, newElevation)
                view.setTag(R.id.item_touch_helper_previous_elevation, originalElevation)
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
        circularRevealDrawn = false
        startSwiping = false
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        circularRevealDrawn = false
        startSwiping = false
    }

    override fun getAnimationDuration(recyclerView: RecyclerView, animationType: Int, animateDx: Float, animateDy: Float): Long {
        return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy)
    }

    override fun isLongPressDragEnabled(): Boolean = false

}