package dev.olog.msc.presentation.base.adapter.drag

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.utils.k.extension.setGone
import dev.olog.msc.utils.k.extension.setVisible

class TouchHelperAdapterCallback(
        private val adapter : TouchableAdapter

) : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        ItemTouchHelper.RIGHT

) {

    private val deleteBackground = ColorDrawable(0xfff44336.toInt())
    private var deleteIcon: Drawable? = null
    private var intrinsicWidth = 0
    private var intrinsicHeight = 0

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        if (adapter.canInteractWithViewHolder(viewHolder.itemViewType)!! &&
                adapter.canInteractWithViewHolder(target.itemViewType)!!){
            adapter.onMoved(viewHolder.adapterPosition, target.adapterPosition)
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
            ItemTouchHelper.ACTION_STATE_SWIPE -> drawOnSwiped(c, viewHolder, dX)
            ItemTouchHelper.ACTION_STATE_DRAG -> drawOnMoved(viewHolder)
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun drawOnSwiped(canvas: Canvas, viewHolder: RecyclerView.ViewHolder, dx: Float){

        val view = viewHolder.itemView
        drawDeleteBackground(canvas, view, dx)
        drawDeleteIcon(canvas, view, dx)
    }

    private fun drawDeleteBackground(canvas: Canvas, view: View, dx: Float){

        if (dx < 0) return
        deleteBackground.setBounds(view.left, view.top, (view.left + dx).toInt(), view.bottom)
        deleteBackground.draw(canvas)
    }

    private fun drawDeleteIcon(canvas: Canvas, view: View, dx: Float){
        if (dx < 0) return

        val itemHeight = view.bottom - view.top

        val deleteIcon = getDeleteIcon(view.context)

        val deleteIconTop = view.top + (itemHeight - intrinsicHeight) / 2
        val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
        val deleteIconLeft = view.left + deleteIconMargin
        val deleteIconRight = view.left + deleteIconMargin + intrinsicWidth
        val deleteIconBottom = deleteIconTop + intrinsicHeight
        // Draw the delete icon
        deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)

        deleteIcon.draw(canvas)
        ViewCompat.setElevation(view, 0f)
    }

    private fun getDeleteIcon(context: Context): Drawable{
        if (deleteIcon == null){
            deleteIcon = ContextCompat.getDrawable(context, R.drawable.vd_delete)
            deleteIcon!!.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            intrinsicWidth = deleteIcon!!.intrinsicWidth
            intrinsicHeight = deleteIcon!!.intrinsicHeight
        }
        return deleteIcon!!
    }

    private fun drawOnMoved(viewHolder: RecyclerView.ViewHolder){
        viewHolder.itemView.findViewById<View>(R.id.scrim)?.setVisible()
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        ViewCompat.setElevation(viewHolder.itemView, 0f)
        adapter.onInteractionEnd(viewHolder.adapterPosition)
        viewHolder.itemView.findViewById<View>(R.id.scrim)?.setGone()
    }

    override fun isLongPressDragEnabled(): Boolean = false

}