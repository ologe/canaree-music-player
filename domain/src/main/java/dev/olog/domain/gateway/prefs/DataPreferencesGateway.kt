package dev.olog.domain.gateway.prefs

import java.util.concurrent.atomic.AtomicBoolean

interface DataPreferencesGateway {

    fun areFolderImagesCreated() : AtomicBoolean
    fun arePlaylistImagesCreated() : AtomicBoolean
    fun areArtistImagesCreated() : AtomicBoolean
    fun areGenreImagesCreated() : AtomicBoolean

    fun setFolderImagesCreated()
    fun setPlaylistImagesCreated()
    fun setArtistImagesCreated()
    fun setGenreImagesCreated()

}