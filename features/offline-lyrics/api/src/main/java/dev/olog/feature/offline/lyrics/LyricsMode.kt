package dev.olog.feature.offline.lyrics

import android.text.Spannable

sealed class LyricsMode {
    class Normal(val lyrics: Spannable) : LyricsMode()
    class Synced(val lyrics: List<Pair<Long, Spannable>>) : LyricsMode()
}