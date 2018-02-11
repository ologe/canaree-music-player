package dev.olog.msc.presentation.widget

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.toggleVisibility

class QuickActionView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0

) : AppCompatImageView(context, attrs, defStyleAttr), View.OnClickListener {

    private var currentMediaId: MediaId? = null

    enum class Type {
        NONE, PLAY, SHUFFLE
    }

    init {
        setupImage()
        setBackgroundResource(R.drawable.faded_circle)
    }

    private fun setupImage(){
        toggleVisibility(AppConstants.QUICK_ACTION != Type.NONE)

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
        currentMediaId?.let {
            val mediaProvider = context as MediaProvider
            when (AppConstants.QUICK_ACTION){
                Type.PLAY -> mediaProvider.playFromMediaId(currentMediaId!!)
                Type.SHUFFLE -> mediaProvider.shuffle(currentMediaId!!)
                else -> {}
            }
        }
    }
}