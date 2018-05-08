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
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.utils.k.extension.setGone
import dev.olog.msc.utils.k.extension.setVisible

class TouchHelperAnimationController {

    private val deleteBackground = ColorDrawable(0xFF_f44336.toInt())
    private val playNextBackground = ColorDrawable(0xFF_2979FF.toInt())

    private var deleteIcon: Drawable? = null
    private var playNextIcon: Drawable? = null

    private var deleteIntrinsicWidth = 0
    private var deleteIntrinsicHeight = 0

    private var playNextIntrinsicWidth = 0
    private var playNextIntrinsicHeight = 0

    fun drawMove(viewHolder: RecyclerView.ViewHolder){
        viewHolder.itemView.findViewById<View>(R.id.scrim)?.setVisible()
    }

    fun drawSwipeRight(canvas: Canvas, viewHolder: RecyclerView.ViewHolder, dx: Float){
        val view = viewHolder.itemView
        drawDeleteBackground(canvas, view, dx)
        drawDeleteIcon(canvas, view, dx)
    }

    fun drawSwipeLeft(canvas: Canvas, viewHolder: RecyclerView.ViewHolder, dx: Float){
        val view = viewHolder.itemView
        drawPlayNextBackground(canvas, view, dx)
        drawPlayNextIcon(canvas, view, dx)
    }

    fun clear(viewHolder: RecyclerView.ViewHolder){
        ViewCompat.setElevation(viewHolder.itemView, 0f)
        viewHolder.itemView.findViewById<View>(R.id.scrim)?.setGone()
    }

    private fun drawDeleteBackground(canvas: Canvas, view: View, dx: Float){
        if (dx < 0) return
        deleteBackground.setBounds(view.left, view.top, (view.left + dx).toInt(), view.bottom)
        deleteBackground.draw(canvas)
    }

    private fun drawPlayNextBackground(canvas: Canvas, view: View, dx: Float){
        if (dx > 0) return
        playNextBackground.setBounds((view.right + dx).toInt(), view.top, view.right, view.bottom)
        playNextBackground.draw(canvas)
    }

    private fun drawDeleteIcon(canvas: Canvas, view: View, dx: Float){
        if (dx < 0) return

        val itemHeight = view.bottom - view.top

        val deleteIcon = getDeleteIcon(view.context)

        val deleteIconTop = view.top + (itemHeight - deleteIntrinsicHeight) / 2
        val deleteIconMargin = (itemHeight - deleteIntrinsicHeight) / 2
        val deleteIconLeft = view.left + deleteIconMargin
        val deleteIconRight = view.left + deleteIconMargin + deleteIntrinsicWidth
        val deleteIconBottom = deleteIconTop + deleteIntrinsicHeight
        // Draw the delete icon
        deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)

        deleteIcon.draw(canvas)
        ViewCompat.setElevation(view, 0f)
    }

    private fun drawPlayNextIcon(canvas: Canvas, view: View, dx: Float){
        if (dx > 0) return

        val itemHeight = view.bottom - view.top

        val playNextIcon = getPlayNextIcon(view.context)

        val playNextIconTop = view.top + (itemHeight - playNextIntrinsicHeight) / 2
        val playNextIconMargin = (itemHeight - playNextIntrinsicHeight) / 2
        val playNextIconLeft = view.right - playNextIconMargin - playNextIntrinsicWidth
        val playNextIconRight = view.right - playNextIconMargin
        val playNextIconBottom = playNextIconTop + playNextIntrinsicHeight
        // Draw the delete icon
        playNextIcon.setBounds(playNextIconLeft, playNextIconTop, playNextIconRight, playNextIconBottom)

        playNextIcon.draw(canvas)
        ViewCompat.setElevation(view, 0f)
    }

    private fun getDeleteIcon(context: Context): Drawable{
        if (deleteIcon == null){
            deleteIcon = ContextCompat.getDrawable(context, R.drawable.vd_delete)
            deleteIcon!!.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            deleteIntrinsicWidth = deleteIcon!!.intrinsicWidth
            deleteIntrinsicHeight = deleteIcon!!.intrinsicHeight
        }
        return deleteIcon!!
    }

    private fun getPlayNextIcon(context: Context): Drawable {
        if (playNextIcon == null){
            playNextIcon = ContextCompat.getDrawable(context, R.drawable.vd_playlist_add)
            playNextIcon!!.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            playNextIntrinsicWidth = playNextIcon!!.intrinsicWidth
            playNextIntrinsicHeight = playNextIcon!!.intrinsicHeight
        }
        return playNextIcon!!
    }

}