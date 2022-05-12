package dev.olog.ui.model

import dev.olog.core.MediaId
import dev.olog.platform.adapter.BaseModel
import java.io.File

data class DisplayableFile(
    override val type: Int,
    override val mediaId: MediaId,
    val title: String,
    val path: String?
) : BaseModel {

    fun isFile(): Boolean = path != null
    fun asFile(): File = File(path!!)

}