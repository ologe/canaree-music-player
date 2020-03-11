package dev.olog.presentation.model

import dev.olog.presentation.PresentationId
import java.io.File

data class DisplayableFile(
    override val type: Int,
    override val mediaId: PresentationId.Category,
    val title: String,
    val path: String?
) : BaseModel {

    fun isFile(): Boolean = path != null
    fun asFile(): File = File(path!!)

}