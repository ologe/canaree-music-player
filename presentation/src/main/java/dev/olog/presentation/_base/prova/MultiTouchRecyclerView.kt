package dev.olog.presentation._base.prova

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent

class MultiTouchRecyclerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0

) : RecyclerView(context, attrs, defStyle), MultiTouch {

    private val touchListener by lazy { CustomTouch(this) }

    override var isMultiTouchEnabled = false
    private val selectedItems = mutableSetOf<Int>()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addOnItemTouchListener(touchListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disableMultiTouch()
        removeOnItemTouchListener(touchListener)
    }

    fun enableMultiTouch(position: Int){
        isMultiTouchEnabled = true
        setSelected(position)
    }

    fun disableMultiTouch(){
        isMultiTouchEnabled = false
        selectedItems.forEach { position ->
            val holder = findViewHolderForAdapterPosition(position)
            holder.itemView.isSelected = false
            adapter.notifyItemChanged(position)
        }
    }

    override fun setSelected(position: Int): Boolean{
        val holder = findViewHolderForAdapterPosition(position)
        val child = holder.itemView
        if (position != RecyclerView.NO_POSITION){
            child.isSelected = !child.isSelected
            adapter?.notifyItemChanged(position)
            if (child.isSelected){
                selectedItems.add(position)
            } else {
                selectedItems.remove(position)
            }

            return true
        }
        return false
    }


    private class CustomTouch(
            private val multiTouch: MultiTouch

    ) : RecyclerView.OnItemTouchListener {

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        override fun onTouchEvent(rv: RecyclerView?, e: MotionEvent?) {}

        override fun onInterceptTouchEvent(recyclerView: RecyclerView, e: MotionEvent): Boolean {
            if (multiTouch.isMultiTouchEnabled){
                val child = recyclerView.findChildViewUnder(e.x, e.y)
                return multiTouch.setSelected(recyclerView.getChildAdapterPosition(child))
            }

            return false
        }
    }

}

private interface MultiTouch {
    var isMultiTouchEnabled: Boolean
    fun setSelected(position: Int): Boolean
}