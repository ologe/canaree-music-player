package dev.olog.core.blacklist

import kotlinx.coroutines.flow.Flow
import java.io.File

interface BlacklistGateway {

    fun observeBlacklist(): Flow<List<File>>
    suspend fun setBlacklist(directories: List<File>)

}