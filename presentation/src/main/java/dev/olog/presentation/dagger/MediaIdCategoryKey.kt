package dev.olog.presentation.dagger

import dagger.MapKey
import dev.olog.core.MediaIdCategory

@MapKey
annotation class MediaIdCategoryKey(
        val value: MediaIdCategory
)
