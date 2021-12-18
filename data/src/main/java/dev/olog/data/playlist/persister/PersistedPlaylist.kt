package dev.olog.data.playlist.persister

import dev.olog.core.entity.track.Playlist
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class PersistedPlaylist(file: File) {

    constructor(playlist: Playlist) : this(File(playlist.path))

    private val items = try {
        PlaylistPersister.resolvePersister(file)
            ?.read(file)
            .orEmpty()
            .toMutableList()
    } catch (ex: FileNotFoundException) {
        // not created yet
        mutableListOf()
    }

    fun asList(): List<String> = items.toList()

    fun write(file: File) {
        PlaylistPersister.resolvePersister(file)?.write(file, items)
    }

    fun add(path: String) {
        items.add(path)
    }

    fun addMultiple(paths: List<String>) {
        items.addAll(paths)
    }

    fun removeAt(position: Int) {
        items.removeAt(position.coerceIn(0, items.size - 1))
    }

    fun removeMultiple(path: List<String>) {
        items.removeAll(path)
    }

    fun clear() {
        items.clear()
    }

    fun move(from: Int, to: Int) {
        Collections.swap(
            items,
            from.coerceIn(0, items.size - 1),
            to.coerceIn(0, items.size - 1),
        )
    }

}