package dev.olog.data.playlist.persister

import java.io.File

/**
 * Custom implementation of https://cs.android.com/android/platform/superproject/+/master:packages/providers/MediaProvider/src/com/android/providers/media/playlist/M3uPlaylistPersister.java
 */
class M3uPlaylistPersister : PlaylistPersister {

    override fun read(file: File): List<String> {
        return file.readLines()
            .filter { it.isNotBlank() && !it.startsWith("#") }
            .map { it.replace('\\', '/') }
    }

    override fun write(file: File, items: List<String>) {
        file.printWriter().use { printer ->
            printer.println("#EXTM3U")
            items.forEach { printer.println(it) }
        }
    }
}