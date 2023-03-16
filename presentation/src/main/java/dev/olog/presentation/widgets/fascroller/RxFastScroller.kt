package dev.olog.presentation.widgets.fascroller

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.widget.*
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.R
import dev.olog.ui.palette.colorAccent
import dev.olog.ui.palette.colorControlNormal
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import java.lang.Runnable

private const val BUBBLE_ANIMATION_DURATION = 100
private const val SCROLL_BAR_ANIMATION_DURATION = 300
private const val SCROLL_BAR_HIDE_DELAY = 1000
private const val TRACK_SNAP_RANGE = 5

class RxFastScroller(
        context: Context,
        attrs: AttributeSet

) : LinearLayout(context, attrs), CoroutineScope by MainScope() {


    interface SectionIndexer {
        fun getSectionText(position: Int): String?
    }

    init {
        layout(context, attrs)
        layoutParams = generateLayoutParams(attrs)
    }

    private fun layout(context: Context, attrs: AttributeSet?) {
        View.inflate(context, R.layout.layout_fastscroller, this)

        clipChildren = false
        orientation = HORIZONTAL

        mBubbleView = findViewById<View>(R.id.fastscroll_bubble) as TextView
        mHandleView = findViewById<View>(R.id.fastscroll_handle) as ImageView
        mScrollbar = findViewById(R.id.fastscroll_scrollbar)

        var bubbleColor = Color.GRAY
        var handleColor = Color.DKGRAY
        var textColor = Color.WHITE

        var hideScrollbar = true

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FastScroller, 0, 0)

            try {
                bubbleColor = context.colorAccent()
                handleColor = context.colorControlNormal()
                textColor = typedArray.getColor(R.styleable.FastScroller_bubbleTextColor, textColor)
                hideScrollbar = typedArray.getBoolean(R.styleable.FastScroller_hideScrollbar, true)
            } finally {
                typedArray.recycle()
            }
        }

        setHandleColor(handleColor)
        setBubbleColor(bubbleColor)
        setBubbleTextColor(textColor)
        setHideScrollbar(hideScrollbar)
    }

    @ColorInt private var mBubbleColor: Int = 0
    @ColorInt private var mHandleColor: Int = 0

    private var mHeight: Int = 0
    private var mHideScrollbar: Boolean = false
    private var mSectionIndexer: SectionIndexer? = null
    private var mScrollbarAnimator: ViewPropertyAnimator? = null
    private var mBubbleAnimator: ViewPropertyAnimator? = null
    private var mRecyclerView: RecyclerView? = null
    private var mBubbleView: TextView? = null
    private var mHandleView: ImageView? = null
    private var mScrollbar: View? = null
    private var mBubbleImage: Drawable? = null
    private var mHandleImage: Drawable? = null

    private val bubbleTextPublisher = ConflatedBroadcastChannel("")
    private val scrollPublisher = ConflatedBroadcastChannel<Int>(RecyclerView.NO_POSITION)
    private var bubbleTextDisposable : Job? = null
    private var scrollDisposable : Job? = null

    private val mScrollbarHider = Runnable { hideScrollbar() }

    private val mScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (!mHandleView!!.isSelected && isEnabled) {
                setViewPositions(getScrollProportion(recyclerView))
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (isEnabled) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        handler.removeCallbacks(mScrollbarHider)
                        cancelAnimation(mScrollbarAnimator)

                        if (!isViewVisible(mScrollbar)) {
                            showScrollbar()
                        }
                    }

                    RecyclerView.SCROLL_STATE_IDLE -> if (mHideScrollbar && !mHandleView!!.isSelected) {
                        handler.postDelayed(mScrollbarHider, SCROLL_BAR_HIDE_DELAY.toLong())
                    }
                }
            }
        }
    }

    private var showBubble = false

    override fun setLayoutParams(params: ViewGroup.LayoutParams) {
        params.width = LayoutParams.WRAP_CONTENT
        super.setLayoutParams(params)
    }

    fun setLayoutParams(viewGroup: ViewGroup) {
        val recyclerViewId = mRecyclerView!!.id
        val marginTop = resources.getDimensionPixelSize(R.dimen.fastscroll_scrollbar_margin_top)
        val marginBottom = resources.getDimensionPixelSize(R.dimen.fastscroll_scrollbar_margin_bottom)

        if (recyclerViewId == View.NO_ID) {
            throw IllegalArgumentException("RecyclerView must have a view ID")
        }

        if (viewGroup is ConstraintLayout) {
            val constraintSet = ConstraintSet()
            val layoutId = id

            constraintSet.connect(layoutId, ConstraintSet.TOP, recyclerViewId, ConstraintSet.TOP)
            constraintSet.connect(layoutId, ConstraintSet.BOTTOM, recyclerViewId, ConstraintSet.BOTTOM)
            constraintSet.connect(layoutId, ConstraintSet.END, recyclerViewId, ConstraintSet.END)
            constraintSet.applyTo(viewGroup)

            val layoutParams = layoutParams as ConstraintLayout.LayoutParams
            layoutParams.setMargins(0, marginTop, 0, marginBottom)
            setLayoutParams(layoutParams)

        } else if (viewGroup is androidx.coordinatorlayout.widget.CoordinatorLayout) {
            val layoutParams = layoutParams as androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams

            layoutParams.anchorId = recyclerViewId
            layoutParams.anchorGravity = GravityCompat.END
            layoutParams.setMargins(0, marginTop, 0, marginBottom)
            setLayoutParams(layoutParams)

        } else if (viewGroup is FrameLayout) {
            val layoutParams = layoutParams as FrameLayout.LayoutParams

            layoutParams.gravity = GravityCompat.END
            layoutParams.setMargins(0, marginTop, 0, marginBottom)
            setLayoutParams(layoutParams)

        } else if (viewGroup is RelativeLayout) {
            val layoutParams = layoutParams as RelativeLayout.LayoutParams
            val endRule = RelativeLayout.ALIGN_END

            layoutParams.addRule(RelativeLayout.ALIGN_TOP, recyclerViewId)
            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, recyclerViewId)
            layoutParams.addRule(endRule, recyclerViewId)
            layoutParams.setMargins(0, marginTop, 0, marginBottom)
            setLayoutParams(layoutParams)

        } else {
            throw IllegalArgumentException("Parent ViewGroup must be a ConstraintLayout, CoordinatorLayout, FrameLayout, or RelativeLayout")
        }
    }

    fun setSectionIndexer(sectionIndexer: SectionIndexer?) {
        mSectionIndexer = sectionIndexer
    }

    fun showBubble(show: Boolean){
        showBubble = show
    }

    fun attachRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = recyclerView


        if (mRecyclerView != null) {
            mRecyclerView!!.addOnScrollListener(mScrollListener)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (!isInEditMode){
            if (showBubble){
                launch {
                    bubbleTextDisposable = launch {
                        bubbleTextPublisher.asFlow()
                            .distinctUntilChanged()
                            .flowOn(Dispatchers.Default)
                            .map {
                                when {
                                    it < "A" -> "#"
                                    it > "Z" -> "?"
                                    else -> it
                                }
                            }.collect { mBubbleView!!.text = it }
                    }
                }
            }

            scrollDisposable = launch {
                scrollPublisher.asFlow()
                    .filter { it != RecyclerView.NO_POSITION }
                    .distinctUntilChanged()
                    .flowOn(Dispatchers.Default)
                    .collect { mRecyclerView?.layoutManager?.scrollToPosition(it) }
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        bubbleTextDisposable?.cancel()
        scrollDisposable?.cancel()
    }

    fun detachRecyclerView() {
        if (mRecyclerView != null) {
            mRecyclerView!!.removeOnScrollListener(mScrollListener)
            mRecyclerView = null
        }
    }

    /**
     * Hide the scrollbar when not scrolling.
     *
     * @param hideScrollbar True to hide the scrollbar, false to show
     */
    fun setHideScrollbar(hideScrollbar: Boolean) {
        mHideScrollbar = hideScrollbar
        mScrollbar!!.visibility = if (hideScrollbar) View.GONE else View.VISIBLE
    }

    /**
     * Set the color for the scroll handle.
     *
     * @param color The color for the scroll handle
     */
    fun setHandleColor(@ColorInt color: Int) {
        mHandleColor = color

        if (mHandleImage == null) {
            mHandleImage = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.fastscroll_handle)!!)
            mHandleImage!!.mutate()
        }

        DrawableCompat.setTint(mHandleImage!!, mHandleColor)
        mHandleView!!.setImageDrawable(mHandleImage)
    }

    /**
     * Set the background color of the index bubble.
     *
     * @param color The background color for the index bubble
     */
    fun setBubbleColor(@ColorInt color: Int) {
        mBubbleColor = color

        if (mBubbleImage == null) {
            mBubbleImage = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.fastscroll_bubble)!!)
            mBubbleImage!!.mutate()
        }

        DrawableCompat.setTint(mBubbleImage!!, mBubbleColor)

        mBubbleView!!.background = mBubbleImage
    }

    /**
     * Set the text color of the index bubble.
     *
     * @param color The text color for the index bubble
     */
    fun setBubbleTextColor(@ColorInt color: Int) {
        mBubbleView!!.setTextColor(color)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        visibility = if (enabled) View.VISIBLE else View.GONE
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mRecyclerView?.stopScroll()
                if (event.x < mHandleView!!.x - ViewCompat.getPaddingStart(mHandleView!!)) {
                    return false
                }

                setHandleSelected(true)

                handler.removeCallbacks(mScrollbarHider)
                cancelAnimation(mScrollbarAnimator)
                cancelAnimation(mBubbleAnimator)

                if (!isViewVisible(mScrollbar)) {
                    showScrollbar()
                }

                if (!isViewVisible(mBubbleView) && showBubble) {
                    showBubble()
                }

                val y = event.y
                setViewPositions(y)
                setRecyclerViewPosition(y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val y = event.y
                setViewPositions(y)
                setRecyclerViewPosition(y)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                setHandleSelected(false)

                if (mHideScrollbar) {
                    handler.postDelayed(mScrollbarHider, SCROLL_BAR_HIDE_DELAY.toLong())
                }

                if (isViewVisible(mBubbleView) && showBubble) {
                    hideBubble()
                }

                return true
            }
        }

        return super.onTouchEvent(event)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mHeight = h
    }

    private fun setRecyclerViewPosition(y: Float) {
        if (mRecyclerView != null && mRecyclerView?.adapter != null) {
            val itemCount = mRecyclerView!!.adapter!!.itemCount
            val proportion: Float

            if (mHandleView!!.y == 0f) {
                proportion = 0f
            } else if (mHandleView!!.y + mHandleView!!.height >= mHeight - TRACK_SNAP_RANGE) {
                proportion = 1f
            } else {
                proportion = y / mHeight.toFloat()
            }

            val targetPos = getValueInRange(0, itemCount - 1, (proportion * itemCount.toFloat()).toInt())
            scrollPublisher.trySend(targetPos)

            val letter = mSectionIndexer?.getSectionText(targetPos)
            letter?.let { bubbleTextPublisher.trySend(it) }
        }
    }

    private fun getScrollProportion(recyclerView: RecyclerView): Float {
        val verticalScrollOffset = recyclerView.computeVerticalScrollOffset()
        val verticalScrollRange = recyclerView.computeVerticalScrollRange()
        val rangeDiff = (verticalScrollRange - mHeight).toFloat()
        val proportion = verticalScrollOffset.toFloat() / if (rangeDiff > 0) rangeDiff else 1f
        return mHeight * proportion
    }

    private fun getValueInRange(min: Int, max: Int, value: Int): Int {
        val minimum = Math.max(min, value)
        return Math.min(minimum, max)
    }

    private fun setViewPositions(y: Float) {
        val bubbleHeight = mBubbleView!!.height
        val handleHeight = mHandleView!!.height

        mBubbleView!!.y = getValueInRange(0, mHeight - bubbleHeight - handleHeight / 2, (y - bubbleHeight).toInt()).toFloat()
        mHandleView!!.y = getValueInRange(0, mHeight - handleHeight, (y - handleHeight / 2).toInt()).toFloat()
    }

    private fun isViewVisible(view: View?): Boolean {
        return view != null && view.visibility == View.VISIBLE
    }

    private fun cancelAnimation(animator: ViewPropertyAnimator?) {
        animator?.cancel()
    }

    private fun showBubble() {
        mBubbleView!!.visibility = View.VISIBLE
        mBubbleAnimator = mBubbleView!!.animate().alpha(1f)
                .setDuration(BUBBLE_ANIMATION_DURATION.toLong())
                .setListener(object : AnimatorListenerAdapter() {

                    // adapter required for new alpha value to stick
                })
    }

    private fun hideBubble() {
        mBubbleAnimator = mBubbleView!!.animate().alpha(0f)
                .setDuration(BUBBLE_ANIMATION_DURATION.toLong())
                .setListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        mBubbleView!!.visibility = View.GONE
                        mBubbleAnimator = null
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        super.onAnimationCancel(animation)
                        mBubbleView!!.visibility = View.GONE
                        mBubbleAnimator = null
                    }
                })
    }

    private fun showScrollbar() {
        if (mRecyclerView!!.computeVerticalScrollRange() - mHeight > 0) {
            val transX = resources.getDimensionPixelSize(R.dimen.fastscroll_scrollbar_padding_end).toFloat()

            mScrollbar!!.translationX = transX
            mScrollbar!!.visibility = View.VISIBLE
            mScrollbarAnimator = mScrollbar!!.animate().translationX(0f).alpha(1f)
                    .setDuration(SCROLL_BAR_ANIMATION_DURATION.toLong())
                    .setListener(object : AnimatorListenerAdapter() {
                        // adapter required for new alpha value to stick
                    })
        }
    }

    private fun hideScrollbar() {
        val transX = resources.getDimensionPixelSize(R.dimen.fastscroll_scrollbar_padding_end).toFloat()

        mScrollbarAnimator = mScrollbar!!.animate()
                .translationX(transX)
                .alpha(0f)
                .setDuration(SCROLL_BAR_ANIMATION_DURATION.toLong())
                .setListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        mScrollbar!!.visibility = View.GONE
                        mScrollbarAnimator = null
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        super.onAnimationCancel(animation)
                        mScrollbar!!.visibility = View.GONE
                        mScrollbarAnimator = null
                    }
                })
    }

    private fun setHandleSelected(selected: Boolean) {
        mHandleView!!.isSelected = selected
        DrawableCompat.setTint(mHandleImage!!, if (selected) mBubbleColor else mHandleColor)
    }

}
