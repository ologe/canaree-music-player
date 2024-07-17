package dev.olog.shared.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.extensions.toggleVisibility
import dev.olog.shared.android.theme.HasQuickAction
import dev.olog.shared.android.theme.QuickAction
import dev.olog.shared.android.viewScope
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class QuickActionView (
        context: Context,
        attrs: AttributeSet

) : AppCompatImageView(context, attrs), View.OnClickListener {

    private var currentMediaId: MediaId? = null

    private val hasQuickAction by lazyFast { context.applicationContext.findInContext<HasQuickAction>() }

    init {
        setImage(if (isInEditMode) QuickAction.SHUFFLE else hasQuickAction.getQuickAction())
        setBackgroundResource(R.drawable.background_quick_action)
    }

    private fun setImage(quickAction: QuickAction) {
        toggleVisibility(quickAction != QuickAction.NONE, true)

        when (quickAction) {
            QuickAction.NONE -> setImageDrawable(null)
            QuickAction.PLAY -> setImageResource(R.drawable.vd_play)
            QuickAction.SHUFFLE -> setImageResource(R.drawable.vd_shuffle)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) {
            return
        }
        setOnClickListener(this)
        viewScope.launch {
            hasQuickAction.observeQuickAction()
                .collectLatest { action ->
                    setImage(action)
                }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setOnClickListener(null)
    }

    fun setId(mediaId: MediaId) {
        this.currentMediaId = mediaId
    }

    override fun onClick(v: View?) {
        val mediaId = currentMediaId ?: return
        val mediaProvider = context.findInContext<MediaProvider>()
        when (hasQuickAction.getQuickAction()) {
            QuickAction.PLAY -> mediaProvider.playFromMediaId(mediaId, null, null)
            QuickAction.SHUFFLE -> mediaProvider.shuffle(mediaId, null)
            QuickAction.NONE -> {
            }
        }
    }
}