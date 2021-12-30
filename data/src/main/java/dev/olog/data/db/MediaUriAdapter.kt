package dev.olog.data.db

import com.squareup.sqldelight.ColumnAdapter
import dev.olog.core.MediaUri

internal object MediaUriAdapter : ColumnAdapter<MediaUri, String> {

    override fun decode(databaseValue: String): MediaUri {
        return MediaUri(databaseValue)
    }

    override fun encode(value: MediaUri): String {
        return value.toString()
    }
}