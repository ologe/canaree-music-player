package dev.olog.data.repository

import android.content.Context
import android.provider.MediaStore
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.Folder
import dev.olog.core.entity.Song
import dev.olog.core.gateway.FolderGateway2
import dev.olog.core.gateway.Path
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class FolderRepository2 @Inject constructor(
    @ApplicationContext context: Context
)  : BaseRepository<Folder, Path>(context), FolderGateway2 {

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
    }

    override fun queryAll(): List<Folder> {
        return listOf()
    }

    override fun getByParam(param: Path): Folder? {
        return null
    }

    override fun observeByParam(param: Path): Flow<Folder?> {
        return flow {  }
    }

    override fun getTrackListByParam(param: Path): List<Song> {
        return listOf()
    }

    override fun observeTrackListByParam(param: Path): Flow<List<Song>> {
        return flow {  }
    }
}