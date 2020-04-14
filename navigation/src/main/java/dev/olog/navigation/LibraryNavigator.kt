package dev.olog.navigation

import androidx.fragment.app.FragmentActivity

interface LibraryNavigator {

    fun toAlbums(activity: FragmentActivity)

    fun toArtists(activity: FragmentActivity)

    fun toFolders(activity: FragmentActivity)

    fun toGenres(activity: FragmentActivity)

}