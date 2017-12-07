package dev.olog.data

import android.content.res.Resources

object DataConstants {

    const val UNKNOWN = "<unknown>"
    lateinit var UNKNOWN_ALBUM: String
    lateinit var UNKNOWN_ARTIST: String

    fun init(resources: Resources){
        UNKNOWN_ALBUM = resources.getString(R.string.unknown_album)
        UNKNOWN_ARTIST = resources.getString(R.string.unknown_artist)
    }

}