package dev.olog.data

import kotlin.time.Duration.Companion.days

object QueriesConstants {

    // needed to compare with sql strftime('%s','now')
    fun unitTimestamp(): Long = System.currentTimeMillis() / 1000L

    val recentlyAddedMaxTime: Long = 14.days.inWholeSeconds

}