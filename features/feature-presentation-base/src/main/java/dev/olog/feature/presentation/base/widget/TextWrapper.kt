package dev.olog.feature.presentation.base.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import dev.olog.core.coroutines.viewScope
import dev.olog.feature.presentation.base.R
import dev.olog.shared.coroutines.autoDisposeJob
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
        job = viewScope.launchWhenAttached {
            updateInternal(title, artist)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job = null
    }

    private suspend fun updateInternal(title: String, artist: String) {
        TransitionManager.endTransitions(this)
        TransitionManager.beginDelayedTransition(this,
            TextUpdateTransition
        )

        titleView.isSelected = false
        artistView.isSelected = false

        titleView.text = title
        artistView.text = artist

        delay(TextUpdateTransition.DURATION * 2)
        titleView.isSelected = true
        artistView.isSelected = true
    }

}

private object TextUpdateTransition : TransitionSet() {

    const val DURATION = 250L

    init {
        ordering = ORDERING_SEQUENTIAL
        duration =
            DURATION
        addTransition(Fade(Fade.OUT))
        addTransition(ChangeBounds())
        addTransition(Fade(Fade.IN))
    }

}