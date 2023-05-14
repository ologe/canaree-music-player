package dev.olog.data.playlist.persister

import android.content.ContentResolver
import androidx.documentfile.provider.DocumentFile
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Collections

internal class PersistablePlaylist(
    private val _items: MutableList<String> = mutableListOf()
) {

    val items: List<String>
        get() = _items.toList()

    fun read(file: DocumentFile, contentResolver: ContentResolver) {
        clear()
        val persister = PlaylistPersister.resolvePersister(file)
        val items = contentResolver.openFileDescriptor(file.uri, "r")?.use { pfd ->
            persister.read(FileInputStream(pfd.fileDescriptor))
        } ?: emptyList()
        _items.addAll(items)
    }

    fun write(file: DocumentFile, contentResolver: ContentResolver) {
        val persister = PlaylistPersister.resolvePersister(file)
        contentResolver.openFileDescriptor(file.uri, "wt")?.use { pfd ->
            persister.write(FileOutputStream(pfd.fileDescriptor), items)
        }
    }

    fun add(item: String) {
        _items.add(item)
    }

    fun addAll(items: List<String>) {
        _items.addAll(items)
    }

    /**
     * Add the given playlist item at the nearest valid index.
     */
    fun add(index: Int, item: String) {
        _items.add(constrain(index, 0, _items.size), item)
    }

    /**
     * Move an existing playlist item from the nearest valid index to the
     * nearest valid index.
     */
    fun move(from: Int, to: Int) {
        val constraintedFrom = constrain(from, 0, _items.lastIndex)
        val constraintedTo = constrain(to, 0, _items.lastIndex)
        Collections.swap(_items, constraintedFrom, constraintedTo)
    }

    /**
     * Remove an existing playlist item from the nearest valid index.
     */
    fun remove(index: Int) {
        _items.removeAt(constrain(index, 0, _items.size))
    }

    fun clear() {
        _items.clear()
    }

    private fun constrain(amount: Int, low: Int, high: Int): Int = when {
        amount < low -> low
        amount > high -> high
        else -> amount
    }

}