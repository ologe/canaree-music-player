package dev.olog.msc.presentation.library.tab

import dev.olog.core.MediaIdCategory

object TabHorizontalAdapters {

    fun getLastPlayedAlbums(fragment: TabFragment): TabFragmentLastPlayedAlbumsAdapter? {
        val category = fragment.category
        if (category == MediaIdCategory.ALBUMS || category == MediaIdCategory.PODCASTS_ALBUMS){
            return TabFragmentLastPlayedAlbumsAdapter(fragment.lifecycle, fragment.navigator)
        }
        return null
    }

    fun getLastPlayedArtists(fragment: TabFragment): TabFragmentLastPlayedArtistsAdapter? {
        val category = fragment.category
        if (category == MediaIdCategory.ARTISTS || category == MediaIdCategory.PODCASTS_ARTISTS){
            return TabFragmentLastPlayedArtistsAdapter(fragment.lifecycle, fragment.navigator)
        }
        return null
    }

    fun getNewAlbums(fragment: TabFragment): TabFragmentNewAlbumsAdapter? {
        val category = fragment.category
        if (category == MediaIdCategory.ALBUMS || category == MediaIdCategory.PODCASTS_ALBUMS){
            return TabFragmentNewAlbumsAdapter(fragment.lifecycle, fragment.navigator)
        }
        return null
    }

    fun getNewArtists(fragment: TabFragment): TabFragmentNewArtistsAdapter? {
        val category = fragment.category
        if (category == MediaIdCategory.ARTISTS || category == MediaIdCategory.PODCASTS_ARTISTS){
            return TabFragmentNewArtistsAdapter(fragment.lifecycle, fragment.navigator)
        }
        return null
    }

}