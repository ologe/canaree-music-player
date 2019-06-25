package dev.olog.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.theme.QuickActionListener.Companion.quickAction
import dev.olog.presentation.theme.QuickActionListener.Companion.quickActionPublisher
import dev.olog.shared.extensions.toggleVisibility
import kotlinx.coroutines.*
import kotlin.properties.Delegates

class QuickActionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null

) : AppCompatImageView(context, attrs),
    View.OnClickListener,
    CoroutineScope by MainScope() {

    private var currentMediaId by Delegates.notNull<MediaId>()

    enum class Type {
        NONE, PLAY, SHUFFLE
    }

    private var job: Job? = null

    init {
        setImage()
        setBackgroundResource(R.drawable.background_quick_action)
    }

    private fun setImage() {
        toggleVisibility(quickAction() != Type.NONE, true)

        when (quickAction()) {
            Type.NONE -> setImageDrawable(null)
            Type.PLAY -> setImageResource(R.drawable.vd_play)
            Type.SHUFFLE -> setImageResource(R.drawable.vd_shuffle)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setOnClickListener(this)
        job = launch {
            for (type in quickActionPublisher.openSubscription()) {
                setImage()
            }
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
        val mediaProvider = context as MediaProvider
        when (quickAction()) {
            Type.PLAY -> mediaProvider.playFromMediaId(currentMediaId)
            Type.SHUFFLE -> mediaProvider.shuffle(currentMediaId)
            else -> {
            }
        }
    }
}