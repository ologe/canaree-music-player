package dev.olog.presentation.model

import android.support.v4.media.MediaMetadataCompat
import dev.olog.shared.MediaIdHelper

class CoverTempModel {

    val uri : String
    val id : String

    constructor(metadataCompat: MediaMetadataCompat){
        uri = metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
        id = MediaIdHelper.extractLeaf(metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))
    }

    constructor(uri: String, id: String) {
        this.uri = uri
        this.id = id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CoverTempModel

        if (uri != other.uri) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uri.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }


}