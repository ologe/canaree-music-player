//package dev.olog.shared
//
//object MediaIdHelper {
//

//
//
//
//

//
////    fun isSong(mediaId: String) : Boolean {
////        return mediaId.indexOf(LEAF_SEPARATOR) > 0
////    }
//
//
//
//    fun mapCategoryToSource(mediaId: String): Int {
//        val category = extractCategory(mediaId)
//        when (category) {
//            MediaIdHelper.MEDIA_ID_BY_FOLDER -> return 0
//            MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> return 1
//            MediaIdHelper.MEDIA_ID_BY_ALL -> return 2
//            MediaIdHelper.MEDIA_ID_BY_ALBUM -> return 3
//            MediaIdHelper.MEDIA_ID_BY_ARTIST -> return 4
//            MediaIdHelper.MEDIA_ID_BY_GENRE -> return 5
//        }
//        throw IllegalArgumentException("invalid media id $mediaId")
//    }
//
//}