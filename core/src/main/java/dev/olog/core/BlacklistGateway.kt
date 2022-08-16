package dev.olog.core

interface BlacklistGateway {
    suspend fun getBlackList(): List<String>
    suspend fun setBlackList(items: List<String>)
}