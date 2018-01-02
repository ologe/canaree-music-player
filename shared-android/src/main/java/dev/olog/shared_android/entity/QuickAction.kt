package dev.olog.shared_android.entity

data class QuickAction(
        val enum: QuickActionEnum
) {

    val isNone = enum == QuickActionEnum.NONE
    val isPlay = enum == QuickActionEnum.PLAY
    val isShuffle = enum == QuickActionEnum.SHUFFLE

}

enum class QuickActionEnum {
    NONE, PLAY, SHUFFLE
}