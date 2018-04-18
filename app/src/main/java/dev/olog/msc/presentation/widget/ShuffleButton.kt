package dev.olog.msc.presentation.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import dev.olog.msc.R

class ShuffleButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : AppCompatImageButton(context, attrs) {

    init {
        setImageResource(R.drawable.vd_shuffle)
    }

    fun cycle(state: Int){
        when (state){
            PlaybackStateCompat.SHUFFLE_MODE_NONE -> disable()
            else -> enable()
        }
    }

    private fun enable(){
        setColorFilter(ContextCompat.getColor(context!!, R.color.player_selected_button))
    }

    private fun disable(){
        setColorFilter(ContextCompat.getColor(context!!, R.color.button_primary_tint))
    }

}