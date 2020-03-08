package dev.olog.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.transition.TransitionManager
import dev.olog.presentation.R
import dev.olog.presentation.utils.TextUpdateTransition
import dev.olog.shared.android.extensions.launchWhenResumed
import dev.olog.shared.autoDisposeJob
import dev.olog.shared.lazyFast
import kotlinx.coroutines.delay

// linear layout wrapper (other viewgroup not working) is mandatory to avoid autoscroll collision with seekbar
class TextWrapper(
    context: Context,
    attrs: AttributeSet
) : LinearLayout(context, attrs) {

    private val titleView by lazyFast { findViewById<TextView>(R.id.title) }
    private val artistView by lazyFast { findViewById<TextView>(R.id.artist) }

    private var job by autoDisposeJob()

    init {
        orientation = VERTICAL
    }

    fun update(title: String, artist: String) {
        job = launchWhenResumed {
            updateInternal(title, artist)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job = null
    }

    private suspend fun updateInternal(title: String, artist: String) {
        TransitionManager.endTransitions(this)
        TransitionManager.beginDelayedTransition(this, TextUpdateTransition)

        titleView.isSelected = false
        artistView.isSelected = false

        titleView.text = title
        artistView.text = artist

        delay(TextUpdateTransition.DURATION * 2)
        titleView.isSelected = true
        artistView.isSelected = true
    }

}