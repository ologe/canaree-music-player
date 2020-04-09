package dev.olog.feature.library.model

import dev.olog.feature.presentation.base.model.BaseModel
import dev.olog.feature.presentation.base.model.PresentationId
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