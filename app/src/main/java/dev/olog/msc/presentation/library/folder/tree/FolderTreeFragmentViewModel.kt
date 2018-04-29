package dev.olog.msc.presentation.library.folder.tree

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.os.Environment
import dev.olog.msc.R
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.k.extension.isAudioFile
import java.io.File
import java.text.Collator

class FolderTreeFragmentViewModel(
        private val context: Context,
        private val appPreferencesUseCase: AppPreferencesUseCase,
        private val collator: Collator

) : ViewModel() {

    companion object {
        val BACK_HEADER_ID = MediaId.folderId("back header")
    }

    private val currentFile = MutableLiveData<File>()

    init {
        val root = Environment.getExternalStorageDirectory()
        currentFile.postValue(root)
    }

    fun observeFileName(): LiveData<File> = currentFile

    fun observeChildrens(): LiveData<List<DisplayableItem>> = Transformations.map(currentFile, {
        val blackList = appPreferencesUseCase.getBlackList()
        val childrens = it.listFiles()
                .filter { if (it.isDirectory) !blackList.contains(it.path) else !blackList.contains(it.parentFile.path) }

        val (directories, files) = childrens.partition { it.isDirectory }
        val sortedDirectory = filterFolders(directories)
        val sortedFiles = filterTracks(files)

        val displayableItems = sortedDirectory.plus(sortedFiles).map { it.toDisplayableItem() }

        if (it == Environment.getExternalStorageDirectory()){
            displayableItems
        } else {
            backDisplableItem().plus(displayableItems)
        }
    })

    private fun filterFolders(files: List<File>): List<File> {
        return files.filter { !it.isHidden && it.isDirectory && it.listFiles().isNotEmpty() }
                .sortedWith(Comparator { o1, o2 -> collator.compare(o1.name, o2.name) })
    }

    private fun filterTracks(files: List<File>): List<File> {
        return files.filter { it.isAudioFile() }
                .sortedWith(Comparator { o1, o2 -> collator.compare(o1.name, o2.name) })
    }

    fun nextFolder(item: DisplayableItem){
        if (item.mediaId == BACK_HEADER_ID){
            if (currentFile.value!! != Environment.getExternalStorageDirectory()){
                currentFile.postValue(currentFile.value!!.parentFile)
            }
            return
        }

        val file = File(item.image)
        currentFile.postValue(file)
    }

    private fun backDisplableItem(): List<DisplayableItem> {
        return listOf(
                DisplayableItem(
                        R.layout.item_folder_tree_directory,
                        BACK_HEADER_ID,
                        "..."
                )
        )
    }

    private fun File.toDisplayableItem(): DisplayableItem {
        val isDirectory = this.isDirectory
        val id = if (isDirectory) R.layout.item_folder_tree_directory else R.layout.item_folder_tree_track

        val (directories, files) = (this.listFiles() ?: arrayOf()).partition { it.isDirectory }
        val sortedDirectory = filterFolders(directories)
        val sortedFiles = filterTracks(files)

        var size = ""
        val dirSize = sortedDirectory.size
        val trackSize = sortedFiles.size

        if (dirSize == 0 && trackSize == 0){
            size = context.getString(R.string.common_empty)
        }

        if (dirSize > 0){
            size = context.resources.getQuantityString(R.plurals.common_plurals_folder, dirSize, dirSize)
            if (trackSize > 0){
                size += TextUtils.MIDDLE_DOT_SPACED
            }
        }

        if (trackSize > 0){
            size = context.resources.getQuantityString(R.plurals.common_plurals_song, trackSize, trackSize)
        }


        return DisplayableItem(
                id,
                MediaId.folderId(this.path),
                this.name,
                size,
                image = this.path
        )
    }



}