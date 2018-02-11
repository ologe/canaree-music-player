package dev.olog.msc.presentation.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import dev.olog.msc.R

class RepeatButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : AppCompatImageButton(context, attrs) {

    init {
        setImageResource(R.drawable.vd_repeat)
    }

    fun cycle(state: Int){
        when (state){
            PlaybackStateCompat.REPEAT_MODE_NONE -> repeatNone()
            PlaybackStateCompat.REPEAT_MODE_ONE -> repeatOne()
            PlaybackStateCompat.REPEAT_MODE_ALL -> repeatAll()
        }
    }

    private fun repeatNone(){
        setImageResource(R.drawable.vd_repeat)
        setColorFilter(ContextCompat.getColor(context, R.color.button_primary_tint))
    }

    private fun repeatOne(){
        setImageResource(R.drawable.vd_repeat_one)
        setColorFilter(ContextCompat.getColor(context, R.color.item_selected))
    }

    private fun repeatAll(){
        setImageResource(R.drawable.vd_repeat)
        setColorFilter(ContextCompat.getColor(context, R.color.item_selected))
    }

}