package dev.olog.feature.library.sample

import androidx.ui.tooling.preview.datasource.LoremIpsum
import dev.olog.feature.library.folder.FolderFragmentModel
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.PresentationIdCategory
import kotlin.random.Random

internal object FolderModelProvider {

    val hierarchy = listOf(
        FolderFragmentModel.Header("Folders"),
        *(0..2).map {
            val mediaId = PresentationId.Category(PresentationIdCategory.FOLDERS, it.toString())
            val title = LoremIpsum(Random.nextInt(3, 10)).values.joinToString()
            val subtitle = LoremIpsum(Random.nextInt(3, 10)).values.joinToString()
            FolderFragmentModel.Folder(mediaId, title, subtitle)
        }.toTypedArray(),
        FolderFragmentModel.Header("Tracks"),
        *(0..2).map {
            val mediaId = PresentationId.Category(PresentationIdCategory.FOLDERS, it.toString())
            val title = LoremIpsum(Random.nextInt(3, 10)).values.joinToString()
            val subtitle = LoremIpsum(Random.nextInt(3, 10)).values.joinToString()
            FolderFragmentModel.File(mediaId, title, subtitle)
        }.toTypedArray(),
    )

    val default = (0..10).map {
        val mediaId = PresentationId.Category(PresentationIdCategory.FOLDERS, it.toString())
        val title = LoremIpsum(Random.nextInt(3, 10)).values.joinToString()
        val subtitle = LoremIpsum(Random.nextInt(3, 10)).values.joinToString()
        FolderFragmentModel.Album(mediaId, title, subtitle)
    }

}