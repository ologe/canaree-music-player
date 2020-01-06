package dev.olog.data.test

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteQueryBuilder

/**
 * Funky way to test media store
 */
internal class InMemoryContentProvider : ContentProvider() {

    companion object {
        private const val AUTHORITY = "canaree_media"
        const val AUDIO = "audio"
        const val GENRES = "genres"

        private val MATCHER = UriMatcher(UriMatcher.NO_MATCH)

        fun getContentUri(table: String): Uri {
            return Uri.parse("content://${AUTHORITY}/$table")
        }

        fun getContentUri(table: String, id: Long): Uri {
            return ContentUris.withAppendedId(getContentUri(table), id)
        }


        private const val CODE_ALL = 1
        private const val CODE_ITEM = 2

        init {
            MATCHER.addURI(AUTHORITY, AUDIO, CODE_ALL)
            MATCHER.addURI(AUTHORITY, "$AUDIO/*", CODE_ITEM)

            MATCHER.addURI(AUTHORITY, GENRES, CODE_ALL)
            MATCHER.addURI(AUTHORITY, "$GENRES/*", CODE_ITEM)
        }
    }

    private lateinit var room: MediaStoreDatabase

    override fun onCreate(): Boolean {
        room = Room.inMemoryDatabaseBuilder(context!!, MediaStoreDatabase::class.java)
            .build()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val code = MATCHER.match(uri)
        if (code == CODE_ALL) {
            return queryAll(uri, projection, selection, selectionArgs, sortOrder)
        }
        if (code == CODE_ITEM) {
            return queryItem(uri, projection, selection, selectionArgs, sortOrder)
        }
        throw IllegalArgumentException("Unknown URI: $uri")
    }

    private fun queryAll(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        val builder = SupportSQLiteQueryBuilder.builder(uri.path!!.substring(1))
        builder.columns(projection)
        selection?.let { builder.selection(selection, selectionArgs) }
        sortOrder?.let { builder.orderBy(sortOrder) }

        return room.mediaDao().rawQuery(builder.create())
    }

    private fun queryItem(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        val (table, id) = uri.path!!.substring(1).split("/")

        val builder = SupportSQLiteQueryBuilder.builder(table)
        builder.columns(projection)

        val sel = "_id = $id"
        if (selection == null) {
            builder.selection(sel, null)
        } else {
            builder.selection("$sel AND $selection", selectionArgs)
        }

        sortOrder?.let { builder.orderBy(sortOrder) }

        return room.mediaDao().rawQuery(builder.create())
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (values == null) {
            throw IllegalArgumentException("null values")
        }
        val code = MATCHER.match(uri)
        if (code == CODE_ITEM) {
            throw IllegalArgumentException("Unknown URI: $uri")
        }

        if (uri.path!!.contains(AUDIO)) {
            val item = MediaStoreTrack.fromContentValues(values)
            val id = room.mediaDao().insertTrack(item)
            return getContentUri(AUDIO, id)
        }

        if (uri.path!!.contains(GENRES)) {
            val item = MediaStoreGenre.fromContentValues(values)
            val id = room.mediaDao().insertGenre(item)
            return getContentUri(GENRES, id)
        }

        throw IllegalArgumentException("Unknown URI: $uri")
    }

    override fun bulkInsert(uri: Uri, values: Array<out ContentValues>): Int {
        if (values.isEmpty()) {
            throw IllegalArgumentException("null values")
        }
        val code = MATCHER.match(uri)
        if (code == CODE_ITEM) {
            throw IllegalArgumentException("Unknown URI: $uri")
        }

        if (uri.path!!.contains(AUDIO)) {
            val tracks = values.map { MediaStoreTrack.fromContentValues(it) }
            room.mediaDao().insertMultipleTrack(tracks)
            return 1
        }

        if (uri.path!!.contains(GENRES)) {
            val tracks = values.map { MediaStoreGenre.fromContentValues(it) }
            room.mediaDao().insertMultipleGenre(tracks)
            return 1
        }

        throw IllegalArgumentException("Unknown URI: $uri")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        if (MATCHER.match(uri) == CODE_ITEM){
            val (_, id) = uri.path!!.substring(1).split("/")
            if (uri.path!!.contains(AUDIO)) {
                room.mediaDao().deleteSingleTrack(id.toLong())
                return 1
            }
            if (uri.path!!.contains(GENRES)) {
                room.mediaDao().deleteSingleGenre(id.toLong())
                return 1
            }
        }
        if (uri.path!!.contains(AUDIO)) {
            room.mediaDao().deleteTracks()
            return 1
        }
        if (uri.path!!.contains(GENRES)) {
            room.mediaDao().deleteGenres()
            return 1
        }

        throw IllegalArgumentException("Unknown URI: $uri")
    }

    override fun getType(uri: Uri): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}