package dev.olog.feature.base.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import dev.olog.core.mediaid.MediaId
import dev.olog.feature.base.R
import dev.olog.lib.media.mediaProvider
import dev.olog.shared.android.coroutine.viewScope
import dev.olog.shared.android.extensions.findActivity
import dev.olog.shared.android.theme.QuickAction
import dev.olog.shared.android.theme.quickActionAmbient
import dev.olog.shared.exhaustive
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

// TODO refacotor, not the right module, here just because needs a reference to MediaId
class QuickActionView (
    context: Context,
    attrs: AttributeSet
) : AppCompatImageView(context, attrs),
    View.OnClickListener{

    private lateinit var currentMediaId: MediaId

    init {
        setBackgroundResource(R.drawable.background_quick_action)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) {
            setImageResource(R.drawable.vd_play)
            return
        }
        setOnClickListener(this)
        context.quickActionAmbient.flow
            .onEach(this::setImage)
            .launchIn(viewScope)
    }

    private fun setImage(quickAction: QuickAction) {
        isVisible = quickAction != QuickAction.NONE

        when (quickAction) {
            QuickAction.NONE -> setImageDrawable(null)
            QuickAction.PLAY -> setImageResource(R.drawable.vd_play)
            QuickAction.SHUFFLE -> setImageResource(R.drawable.vd_shuffle)
        }.exhaustive
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setOnClickListener(null)
    }

    fun setId(mediaId: MediaId) {
        this.currentMediaId = mediaId
    }

    override fun onClick(v: View?) {
        val mediaProvider = findActivity().mediaProvider
        val ambient = context.quickActionAmbient
        when (ambient.value) {
            QuickAction.PLAY -> mediaProvider.playFromMediaId(currentMediaId, null, null)
            QuickAction.SHUFFLE -> mediaProvider.shuffle(currentMediaId, null)
            QuickAction.NONE -> {
            }
        }
    }
}