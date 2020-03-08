package dev.olog.offlinelyrics

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.olog.shared.autoDisposeJob
import dev.olog.shared.lazyFast
import kotlinx.coroutines.*
import kotlinx.coroutines.android.awaitFrame
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class OfflineLyricsRecyclerView(
    context: Context,
    attrs: AttributeSet
) : RecyclerView(context, attrs) {

    companion object {
        private val TIME = TimeUnit.SECONDS.toMillis(4)
    }

    private var isControlledByUser: Boolean = false

    private var job by autoDisposeJob()
    private var firstScrollJob by autoDisposeJob()

    private var downX = -1f
    private var downY = -1f

    private val configuration = ViewConfiguration.get(context)

    var onTap: (() -> Unit)? = null

    init {
        itemAnimator!!.changeDuration = 100
    }

    private val layoutManagerInternal = LinearLayoutManager(context)
    private val adapterInternal by lazyFast {
        OfflineLyricsAdapter { pos ->
            if (!isControlledByUser) {
                scroll(pos)
            }
        }
    }

    init {
        layoutManager = layoutManagerInternal
        adapter = adapterInternal
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
            }
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                val xUp = event.x
                val yUp = event.y
                if (abs(xUp - downX) < configuration.scaledTouchSlop &&
                    abs(yUp - downY) < configuration.scaledTouchSlop
                ) {
                    onTap?.invoke()
                    return false
                } else {
                    setUserControl()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun setUserControl() {
        isControlledByUser = true
        job = GlobalScope.launch(Dispatchers.Main) {
            delay(TIME)
            isControlledByUser = false
            scrollToCurrent()
        }
    }

    suspend fun scrollToCurrent() {
        firstScrollJob = GlobalScope.launch(Dispatchers.Main) {
            while (adapter.selectedIndex == NO_POSITION) {
                awaitFrame()
            }
            scroll(adapter.selectedIndex)
        }
    }

    override fun getLayoutManager(): LinearLayoutManager {
        return super.getLayoutManager() as LinearLayoutManager
    }

    override fun getAdapter(): OfflineLyricsAdapter {
        return super.getAdapter() as OfflineLyricsAdapter
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job = null
        firstScrollJob = null
    }

    private fun scroll(position: Int) {
        val list = this
        val vh = findViewHolderForAdapterPosition(position)
        if (vh != null ){
            vh.itemView.doOnLayout {
                val offset = it.height
                layoutManager.scrollToPositionWithOffset(position, list.height / 2 - offset - list.paddingTop)
            }
        } else {
            layoutManager.scrollToPositionWithOffset(position, list.height / 2 - list.paddingTop)
        }
    }

}