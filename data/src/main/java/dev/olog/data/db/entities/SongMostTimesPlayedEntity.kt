package dev.olog.data.db.entities

class SongMostTimesPlayedEntity(
    @JvmField
    val songId: Long,
    @JvmField
    val timesPlayed: Int
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SongMostTimesPlayedEntity

        if (songId != other.songId) return false
        if (timesPlayed != other.timesPlayed) return false

        return true
    }

    override fun hashCode(): Int {
        var result = songId.hashCode()
        result = 31 * result + timesPlayed
        return result
    }



}