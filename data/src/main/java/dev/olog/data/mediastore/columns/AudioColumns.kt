package dev.olog.data.mediastore.columns

import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi

// copy of AudioColumns with sdk level enforcement
object AudioColumns {

    const val _ID = MediaStore.Audio.AudioColumns._ID
    const val ALBUM = MediaStore.Audio.AudioColumns.ALBUM
    const val ALBUM_ARTIST = MediaStore.Audio.AudioColumns.ALBUM_ARTIST
    const val ARTIST = MediaStore.Audio.AudioColumns.ARTIST
    @RequiresApi(Build.VERSION_CODES.Q)
    const val BUCKET_DISPLAY_NAME = MediaStore.Audio.AudioColumns.BUCKET_DISPLAY_NAME
    @RequiresApi(Build.VERSION_CODES.Q)
    const val BUCKET_ID = MediaStore.Audio.AudioColumns.BUCKET_ID
    const val DATA = MediaStore.Audio.AudioColumns.DATA
    const val DATE_ADDED = MediaStore.Audio.AudioColumns.DATE_ADDED
    const val DISPLAY_NAME = MediaStore.Audio.AudioColumns.DISPLAY_NAME
    const val DURATION = MediaStore.Audio.AudioColumns.DURATION
    @RequiresApi(Build.VERSION_CODES.Q)
    const val RELATIVE_PATH = MediaStore.Audio.AudioColumns.RELATIVE_PATH
    const val TITLE = MediaStore.Audio.AudioColumns.TITLE
    const val YEAR = MediaStore.Audio.AudioColumns.YEAR
    const val ALBUM_ID = MediaStore.Audio.AudioColumns.ALBUM_ID
    const val ARTIST_ID = MediaStore.Audio.AudioColumns.ARTIST_ID
    const val IS_PODCAST = MediaStore.Audio.AudioColumns.IS_PODCAST
    const val TRACK = MediaStore.Audio.AudioColumns.TRACK

    @RequiresApi(Build.VERSION_CODES.R)
    const val GENRE_ID = MediaStore.Audio.AudioColumns.GENRE_ID
    @RequiresApi(Build.VERSION_CODES.R)
    const val GENRE = MediaStore.Audio.AudioColumns.GENRE

    const val IS_ALARM = MediaStore.Audio.AudioColumns.IS_ALARM
    @RequiresApi(Build.VERSION_CODES.Q)
    const val IS_AUDIOBOOK = MediaStore.Audio.AudioColumns.IS_AUDIOBOOK
    const val IS_NOTIFICATION = MediaStore.Audio.AudioColumns.IS_NOTIFICATION
    @RequiresApi(Build.VERSION_CODES.S)
    const val IS_RECORDING = MediaStore.Audio.AudioColumns.IS_RECORDING
    const val IS_RINGTONE = MediaStore.Audio.AudioColumns.IS_RINGTONE

}