package dev.olog.msc.presentation.utils

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import dev.olog.msc.R
import dev.olog.msc.utils.MediaIdCategory

class PlayingFromTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : AppCompatTextView(context, attrs) {

    fun setText(category: MediaIdCategory){
        if (category == MediaIdCategory.SONGS){
            setAllTracksText()
        } else {
            setDefaultText(category)
        }
    }

    private fun setAllTracksText(){
        val text = context.getString(R.string.playing_queue_playing_from_all_tracks)
        setText(text)
    }

    private fun setDefaultText(category: MediaIdCategory){
        val fromId = when (category){
            MediaIdCategory.FOLDERS -> R.string.common_folder
            MediaIdCategory.PLAYLISTS -> R.string.common_playlist
            MediaIdCategory.ALBUMS -> R.string.common_album
            MediaIdCategory.ARTISTS -> R.string.common_artist
            MediaIdCategory.GENRES -> R.string.common_genre
            else -> 0
        }

        val from = context.getString(fromId)
        val text = context.getString(R.string.playing_queue_playing_from_x, from)
        setText(text)
    }

}