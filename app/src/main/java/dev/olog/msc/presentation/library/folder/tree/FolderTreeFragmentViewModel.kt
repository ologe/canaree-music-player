package dev.olog.msc.presentation.library.folder.tree

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.database.ContentObserver
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import dev.olog.msc.R
import dev.olog.msc.app.app
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.io.File
import java.text.Collator
import javax.inject.Inject

class FolderTreeFragmentViewModel @Inject constructor(
        private val appPreferencesUseCase: AppPreferencesUseCase,
        private val collator: Collator

) : ViewModel() {

    companion object {
        val BACK_HEADER_ID = MediaId.folderId("back header")
    }

    private val observer = object : ContentObserver(Handler(Looper.getMainLooper())){
        override fun onChange(selfChange: Boolean) {
            currentFile.onNext(currentFile.value!!)
        }
    }

    private val currentFile = BehaviorSubject.createDefault(appPreferencesUseCase.getDefaultMusicFolder())

    init {
        app.contentResolver.registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                true,
                observer)
    }

    override fun onCleared() {
        app.contentResolver.unregisterContentObserver(observer)
    }

    fun observeFileName(): LiveData<File> = currentFile
            .asLiveData()

    fun observeChildrens(): LiveData<List<DisplayableFile>> = currentFile.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map { file ->
                val blackList = appPreferencesUseCase.getBlackList()
                val children: List<File> = file.listFiles()
                        ?.filter { if (file.isDirectory) !blackList.contains(file.absolutePath) else !blackList.contains(file.parentFile.absolutePath) }
                        ?: listOf()

                val (directories, files) = children.partition { it.isDirectory }
                val sortedDirectory = filterFolders(directories)
                val sortedFiles = filterTracks(files)

                val displayableItems = sortedDirectory.plus(sortedFiles)

                if (file.path == "/"){
                    displayableItems
                } else {
                    displayableItems.startWith(backDisplableItem)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .asLiveData()

    private fun filterFolders(files: List<File>): List<DisplayableFile> {
        return files.asSequence()
                .filter { it.isDirectory }
                .sortedWith(Comparator { o1, o2 -> collator.compare(o1.name, o2.name) })
                .map { it.toDisplayableItem() }
                .toList()
                .startWithIfNotEmpty(foldersHeader)
    }

    private fun filterTracks(files: List<File>): List<DisplayableFile> {
        return files.asSequence()
                .filter { it.isAudioFile() }
                .sortedWith(Comparator { o1, o2 -> collator.compare(o1.name, o2.name) })
                .map { it.toDisplayableItem() }
                .toList()
                .startWithIfNotEmpty(tracksHeader)
    }

    fun popFolder(): Boolean{
        val current = currentFile.value!!
        if (current.isStorageDir()){
            return false
        }
        currentFile.onNext(current.parentFile)
        return true
    }

    fun goBack(){
        if (!currentFile.value!!.isStorageDir()){
            currentFile.onNext(currentFile.value!!.parentFile)
        }
    }

    fun nextFolder(file: File){
        currentFile.onNext(file)
    }

    fun updateDefaultFolder(file: File){
        appPreferencesUseCase.setDefaultMusicFolder(file.safeGetCanonicalFile())
    }

    private val backDisplableItem: List<DisplayableFile> = listOf(
            DisplayableFile(R.layout.item_folder_tree_directory, BACK_HEADER_ID, "...", null, null)
    )

    private val foldersHeader = DisplayableFile(
            R.layout.item_folder_tree_header, MediaId.headerId("folder header"), app.getString(R.string.common_folders), null, null)

    private val tracksHeader = DisplayableFile(
            R.layout.item_folder_tree_header, MediaId.headerId("track header"), app.getString(R.string.common_tracks), null, null)

    private fun File.toDisplayableItem(): DisplayableFile {
        val isDirectory = this.isDirectory
        val id = if (isDirectory) R.layout.item_folder_tree_directory else R.layout.item_folder_tree_track

        return DisplayableFile(
                type = id,
                mediaId = MediaId.folderId(this.path),
                title = this.name,
                subtitle = null,
                path =  this.path
        )
    }
}