package dev.olog.msc.dagger.qualifier

import dagger.MapKey
import dev.olog.core.MediaIdCategory

@MapKey
annotation class MediaIdCategoryKey(
        val value: MediaIdCategory
)
