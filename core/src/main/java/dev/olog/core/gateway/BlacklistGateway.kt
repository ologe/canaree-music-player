package dev.olog.core.gateway

interface BlacklistGateway {

    suspend fun getAll(): List<String>
    suspend fun setAll(items: List<String>)

}