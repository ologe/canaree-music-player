package dev.olog.msc.data.mapper

import dev.olog.msc.api.last.fm.model.SearchedImage
import dev.olog.msc.api.last.fm.model.SearchedTrack
import dev.olog.msc.data.entity.LastFmTrackEntity
import dev.olog.msc.data.entity.LastFmTrackImageEntity

fun LastFmTrackEntity.toDomain(): SearchedTrack {
    return SearchedTrack(id, title, artist, album)
}

fun LastFmTrackImageEntity.toDomain(): SearchedImage {
    return SearchedImage(id, image)
}