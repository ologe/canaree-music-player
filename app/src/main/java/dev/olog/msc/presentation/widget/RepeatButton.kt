package dev.olog.msc.presentation.widget

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants

class RepeatButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : AppCompatImageButton(context, attrs) {

    private var enabledColor = ContextCompat.getColor(context, R.color.player_selected_button)
    private var repeatMode = PlaybackStateCompat.REPEAT_MODE_NONE

    init {
        setImageResource(R.drawable.vd_repeat)
    }

    fun cycle(state: Int){
        this.repeatMode = state
        when (state){
            PlaybackStateCompat.REPEAT_MODE_NONE -> repeatNone()
            PlaybackStateCompat.REPEAT_MODE_ONE -> repeatOne()
            PlaybackStateCompat.REPEAT_MODE_ALL -> repeatAll()
        }
    }

    fun updateColor(color: Int){
        this.enabledColor = color
        if (repeatMode != PlaybackStateCompat.REPEAT_MODE_NONE){
            setColorFilter(this.enabledColor)
        }
    }

    private fun repeatNone(){
        setImageResource(R.drawable.vd_repeat)
        val color = if (AppConstants.THEME.isFullscreen()) Color.WHITE
        else ContextCompat.getColor(context!!, R.color.button_primary_tint)
        setColorFilter(color)
    }

    private fun repeatOne(){
        setImageResource(R.drawable.vd_repeat_one)
        setColorFilter(enabledColor)
    }

    private fun repeatAll(){
        setImageResource(R.drawable.vd_repeat)
        setColorFilter(enabledColor)
    }

}