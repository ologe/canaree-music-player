package dev.olog.msc.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.core.MediaId
import dev.olog.msc.utils.k.extension.toggleVisibility
import kotlin.properties.Delegates

class QuickActionView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : AppCompatImageView(context, attrs), View.OnClickListener {

    private var currentMediaId by Delegates.notNull<MediaId>()

    enum class Type {
        NONE, PLAY, SHUFFLE
    }

    init {
        setupImage()
        setBackgroundResource(R.drawable.background_quick_action)
    }

    private fun setupImage(){
        toggleVisibility(AppConstants.QUICK_ACTION != Type.NONE, true)

        when (AppConstants.QUICK_ACTION){
            Type.NONE -> setImageDrawable(null)
            Type.PLAY -> setImageResource(R.drawable.vd_play)
            Type.SHUFFLE -> setImageResource(R.drawable.vd_shuffle)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setOnClickListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setOnClickListener(null)
    }

    fun setId(mediaId: MediaId){
        this.currentMediaId = mediaId
    }

    override fun onClick(v: View?) {
        val mediaProvider = context as MediaProvider
        when (AppConstants.QUICK_ACTION){
            Type.PLAY -> mediaProvider.playFromMediaId(currentMediaId)
            Type.SHUFFLE -> mediaProvider.shuffle(currentMediaId)
            else -> {}
        }
    }
}