package dev.olog.feature.presentation.base

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.presentation.base.extensions.dip
import dev.olog.feature.presentation.base.extensions.findFirstVisibleItem
import dev.olog.feature.presentation.base.extensions.findLastVisibleItem

class DottedDividerDecorator(
    context: Context,
    private val headerViewTypes: List<Int>
) : RecyclerView.ItemDecoration() {

    private val dashWidth = context.dip(2)
    private val dashGap = context.dip(2)
    private val dashHeight = context.dip(2)
    private val paint = Paint().apply {
        color = Color.BLACK
        alpha = 25 // 0.1f
    }

    private val rect = Rect()

    private val isDarkMode = context.resources.getBoolean(R.bool.is_dark_mode)

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (isDarkMode) {
            return
        }
        c.save()
        for (index in parent.findFirstVisibleItem..parent.findLastVisibleItem) {
            val vh = parent.findViewHolderForLayoutPosition(index) ?: continue
            drawDivider(c, vh)
        }
        c.restore()
    }

    private fun drawDivider(
        c: Canvas,
        vh: RecyclerView.ViewHolder
    ) {
        if (vh.itemViewType !in headerViewTypes) {
            return
        }
        val view = vh.itemView
        val left = view.left + view.paddingLeft
        val right = view.right - view.paddingRight
        for (index in left..right step dashWidth + dashGap) {
            val top = view.bottom - dashHeight / 2
            val bottom = view.bottom + dashHeight / 2
            rect.set(index, top, index + dashWidth, bottom)

            c.drawRect(rect, paint)
        }
    }

}