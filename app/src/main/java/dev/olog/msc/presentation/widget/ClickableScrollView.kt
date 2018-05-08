//package dev.olog.msc.presentation.widget
//
//import android.content.Context
//import android.util.AttributeSet
//import android.view.MotionEvent
//import android.view.View
//import android.view.ViewConfiguration
//import android.widget.ScrollView
//import kotlin.math.abs
//
//class ClickableScrollView(
//        context: Context,
//        attrs: AttributeSet? = null
//
//) : ScrollView(context, attrs), View.OnTouchListener {
//
//    private val configuration = ViewConfiguration.get(context)
//
//    private var onClick: (() -> Unit)? = null
//
//    private var timePressed = -1L
//    private var xDown = 0f
//    private var yDown = 0f
//
//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        setOnTouchListener(this)
//    }
//
//    override fun onDetachedFromWindow() {
//        super.onDetachedFromWindow()
//        setOnTouchListener(null)
//    }
//
//    fun setClickBehavior(onClick: () -> Unit) {
//        this.onClick = onClick
//    }
//
//    override fun onTouch(v: View, event: MotionEvent): Boolean {
//        when (event.actionMasked){
//            MotionEvent.ACTION_DOWN -> {
//                timePressed = System.currentTimeMillis()
//                xDown = event.x
//                yDown = event.y
//            }
//            MotionEvent.ACTION_UP -> {
//                if (System.currentTimeMillis() - timePressed <= ViewConfiguration.getTapTimeout()){
//                    val xUp = event.x
//                    val yUp = event.y
//                    if (abs(xUp - xDown) < configuration.scaledTouchSlop &&
//                            abs(yUp - yDown) < configuration.scaledTouchSlop){
//                        onClick?.invoke()
//                    }
//                }
//            }
//        }
//
//        return false
//    }
//}