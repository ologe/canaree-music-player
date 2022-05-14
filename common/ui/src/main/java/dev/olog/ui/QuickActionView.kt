package dev.olog.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import dev.olog.core.MediaId
import dev.olog.feature.media.api.MediaProvider
import dev.olog.platform.theme.HasQuickAction
import dev.olog.platform.theme.QuickAction
import dev.olog.shared.extension.coroutineScope
import dev.olog.shared.extension.findInContext
import dev.olog.shared.extension.lazyFast
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class QuickActionView (
        context: Context,
        attrs: AttributeSet

) : AppCompatImageView(context, attrs),
    View.OnClickListener {

    private var currentMediaId by Delegates.notNull<MediaId>()

    private val hasQuickAction by lazyFast { context.applicationContext.findInContext<HasQuickAction>() }

    init {
        setImage()
        setBackgroundResource(R.drawable.background_quick_action)
    }

    private fun setImage() {
        val quickAction = hasQuickAction.getQuickAction()
        isVisible = quickAction != QuickAction.NONE

        when (quickAction) {
            QuickAction.NONE -> setImageDrawable(null)
            QuickAction.PLAY -> setImageResource(R.drawable.vd_play)
            QuickAction.SHUFFLE -> setImageResource(R.drawable.vd_shuffle)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setOnClickListener(this)
        coroutineScope.launch {
            for (type in hasQuickAction.observeQuickAction()) {
                setImage()
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
        val mediaProvider = context.findInContext<MediaProvider>()
        when (hasQuickAction.getQuickAction()) {
            QuickAction.PLAY -> mediaProvider.playFromMediaId(currentMediaId, null, null)
            QuickAction.SHUFFLE -> mediaProvider.shuffle(currentMediaId, null)
            QuickAction.NONE -> {
            }
        }
    }
}