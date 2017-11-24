//package dev.olog.presentation.fragment_detail
//
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Rect
//import android.support.annotation.LayoutRes
//import android.support.v7.widget.RecyclerView
//import android.view.LayoutInflater
//import android.view.View
//
//
//class HeaderDecoration(context: Context, parent: RecyclerView, @LayoutRes resId: Int) : RecyclerView.ItemDecoration() {
//
//    private val mLayout = LayoutInflater.from(context).inflate(resId, parent, false)
//
//    init {
//        // inflate and measure the layout
//        mLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
//    }
//
//    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
//        super.onDraw(c, parent, state)
//        // layout basically just gets drawn on the reserved space on top of the first view
//        mLayout.layout(parent.left, 0, parent.right, mLayout.measuredHeight)
//
//        val adapter = parent.adapter as CoolDetailAdapter
//        for (headerPosition in adapter.getHeaderPositions()) {
//            val view = parent.getChildAt(headerPosition)
//            if (view != null){
//                c.save()
//                val height = mLayout.measuredHeight
//                val top = view.top - height
//                c.translate(0f, top.toFloat())
//                mLayout.draw(c)
//                c.restore()
//            }
//
//        }
//
////        for (i in 0 until parent.childCount) {
////            val view = parent.getChildAt(i)
////            val adapter = parent.adapter as CoolDetailAdapter
////            if (parent.getChildAdapterPosition(view) == 0) {
////
////                break
////            }
////        }
//    }
//
//    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
//        val adapter = parent.adapter as CoolDetailAdapter
//        for (headerPosition in adapter.getHeaderPositions()) {
//            outRect.set(0, mLayout.measuredHeight, 0, 0)
//        }
////        if (parent.getChildAdapterPosition(view) == 0) {
////            outRect.set(0, mLayout.measuredHeight, 0, 0)
////        } else {
////            outRect.setEmpty()
////        }
//    }
//}