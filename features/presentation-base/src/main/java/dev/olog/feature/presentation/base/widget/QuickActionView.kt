package dev.olog.feature.presentation.base.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import dev.olog.feature.presentation.base.R
import dev.olog.feature.presentation.base.extensions.mediaProvider
import dev.olog.lib.media.MediaProvider
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.toDomain
import dev.olog.shared.android.theme.QuickAction
import dev.olog.shared.android.theme.themeManager
import kotlin.properties.Delegates

class QuickActionView(
    context: Context,
    attrs: AttributeSet

) : AppCompatImageView(context, attrs), View.OnClickListener {

    private var currentMediaId by Delegates.notNull<PresentationId.Category>()

    init {
        setImage()
        setBackgroundResource(R.drawable.background_quick_action)
    }

    private fun setImage() {
        if (isInEditMode) {
            setImageResource(R.drawable.vd_play)
            return
        }
        val quickAction = context.themeManager.quickAction
        isVisible = quickAction != QuickAction.NONE

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
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setOnClickListener(null)
    }

    fun setId(mediaId: PresentationId.Category) {
        this.currentMediaId = mediaId
    }

    override fun onClick(v: View?) {
        when (context.themeManager.quickAction) {
            QuickAction.PLAY -> mediaProvider.playFromMediaId(currentMediaId.toDomain(), null, null)
            QuickAction.SHUFFLE -> mediaProvider.shuffle(currentMediaId.toDomain(), null)
            QuickAction.NONE -> {
            }
        }
    }
}