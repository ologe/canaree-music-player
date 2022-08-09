package dev.olog.data

import dev.olog.core.gateway.base.HasLastPlayed

object DataConstants {

    // two weeks in seconds
    const val RECENTLY_ADDED_PERIOD_IN_SECONDS = 1209600L
    const val MAX_LAST_PLAYED = HasLastPlayed.MAX_ITEM_TO_SHOW
    const val MIN_MOST_PLAYED_TIMES = 5
    const val MAX_MOST_PLAYED_ITEMS = 10

}