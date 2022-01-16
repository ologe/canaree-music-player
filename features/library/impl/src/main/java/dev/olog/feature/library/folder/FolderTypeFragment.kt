package dev.olog.feature.library.folder

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaStoreType
import dev.olog.core.MediaUri
import dev.olog.feature.library.LibraryPrefs
import dev.olog.feature.library.R
import dev.olog.feature.library.folder.tree.FolderTreeFragment
import dev.olog.feature.library.tab.TabFragment
import dev.olog.shared.android.extensions.collectOnLifecycle
import javax.inject.Inject

@AndroidEntryPoint
class FolderTypeFragment : Fragment(R.layout.fragment_folder_type) {

    @Inject
    lateinit var libraryPrefs: LibraryPrefs

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        libraryPrefs.useFolderTree.observe()
            .collectOnLifecycle(this) {
                updateFragment(it)
            }
    }

    private fun updateFragment(showAsTree: Boolean) {
        val fragment = if (showAsTree) {
            FolderTreeFragment.newInstance()
        } else {
            TabFragment.newInstance(MediaUri.Category.Folder, MediaStoreType.Song)
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, null)
            .commitAllowingStateLoss()
    }

}