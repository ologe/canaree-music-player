package dev.olog.msc.presentation.widget

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import dev.olog.msc.R
import dev.olog.msc.theme.AppTheme

class ShuffleButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : AppCompatImageButton(context, attrs) {

    private var enabledColor = ContextCompat.getColor(context, R.color.player_selected_button)
    private var shuffleMode = PlaybackStateCompat.SHUFFLE_MODE_NONE

    init {
        setImageResource(R.drawable.vd_shuffle)
    }

    fun cycle(state: Int){
        this.shuffleMode = state
        when (state){
            PlaybackStateCompat.SHUFFLE_MODE_NONE -> disable()
            else -> enable()
        }
    }

    fun updateColor(color: Int){
        this.enabledColor = color
        if (shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL){
            setColorFilter(this.enabledColor)
        }
    }

    private fun enable(){
        setColorFilter(enabledColor)
    }

    private fun disable(){
        val color = if (AppTheme.isFullscreen()) Color.WHITE
        else ContextCompat.getColor(context!!, R.color.button_primary_tint)
        setColorFilter(color)
    }

}