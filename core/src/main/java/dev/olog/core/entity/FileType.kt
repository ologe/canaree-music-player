package dev.olog.core.entity

sealed class FileType {

    class Folder(
        @JvmField
        val name: String,
        @JvmField
        val path: String
    ) : FileType() {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Folder

            if (name != other.name) return false
            if (path != other.path) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + path.hashCode()
            return result
        }
    }

    class Track(
        @JvmField
        val title: String,
        @JvmField
        val path: String
    ) : FileType() {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Track

            if (title != other.title) return false
            if (path != other.path) return false

            return true
        }

        override fun hashCode(): Int {
            var result = title.hashCode()
            result = 31 * result + path.hashCode()
            return result
        }
    }
}