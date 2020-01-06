package dev.olog.core.entity.favorite

import org.junit.Assert.assertEquals
import org.junit.Test

class FavoriteStateTest {

    @Test
    fun shouldBecameFavorite() {
        assertEquals(
            FavoriteState.FAVORITE,
            FavoriteState.NOT_FAVORITE.reverse()
        )
    }

    @Test
    fun shouldBecameNotFavorite() {
        assertEquals(
            FavoriteState.NOT_FAVORITE,
            FavoriteState.FAVORITE.reverse()
        )
    }

}