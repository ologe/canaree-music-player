package dev.olog.msc.presentation.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import dev.olog.msc.R
import dev.olog.msc.theme.AppTheme
import dev.olog.msc.utils.k.extension.textColorPrimary
import dev.olog.msc.utils.k.extension.textColorSecondary
import dev.olog.msc.utils.k.extension.textColorTertiary

class RepeatButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : AppCompatImageButton(context, attrs) {

    private var enabledColor = ContextCompat.getColor(context, R.color.player_selected_button)
    private var repeatMode = PlaybackStateCompat.REPEAT_MODE_NONE

    init {
        setImageResource(R.drawable.vd_repeat)

        if (AppTheme.isDarkTheme()){
            enabledColor = textColorPrimary()
        }
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

        val color = if (AppTheme.isDarkTheme()){
            alpha = .7f
            textColorSecondary()
        } else textColorTertiary()
        setColorFilter(color)
    }

    private fun repeatOne(){
        alpha = 1f
        setImageResource(R.drawable.vd_repeat_one)
        setColorFilter(enabledColor)
    }

    private fun repeatAll(){
        alpha = 1f
        setImageResource(R.drawable.vd_repeat)
        setColorFilter(enabledColor)
    }

}