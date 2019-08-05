package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recent_searches",
    indices = [(Index("pk"))]
)
class RecentSearchesEntity(
    @PrimaryKey(autoGenerate = true)
    @JvmField
    val pk: Int = 0,
    @JvmField
    val dataType: Int,
    @JvmField
    val itemId: Long,
    @JvmField
    val insertionTime: Long = System.currentTimeMillis()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecentSearchesEntity

        if (pk != other.pk) return false
        if (dataType != other.dataType) return false
        if (itemId != other.itemId) return false
        if (insertionTime != other.insertionTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pk
        result = 31 * result + dataType
        result = 31 * result + itemId.hashCode()
        result = 31 * result + insertionTime.hashCode()
        return result
    }
}