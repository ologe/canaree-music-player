package dev.olog.feature.base.adapter.media

import android.content.Context
import androidx.annotation.Px
import dev.olog.shared.android.extensions.dimen

sealed class ItemDirection {
    object Vertical : ItemDirection()

    data class Horizontal private constructor(@Px val size: Int) : ItemDirection() {
        constructor(context: Context) : this (context.dimen(dev.olog.shared.android.R.dimen.item_tab_album_last_player_width))
    }

}