package dev.olog.domain.gateway

import dev.olog.domain.entity.Genre
import io.reactivex.Completable

interface GenreGateway  :
        BaseGateway<Genre, Long>,
        ChildsHasSongs<Long>,
        HasMostPlayed<String> {

    fun deleteGenre(id: Long): Completable

}
