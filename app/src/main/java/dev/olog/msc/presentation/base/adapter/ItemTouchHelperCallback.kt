package dev.olog.msc.presentation.base.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import dev.olog.msc.R

class ItemTouchHelperCallback(
        context: Context?,
        private val adapter: ItemTouchHelperAdapter,
        private val canSwipe: Boolean

) : ItemTouchHelper.Callback() {

    private val deleteIcon = ContextCompat.getDrawable(context!!, R.drawable.vd_delete)!!
    private val intrinsicWidth = deleteIcon.intrinsicWidth
    private val intrinsicHeight = deleteIcon.intrinsicHeight
    private val background = ColorDrawable(0xfff44336.toInt())

    init {
        deleteIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
    }

    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.END or ItemTouchHelper.START
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        if (target.itemViewType == adapter.draggableViewType){
            adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (viewHolder.itemViewType == adapter.draggableViewType){
            adapter.onItemDismiss(viewHolder.adapterPosition)
        }
    }

    override fun isItemViewSwipeEnabled(): Boolean = canSwipe

    override fun isLongPressDragEnabled(): Boolean = false

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            val itemView = viewHolder.itemView
            val itemHeight = itemView.bottom - itemView.top

            // Draw the red delete background
            if (dX >= 0){
                background.setBounds(recyclerView.left, itemView.top, recyclerView.left + dX.toInt(), itemView.bottom)
            } else {
                background.setBounds(recyclerView.right + dX.toInt(), itemView.top, recyclerView.right, itemView.bottom)
            }

            background.draw(c)

            // Calculate position of delete icon
            if (dX >= 0){
                val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
                val deleteIconLeft = itemView.left + deleteIconMargin
                val deleteIconRight = itemView.left + deleteIconMargin + intrinsicWidth
                val deleteIconBottom = deleteIconTop + intrinsicHeight
                // Draw the delete icon
                deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
            } else {
                val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
                val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
                val deleteIconRight = itemView.right - deleteIconMargin
                val deleteIconBottom = deleteIconTop + intrinsicHeight
                // Draw the delete icon
                deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
            }

            deleteIcon.draw(c)
            ViewCompat.setElevation(viewHolder.itemView, 0f)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        ViewCompat.setElevation(viewHolder.itemView, 0f)
    }

}