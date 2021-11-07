package dev.olog.feature.base.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import dev.olog.core.MediaId
import dev.olog.feature.base.R
import dev.olog.media.mediaProvider
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.theme.HasQuickAction
import dev.olog.shared.android.theme.QuickAction
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class QuickActionView (
        context: Context,
        attrs: AttributeSet

) : AppCompatImageView(context, attrs), View.OnClickListener {

    private var currentMediaId by Delegates.notNull<MediaId>()

    private var job: Job? = null

    private val hasQuickAction = context.applicationContext.findInContext<HasQuickAction>()

    init {
        setImage(hasQuickAction.getQuickAction())
        setBackgroundResource(R.drawable.background_quick_action)
    }

    private fun setImage(quickAction: QuickAction) {
        isVisible = quickAction != QuickAction.NONE
        when (quickAction) {
            QuickAction.NONE -> setImageDrawable(null)
            QuickAction.PLAY -> setImageResource(dev.olog.shared.android.R.drawable.vd_play)
            QuickAction.SHUFFLE -> setImageResource(dev.olog.shared.android.R.drawable.vd_shuffle)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setOnClickListener(this)
        job = GlobalScope.launch {
            hasQuickAction.observeQuickAction()
                .collect { setImage(it) }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setOnClickListener(null)
        job?.cancel()
    }

    fun setId(mediaId: MediaId) {
        this.currentMediaId = mediaId
    }

    override fun onClick(v: View?) {
        when (hasQuickAction.getQuickAction()) {
            QuickAction.PLAY -> mediaProvider.playFromMediaId(currentMediaId, null, null)
            QuickAction.SHUFFLE -> mediaProvider.shuffle(currentMediaId, null)
            QuickAction.NONE -> {
            }
        }
    }
}