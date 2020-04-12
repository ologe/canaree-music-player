package dev.olog.feature.settings.model

import android.os.Environment
import dev.olog.feature.presentation.base.model.BaseModel
import dev.olog.feature.presentation.base.model.PresentationId
import timber.log.Timber

internal data class BlacklistModel(
    override val type: Int,
    override val mediaId: PresentationId.Category,
    val title: String,
    val path: String,
    var isBlacklisted: Boolean
) : BaseModel {

    companion object {
        @Suppress("DEPRECATION")
        @JvmStatic
        private val defaultStorageDir = Environment.getExternalStorageDirectory().path ?: "/storage/emulated/0/"
    }

    // show the path without "/storage/emulated/0"
    val displayablePath : String
        get() {
            return try {
                path.substring(defaultStorageDir.length)
            } catch (ex: StringIndexOutOfBoundsException){
                Timber.e(ex)
                path
            }
        }

}