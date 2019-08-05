package dev.olog.core.entity

class UserCredentials(
    @JvmField
    val username: String,
    @JvmField
    val password: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserCredentials

        if (username != other.username) return false
        if (password != other.password) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + password.hashCode()
        return result
    }
}