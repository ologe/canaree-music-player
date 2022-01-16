package dev.olog.feature.library.category

import dev.olog.core.MediaStoreType
import dev.olog.feature.library.LibraryCategoryBehavior
import dev.olog.feature.library.LibraryPrefs
import javax.inject.Inject

internal class LibraryCategoriesFragmentPresenter @Inject constructor(
    private val libraryPrefs: LibraryPrefs
) {

    fun getDefaultDataSet(type: MediaStoreType): List<LibraryCategoryBehavior> {
        return when (type) {
            MediaStoreType.Podcast -> libraryPrefs.getDefaultPodcastLibraryCategories()
            MediaStoreType.Song -> libraryPrefs.getDefaultLibraryCategories()
        }
    }

    fun getDataSet(type: MediaStoreType): List<LibraryCategoryBehavior> {
        return when (type) {
            MediaStoreType.Podcast -> libraryPrefs.getPodcastLibraryCategories()
            MediaStoreType.Song -> libraryPrefs.getLibraryCategories()
        }
    }

    fun setDataSet(type: MediaStoreType, list: List<LibraryCategoryBehavior>) {
        return when (type) {
            MediaStoreType.Podcast -> libraryPrefs.setPodcastLibraryCategories(list)
            MediaStoreType.Song -> libraryPrefs.setLibraryCategories(list)
        }
    }

}