package dev.olog.core.gateway

interface BlacklistGateway {

    suspend fun getBlacklist(): List<String>
    suspend fun setBlacklist(items: List<String>)

    suspend fun reset()

}