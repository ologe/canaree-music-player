package dev.olog.data.mediastore

import android.net.Uri
import android.provider.MediaStore
import dev.olog.platform.BuildVersion

// todo use VOLUME_EXTERNAL_PRIMARY OR VOLUME_EXTERNAL ???
// TODO scan mediastore after updating
//   see https://github.com/google/modernstorage/blob/1.0.0-alpha03/mediastore/src/main/java/com/google/modernstorage/mediastore/MediaStoreRepository.kt
object MediaStoreUris {

    val audio: Uri = when {
        BuildVersion.isQ() -> MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        else -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    val genres: Uri = when {
        BuildVersion.isQ() -> MediaStore.Audio.Genres.getContentUri(MediaStore.VOLUME_EXTERNAL)
        else -> MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI
    }

    fun genreTracks(id: Long): Uri = when {
        BuildVersion.isQ() -> MediaStore.Audio.Genres.Members.getContentUri(MediaStore.VOLUME_EXTERNAL, id)
        // TODO make sure it works
        else -> MediaStore.Audio.Genres.Members.getContentUri("external", id)
    }

    @Suppress("DEPRECATION")
    val playlists: Uri = when {
        BuildVersion.isQ() -> MediaStore.Audio.Playlists.getContentUri(MediaStore.VOLUME_EXTERNAL)
        else -> MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI
    }

    @Suppress("DEPRECATION")
    fun playlistTracks(id: Long): Uri = when {
        BuildVersion.isQ() -> MediaStore.Audio.Playlists.Members.getContentUri(MediaStore.VOLUME_EXTERNAL, id)
        // TODO make sure it works
        else -> MediaStore.Audio.Playlists.Members.getContentUri("external", id)
    }

    val files: Uri = when {
        BuildVersion.isQ() -> MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        else -> MediaStore.Files.getContentUri("external")
    }

}