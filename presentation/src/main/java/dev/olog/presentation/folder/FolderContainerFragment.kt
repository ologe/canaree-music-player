package dev.olog.presentation.folder

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaIdCategory
import dev.olog.presentation.R
import dev.olog.presentation.base.viewLifecycleScope
import dev.olog.presentation.folder.tree.FolderTreeFragment
import dev.olog.presentation.interfaces.CanHandleOnBackPressed
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.presentation.tab.TabFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class FolderContainerFragment :
    Fragment(R.layout.fragment_folder_container),
    CanHandleOnBackPressed {

    companion object {
        fun newInstance() = FolderContainerFragment()
    }

    @Inject
    lateinit var preferencesGateway: PresentationPreferencesGateway

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesGateway.observeFolderAsHierarchy()
            .onEach(::update)
            .launchIn(viewLifecycleScope)
    }

    private fun update(asHierarchy: Boolean) {
        childFragmentManager.commit {
            val fragment = if (asHierarchy) {
                FolderTreeFragment.newInstance()
            } else {
                TabFragment.newInstance(MediaIdCategory.FOLDERS)
            }
            replace(R.id.container, fragment, fragment::class.java.name)
        }
    }

    override fun handleOnBackPressed(): Boolean {
        val fragment = childFragmentManager.fragments.firstOrNull()
        return fragment is CanHandleOnBackPressed && fragment.handleOnBackPressed()
    }
}