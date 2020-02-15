package dev.olog.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.shared.android.extensions.lifecycleScope
import dev.olog.shared.android.extensions.toggleVisibility
import dev.olog.shared.android.theme.HasQuickAction
import dev.olog.shared.android.theme.QuickAction
import dev.olog.shared.autoDisposeJob
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.properties.Delegates

class QuickActionView(
    context: Context,
    attrs: AttributeSet

) : AppCompatImageView(context, attrs), View.OnClickListener {

    private var currentMediaId by Delegates.notNull<MediaId>()

    private var job by autoDisposeJob()

    private val hasQuickAction by lazyFast { context.applicationContext as HasQuickAction }

    init {
        setImage()
        setBackgroundResource(R.drawable.background_quick_action)
    }

    private fun setImage() {
        val quickAction = hasQuickAction.getQuickAction()
        toggleVisibility(quickAction != QuickAction.NONE, true)

        when (quickAction) {
            QuickAction.NONE -> setImageDrawable(null)
            QuickAction.PLAY -> setImageResource(R.drawable.vd_play)
            QuickAction.SHUFFLE -> setImageResource(R.drawable.vd_shuffle)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setOnClickListener(this)

        job = hasQuickAction.observeQuickAction()
            .consumeAsFlow()
            .onEach { setImage() }
            .launchIn(lifecycleScope)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setOnClickListener(null)
        job = null
    }

    fun setId(mediaId: MediaId) {
        this.currentMediaId = mediaId
    }

    override fun onClick(v: View?) {
        val mediaProvider = context as MediaProvider
        when (hasQuickAction.getQuickAction()) {
            QuickAction.PLAY -> mediaProvider.playFromMediaId(currentMediaId, null, null)
            QuickAction.SHUFFLE -> mediaProvider.shuffle(currentMediaId, null)
            QuickAction.NONE -> {
            }
        }
    }
}