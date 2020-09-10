//package dev.olog.feature.library.track
//
//import dev.olog.feature.presentation.base.model.PresentationId
//
//sealed class TrackFragmentItem {
//
//    object Shuffle : TrackFragmentItem()
//
//    data class Track(
//        val mediaId: PresentationId.Track,
//        val title: String,
//        val subtitle: String
//    ) : TrackFragmentItem()
//
//    data class Podcast(
//        val mediaId: PresentationId.Track,
//        val title: String,
//        val subtitle: String,
//        val duration: Long
//    ) : TrackFragmentItem()
//
//}