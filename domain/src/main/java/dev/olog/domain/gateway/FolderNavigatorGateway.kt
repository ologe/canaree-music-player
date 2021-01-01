package dev.olog.domain.gateway

import dev.olog.domain.entity.FileType
import kotlinx.coroutines.flow.Flow
import java.io.File

interface FolderNavigatorGateway {

    fun observeFolderChildren(file: File): Flow<List<FileType>>

}