package dev.olog.msc.presentation.library.folder.tree

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
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

class FolderTreeFragmentViewModel(
        private val appPreferencesUseCase: AppPreferencesUseCase,
        private val collator: Collator

) : ViewModel() {

    companion object {
        val BACK_HEADER_ID = MediaId.folderId("back header")
    }

    private val isExternalLiveData = MutableLiveData<ExternalStorageModel>()

    init {
        isExternalLiveData.value = ExternalStorageModel(false, searchSdRoot() != null)
    }

    fun observeIsExternal() : LiveData<ExternalStorageModel> = isExternalLiveData

    fun toggleIsExternal(){
        val isExternal = isExternalLiveData.value!!.isExternal
        val sdRoot = searchSdRoot()
        if (isExternal){
            currentFile.onNext(sdRoot ?: Environment.getExternalStorageDirectory())
        } else {
            currentFile.onNext(Environment.getExternalStorageDirectory())
        }
        isExternalLiveData.value = ExternalStorageModel(!isExternal, sdRoot != null)
    }

    private val observer = object : ContentObserver(Handler(Looper.getMainLooper())){
        override fun onChange(selfChange: Boolean) {
            currentFile.onNext(currentFile.value!!)
        }
    }

    private val currentFile = BehaviorSubject.createDefault(Environment.getExternalStorageDirectory())


    init {
        app.contentResolver.registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, observer)
    }

    override fun onCleared() {
        app.contentResolver.unregisterContentObserver(observer)
    }

    fun observeFileName(): LiveData<File> = currentFile
            .asLiveData()

    fun observeChildrens(): LiveData<List<DisplayableFile>> = currentFile.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map {
                val blackList = appPreferencesUseCase.getBlackList()
                val childrens = it.listFiles()
                        .filter { if (it.isDirectory) !blackList.contains(it.path) else !blackList.contains(it.parentFile.path) }

                val (directories, files) = childrens.partition { it.isDirectory }
                val sortedDirectory = filterFolders(directories)
                val sortedFiles = filterTracks(files)

                val displayableItems = sortedDirectory.plus(sortedFiles)

                if (it == Environment.getExternalStorageDirectory()){
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

    private fun searchSdRoot(): File? {
        var sdcardpath : String? = null

        //Datas
        if (File("/data/sdext4/").exists() && File("/data/sdext4/").canRead()) {
            sdcardpath = "/data/sdext4/"
        }
        if (File("/data/sdext3/").exists() && File("/data/sdext3/").canRead()) {
            sdcardpath = "/data/sdext3/"
        }
        if (File("/data/sdext2/").exists() && File("/data/sdext2/").canRead()) {
            sdcardpath = "/data/sdext2/"
        }
        if (File("/data/sdext1/").exists() && File("/data/sdext1/").canRead()) {
            sdcardpath = "/data/sdext1/"
        }
        if (File("/data/sdext/").exists() && File("/data/sdext/").canRead()) {
            sdcardpath = "/data/sdext/"
        }

        //MNTS

        if (File("mnt/sdcard/external_sd/").exists() && File("mnt/sdcard/external_sd/").canRead()) {
            sdcardpath = "mnt/sdcard/external_sd/"
        }
        if (File("mnt/extsdcard/").exists() && File("mnt/extsdcard/").canRead()) {
            sdcardpath = "mnt/extsdcard/"
        }
        if (File("mnt/external_sd/").exists() && File("mnt/external_sd/").canRead()) {
            sdcardpath = "mnt/external_sd/"
        }
        if (File("mnt/emmc/").exists() && File("mnt/emmc/").canRead()) {
            sdcardpath = "mnt/emmc/"
        }
        if (File("mnt/sdcard0/").exists() && File("mnt/sdcard0/").canRead()) {
            sdcardpath = "mnt/sdcard0/"
        }
        if (File("mnt/sdcard1/").exists() && File("mnt/sdcard1/").canRead()) {
            sdcardpath = "mnt/sdcard1/"
        }
//        if (File("mnt/sdcard/").exists() && File("mnt/sdcard/").canRead()) {
//            sdcardpath = "mnt/sdcard/"
//        }

        //Storages
        if (File("/storage/removable/sdcard1/").exists() && File("/storage/removable/sdcard1/").canRead()) {
            sdcardpath = "/storage/removable/sdcard1/"
        }
        if (File("/storage/external_SD/").exists() && File("/storage/external_SD/").canRead()) {
            sdcardpath = "/storage/external_SD/"
        }
        if (File("/storage/ext_sd/").exists() && File("/storage/ext_sd/").canRead()) {
            sdcardpath = "/storage/ext_sd/"
        }
        if (File("/storage/sdcard1/").exists() && File("/storage/sdcard1/").canRead()) {
            sdcardpath = "/storage/sdcard1/"
        }
        if (File("/storage/sdcard0/").exists() && File("/storage/sdcard0/").canRead()) {
            sdcardpath = "/storage/sdcard0/"
        }
//        if (File("/storage/sdcard/").exists() && File("/storage/sdcard/").canRead()) {
//            sdcardpath = "/storage/sdcard/"
//        }

        if (sdcardpath != null && File(sdcardpath).path != Environment.getExternalStorageDirectory().path){
            return File(sdcardpath)
        }
        return null
    }

}