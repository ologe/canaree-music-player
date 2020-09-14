package dev.olog.shared.components.sample

import androidx.ui.tooling.preview.PreviewParameterProvider
import dev.olog.domain.MediaIdCategory
import dev.olog.shared.components.ambient.ImageShape
import dev.olog.shared.components.ambient.QuickAction

class DarkModePreviewProviders : PreviewParameterProvider<Boolean> {

    override val values: Sequence<Boolean>
        get() = sequenceOf(false, true)
}

class ImageShapePreviewProvider : PreviewParameterProvider<ImageShape> {

    override val values: Sequence<ImageShape>
        get() = sequenceOf(*ImageShape.values())
}

class QuickActionPreviewProvider : PreviewParameterProvider<QuickAction> {

    override val values: Sequence<QuickAction>
        get() = sequenceOf(*QuickAction.values())
}

class MediaIdCategoryPreviewProvider : PreviewParameterProvider<MediaIdCategory> {

    override val values: Sequence<MediaIdCategory>
        get() = sequenceOf(
            MediaIdCategory.FOLDERS,
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.ALBUMS,
            MediaIdCategory.ARTISTS,
            MediaIdCategory.GENRES,
        )
}