package dev.olog.msc.presentation.widget

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import dev.olog.msc.R
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.presentation.utils.images.ColorUtil
import dev.olog.msc.utils.k.extension.textColorSecondary
import dev.olog.msc.utils.k.extension.textColorTertiary

class RepeatButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : AppCompatImageButton(context, attrs) {

    private val defaultEnabledColor: Int
    private var enabledColor: Int
    private var repeatMode = PlaybackStateCompat.REPEAT_MODE_NONE

    init {
        setImageResource(R.drawable.vd_repeat)

        defaultEnabledColor = if (AppTheme.isDarkTheme()){
            ContextCompat.getColor(context, R.color.accent_secondary)
        } else {
            ContextCompat.getColor(context, R.color.accent)
        }
        enabledColor = defaultEnabledColor
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
        val colorLuminance = ColorUtil.calculateLuminance(color)
        if (colorLuminance > .7 || colorLuminance < .3){
            this.enabledColor = defaultEnabledColor
        } else {
            this.enabledColor = color
        }

        if (repeatMode != PlaybackStateCompat.REPEAT_MODE_NONE){
            setColorFilter(this.enabledColor)
        }
    }

    private fun repeatNone(){
        setImageResource(R.drawable.vd_repeat)

        val color = when {
            AppTheme.isFullscreen() -> Color.WHITE
            AppTheme.isDarkTheme() -> {
                alpha = .7f
                textColorSecondary()
            }
            else -> textColorTertiary()
        }
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