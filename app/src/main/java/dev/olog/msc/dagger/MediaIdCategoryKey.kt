package dev.olog.msc.dagger

import dagger.MapKey
import dev.olog.msc.utils.MediaIdCategory

@MapKey
annotation class MediaIdCategoryKey(
        val value: MediaIdCategory
)
