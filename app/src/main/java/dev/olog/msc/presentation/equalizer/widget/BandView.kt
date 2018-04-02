package dev.olog.msc.presentation.equalizer.widget

import android.content.Context
import android.support.v4.math.MathUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.TextView
import dev.olog.msc.R
import dev.olog.msc.presentation.equalizer.EqHelper
import dev.olog.msc.utils.k.extension.dimen
import dev.olog.msc.utils.k.extension.dip
import dev.olog.msc.utils.k.extension.setPaddingBottom
import dev.olog.msc.utils.k.extension.vibrate

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

        when (event.actionMasked){
            MotionEvent.ACTION_DOWN -> startInteraction(y)
            MotionEvent.ACTION_MOVE -> onMoveInteraction(y)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> endInteraction()
        }

        return super.onTouchEvent(event)
    }

    private fun createCurrentLevelTextView(){
        currentLevel = TextView(context)
        currentLevel.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        currentLevel.textAlignment = View.TEXT_ALIGNMENT_CENTER
        currentLevel.visibility = View.GONE
        currentLevel.setSingleLine(true)
        currentLevel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8f)
        currentLevel.scaleX = 1.5f
        currentLevel.scaleY = 1.5f
        currentLevel.setPaddingBottom(context.dip(2))


        addView(currentLevel)
    }

    private fun createBand(){
        view = View(context)
        val params = LinearLayout.LayoutParams(
                normalWidth, LinearLayout.LayoutParams.MATCH_PARENT)

        view.layoutParams = params
        view.setBackgroundResource(R.color.dark_grey)
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
                updateHeight(height.toInt())
                return false
            }
        })
    }

    private fun startInteraction(y: Float){
        lastTouchY = y
        isInteracting = true

        updateWidth(1.5f)
        view.alpha = 1f
        currentLevel.visibility = View.VISIBLE

        context.vibrate(40)
    }

    private fun onMoveInteraction(y: Float){
        val dy = y - lastTouchY
        posY -= dy
        lastTouchY = y

        val wy = height - topMargin
        var level = (1 - posY / wy) * (EqHelper.minDB - EqHelper.maxDB) - EqHelper.minDB
        level = MathUtils.clamp(level, EqHelper.minDB, EqHelper.maxDB)

        updateBand(level)

        val text = if (level > 0f) {
            "+" + level.toString()
        } else {
            level.toString()
        }
        currentLevel.text = text.substring(0, text.indexOf(".") + 2)

        val height = (1 - EqHelper.projectY(level)) * (height - topMargin)
        updateHeight(height.toInt())
    }

    private fun endInteraction(){
        isInteracting = false
        updateWidth(1f)
        view.alpha = getAlphaBasedOnPosition()
        currentLevel.visibility = View.GONE
    }

    private fun updateWidth(scaleX: Float){
        view.scaleX = scaleX
    }

    private fun updateHeight(height: Int){
        val layoutParams = view.layoutParams
        layoutParams.height = MathUtils.clamp(height, topMargin / 2, getHeight() - topMargin / 2)
        view.layoutParams = layoutParams
    }

    private fun updateBand(level: Float){
        setLevel?.invoke(bandIndex, level)
    }

    private fun getAlphaBasedOnPosition(): Float {
        return if (bandIndex % 2 == 0) .6f else .5f
    }

    fun onPresetChange(band: Int, level: Float) {
        if (bandIndex == band) {
            val height = (1 - EqHelper.projectY(level)) * (height - topMargin)
            posY = height
            updateHeight(height.toInt())
        }
    }
}