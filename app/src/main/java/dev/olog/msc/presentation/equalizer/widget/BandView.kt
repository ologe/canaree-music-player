package dev.olog.msc.presentation.equalizer.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.math.MathUtils
import dev.olog.msc.R
import dev.olog.msc.presentation.equalizer.EqHelper
import dev.olog.msc.presentation.equalizer.ResizeAnimation
import dev.olog.shared.extensions.*
import dev.olog.shared.utils.clamp

class BandView (
        context: Context,
        attrs: AttributeSet? = null

) : LinearLayout(context, attrs) {

    private val normalWidth = context.dimen(R.dimen.eq_bar_width)
    private val topMargin = context.dimen(R.dimen.eq_bar_top_margin)
    private var bandIndex: Int = 0

    private lateinit var view: View
    private lateinit var currentLevel : TextView

    private var lastTouchY = 0f
    private var posY = -1f

    private var isInteracting: Boolean = false

    var setLevel : ((Int, Float) -> Unit)? = null

    init {

        val a = context.obtainStyledAttributes(attrs, R.styleable.BandView)
        bandIndex = a.getInt(R.styleable.BandView_bandIndex, -1)
        a.recycle()

        isClickable = true
        isFocusable = true
        clipChildren = false
        clipToOutline = false
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.BOTTOM

        createCurrentLevelTextView()
        createBand()
    }

    // intercept all clicks
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean = true

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val y = event.rawY

        return when (event.actionMasked){
            MotionEvent.ACTION_DOWN -> startInteraction(y)
            MotionEvent.ACTION_MOVE -> onMoveInteraction(y)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> endInteraction()
            else -> super.onTouchEvent(event)
        }
    }

    private fun createCurrentLevelTextView(){
        currentLevel = TextView(context)
        currentLevel.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        currentLevel.textAlignment = View.TEXT_ALIGNMENT_CENTER
        currentLevel.visibility = View.GONE
        currentLevel.setSingleLine(true)
        currentLevel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8f)
        currentLevel.gravity = Gravity.CENTER_HORIZONTAL
        currentLevel.scaleX = 1.5f
        currentLevel.scaleY = 1.5f
        currentLevel.setPaddingBottom(context.dip(2))
        currentLevel.setTextColor(context.textColorPrimary())

        addView(currentLevel)
    }

    private fun createBand(){
        view = View(context)
        val params = LinearLayout.LayoutParams(
                normalWidth, LinearLayout.LayoutParams.MATCH_PARENT)

        view.layoutParams = params
        view.setBackgroundColor(context.colorPrimary())
        view.alpha = getAlphaBasedOnPosition()
        view.elevation = resources.getDimension(R.dimen.eq_bar_elevation)

        addView(view)
    }

    fun initializeBandHeight(level: Float){
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                viewTreeObserver.removeOnPreDrawListener(this)
                val height = (1 - EqHelper.projectY(level)) * (height - topMargin)
                posY = height
                updateHeight(height.toInt(), false)
                return false
            }
        })
    }

    private fun startInteraction(y: Float): Boolean {
        lastTouchY = y
        isInteracting = true

        updateWidth(1.5f)
        view.alpha = 1f
        currentLevel.visibility = View.VISIBLE

        context.vibrate(40)

        parent.requestDisallowInterceptTouchEvent(true)

        return true
    }

    private fun onMoveInteraction(y: Float): Boolean{
        val dy = y - lastTouchY
        posY -= dy
        lastTouchY = y

        val wy = height - topMargin
        var level = (1 - posY / wy) * (EqHelper.minDB - EqHelper.maxDB) - EqHelper.minDB
        level = MathUtils.clamp(level, EqHelper.minDB, EqHelper.maxDB)

        updateBand(level)

        val text = when {
            level > 0f -> "+$level"
            level == 0f -> "$level"
            else -> "$level"
        }
        currentLevel.text = text.substring(0, text.indexOf(".") + 2)

        val height = (1 - EqHelper.projectY(level)) * (height - topMargin)
        updateHeight(height.toInt(), false)

        return true
    }

    private fun endInteraction(): Boolean{
        isInteracting = false
        updateWidth(1f)
        view.alpha = getAlphaBasedOnPosition()
        currentLevel.visibility = View.GONE

        parent.requestDisallowInterceptTouchEvent(false)

        return true
    }

    private fun updateWidth(scaleX: Float){
        view.scaleX = scaleX
    }

    private fun updateHeight(height: Int, animate: Boolean){
        val targetHeight = clamp(height, topMargin / 2, getHeight() - topMargin / 2)

        if (animate) {
            val resizeAnimation = ResizeAnimation(view, targetHeight - view.height)
            resizeAnimation.duration = 300
            view.startAnimation(resizeAnimation)
        } else {
            view.layoutParams.height = targetHeight
            view.requestLayout()
        }
    }

    private fun updateBand(level: Float){
        setLevel?.invoke(bandIndex, level)
    }

    private fun getAlphaBasedOnPosition(): Float {
        return if (bandIndex % 2 == 0) .5f else .4f
    }

    fun onPresetChange(band: Int, level: Float) {
        if (bandIndex == band) {
            val height = (1 - EqHelper.projectY(level)) * (height - topMargin)
            posY = height
            updateHeight(height.toInt(), true)
        }
    }
}