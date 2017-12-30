package dev.olog.domain.entity

data class SmallPlayType(
        val enum: SmallPlayEnum
) {

    val isNone = enum == SmallPlayEnum.NONE
    val isPlay = enum == SmallPlayEnum.PLAY
    val isShuffle = enum == SmallPlayEnum.SHUFFLE

}

enum class SmallPlayEnum {
    NONE, PLAY, SHUFFLE
}