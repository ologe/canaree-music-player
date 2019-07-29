package dev.olog.core.gateway

import dev.olog.core.entity.FileType
import kotlinx.coroutines.flow.Flow
import java.io.File

interface FolderNavigatorGateway {

    fun observeFolderChildren(file: File): Flow<List<FileType>>

}