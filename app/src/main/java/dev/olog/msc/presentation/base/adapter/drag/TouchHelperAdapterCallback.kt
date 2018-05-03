package dev.olog.msc.presentation.base.adapter.drag

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class TouchHelperAdapterCallback(
        private val adapter : TouchableAdapter,
        horizontalDirections: Int

) : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        horizontalDirections

) {

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
            adapter.onSwiped(viewHolder.adapterPosition)
        }
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView,
                             viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float,
                             actionState: Int, isCurrentlyActive: Boolean) {

        when (actionState){
            ItemTouchHelper.ACTION_STATE_SWIPE -> animationsController.drawSwipe(c, viewHolder, dX)
            ItemTouchHelper.ACTION_STATE_DRAG -> animationsController.drawMove(viewHolder)
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }


    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        adapter.onInteractionEnd(viewHolder.adapterPosition)
        animationsController.clear(viewHolder)
    }

    override fun isLongPressDragEnabled(): Boolean = false

}