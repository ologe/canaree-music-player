package dev.olog.core.folder

import kotlinx.coroutines.flow.Flow
import java.io.File

interface FolderNavigatorGateway {

    fun observeFolderChildren(file: File): Flow<List<FileType>>

}