package dev.olog.presentation.widgets.audiowave

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import dev.olog.presentation.R

class AudioWaveView : View {

    constructor(context: Context?) : super(context) {
        setWillNotDraw(false)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setWillNotDraw(false)
        inflateAttrs(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
            defStyleAttr) {
        setWillNotDraw(false)
        inflateAttrs(attrs)
    }

//    var onProgressListener: OnProgressListener? = null

//    var onProgressChanged: (Float, Boolean) -> Unit = { _, _ -> Unit }

//    var onStartTracking: (Float) -> Unit = {}

//    var onStopTracking: (Float) -> Unit = {}

    var chunkHeight: Int = 0
        get() = if (field == 0) h else Math.abs(field)
        set(value) {
            field = value
            redrawData()
        }

    var chunkWidth: Int = dip(2)
        set(value) {
            field = Math.abs(value)
            redrawData()
        }

    var chunkSpacing: Int = dip(1)
        set(value) {
            field = Math.abs(value)
            redrawData()
        }

    var chunkRadius: Int = 0
        set(value) {
            field = Math.abs(value)
            redrawData()
        }

    var minChunkHeight: Int = dip(2)
        set(value) {
            field = Math.abs(value)
            redrawData()
        }

    var waveColor: Int = Color.BLACK
        set(value) {
            wavePaint = smoothPaint(value.withAlpha(0xAA))
            waveFilledPaint = filterPaint(value)
            postInvalidate()
        }

    var progress: Float = 0F
        set(value) {
            require(value in 0..100) { "Progress must be in 0..100" }

            field = Math.abs(value)

//            onProgressListener?.onProgressChanged(field, isTouched)
//            onProgressChanged(field, isTouched)

            postInvalidate()
        }

    var scaledData: ByteArray = byteArrayOf()
        set(value) {
            field = if (value.size <= chunksCount) {
                ByteArray(chunksCount).paste(value)
            } else {
                value
            }

            redrawData()
        }

    var expansionDuration: Long = 400
        set(value) {
            field = Math.max(400, value)
            expansionAnimator.duration = field
        }

    var isExpansionAnimated: Boolean = true

    var isTouched = false

    private val chunksCount: Int
        get() = w / chunkStep

    private val chunkStep: Int
        get() = chunkWidth + chunkSpacing

    private val centerY: Int
        get() = h / 2

    private val progressFactor: Float
        get() = progress / 100F

    private val initialDelay: Long = 50

    private val expansionAnimator = ValueAnimator.ofFloat(0.0F, 1.0F).apply {
        duration = expansionDuration
        interpolator = OvershootInterpolator()
        addUpdateListener {
            redrawData(factor = it.animatedFraction)
        }
    }

    private var wavePaint =
        smoothPaint(waveColor.withAlpha(0xAA))
    private var waveFilledPaint = filterPaint(waveColor)
    private var waveBitmap: Bitmap? = null

    private var w: Int = 0
    private var h: Int = 0

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val cv = canvas ?: return

        waveBitmap?.let { bitmap ->
            cv.transform {
                clipRect(0, 0, w, h)
                drawBitmap(bitmap, 0F, 0F, wavePaint)
            }

            cv.transform {
                clipRect(0F, 0F, w * progressFactor, h.toFloat())
                drawBitmap(bitmap, 0F, 0F, waveFilledPaint)
            }
        }
    }

    // suppressed here since we allocate only once,
    // when the wave bounds have been just calculated(it happens once)
    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        w = right - left
        h = bottom - top

        if (waveBitmap.fits(w, h)) {
            return
        }

        if (changed) {
            waveBitmap.safeRecycle()
            waveBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

            // absolutely ridiculous hack to draw wave in RecyclerView items
            scaledData = when (scaledData.size) {
                0 -> byteArrayOf()
                else -> scaledData
            }
        }
    }

//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        event ?: return super.onTouchEvent(event)
//
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                isTouched = true
//                progress = event.toProgress()
//
//                // these paired calls look ugly, but we need them for Java
//                onProgressListener?.onStartTracking(progress)
//                onStartTracking(progress)
//
//                return true
//            }
//            MotionEvent.ACTION_MOVE -> {
//                isTouched = true
//                progress = event.toProgress()
//                return true
//            }
//            MotionEvent.ACTION_UP -> {
//                isTouched = false
//                onProgressListener?.onStopTracking(progress)
//                onStopTracking(progress)
//                return false
//            }
//            else -> {
//                isTouched = false
//                return super.onTouchEvent(event)
//            }
//        }
//    }

    // Java convenience
    fun setRawData(raw: ByteArray, callback: OnSamplingListener) {
        setRawData(raw) { callback.onComplete() }
    }

    @JvmOverloads
    fun setRawData(raw: ByteArray, callback: () -> Unit = {}) {
        MAIN_THREAD.postDelayed({
            Sampler.downSampleAsync(raw, chunksCount) {
                scaledData = it
                callback()

                if (isExpansionAnimated) {
                    animateExpansion()
                }
            }
        }, initialDelay)
    }

    private fun MotionEvent.toProgress() = this@toProgress.x.clamp(0F, w.toFloat()) / w * 100F

    private fun redrawData(canvas: Canvas? = waveBitmap?.inCanvas(), factor: Float = 1.0F) {
        if (waveBitmap == null || canvas == null) return

        waveBitmap.flush()

        scaledData.forEachIndexed { i, chunk ->
            val chunkHeight = ((chunk.abs.toFloat() / Byte.MAX_VALUE) * chunkHeight).toInt()
            val clampedHeight = Math.max(chunkHeight, minChunkHeight)
            val heightDiff = (clampedHeight - minChunkHeight).toFloat()
            val animatedDiff = (heightDiff * factor).toInt()

            canvas.drawRoundRect(
                rectFOf(
                    left = chunkSpacing / 2 + i * chunkStep,
                    top = height - animatedDiff,
                    right = chunkSpacing / 2 + i * chunkStep + chunkWidth,
                    bottom = height
                ),
                    chunkRadius.toFloat(),
                    chunkRadius.toFloat(),
                    wavePaint
            )
        }

        postInvalidate()
    }

    private fun animateExpansion() {
        expansionAnimator.start()
    }

    private fun inflateAttrs(attrs: AttributeSet?) {
        val resAttrs = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.AudioWaveView,
                0,
                0
        ) ?: return

        with(resAttrs) {
            chunkHeight = getDimensionPixelSize(R.styleable.AudioWaveView_chunkHeight, chunkHeight)
            chunkWidth = getDimensionPixelSize(R.styleable.AudioWaveView_chunkWidth, chunkWidth)
            chunkSpacing = getDimensionPixelSize(R.styleable.AudioWaveView_chunkSpacing,
                    chunkSpacing)
            minChunkHeight = getDimensionPixelSize(R.styleable.AudioWaveView_minChunkHeight,
                    minChunkHeight)
            chunkRadius = getDimensionPixelSize(R.styleable.AudioWaveView_chunkRadius, chunkRadius)
            waveColor = getColor(R.styleable.AudioWaveView_waveColor, waveColor)
            progress = getFloat(R.styleable.AudioWaveView_progress, progress)
            isExpansionAnimated = getBoolean(R.styleable.AudioWaveView_animateExpansion,
                    isExpansionAnimated)
            recycle()
        }
    }

    interface OnSamplingListener {
        fun onComplete()
    }

//    interface OnProgressListener {
//        fun onStartTracking(progress: Float)
//        fun onStopTracking(progress: Float)
//        fun onProgressChanged(progress: Float, byUser: Boolean)
//    }

}