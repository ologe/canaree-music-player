package dev.olog.service.music.model

internal class PlayerMediaEntity(
    @JvmField
    val mediaEntity: MediaEntity,
    @JvmField
    val positionInQueue: PositionInQueue,
    @JvmField
    val bookmark: Long
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerMediaEntity

        if (mediaEntity != other.mediaEntity) return false
        if (positionInQueue != other.positionInQueue) return false
        if (bookmark != other.bookmark) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mediaEntity.hashCode()
        result = 31 * result + positionInQueue.hashCode()
        result = 31 * result + bookmark.hashCode()
        return result
    }
}