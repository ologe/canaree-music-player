package dev.olog.feature.library.folder

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import com.google.android.material.transition.MaterialFadeThrough
import dev.olog.feature.library.R
import dev.olog.feature.library.folder.normal.FoldersNormalFragment
import dev.olog.feature.library.folder.tree.FoldersTreeFragment
import dev.olog.feature.presentation.base.activity.BaseFragment
import dev.olog.navigation.Navigator
import dev.olog.navigation.screens.FragmentScreen
import kotlinx.android.synthetic.main.fragment_folders.*
import javax.inject.Inject

internal class FoldersFragment : BaseFragment() {

    @Inject
    lateinit var presenter: FoldersFragmentPresenter

    @Inject
    lateinit var navigator: Navigator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        graph.isSelected = presenter.showFolderAsHierarchy()
        updateFragments(graph.isSelected)
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        graph.setOnClickListener {
            it.isSelected = !it.isSelected
            presenter.setShowFolderAsHierarchy(it.isSelected)
            updateFragments(it.isSelected)
        }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
        graph.setOnClickListener(null)
    }

    private fun updateFragments(asHierarchy: Boolean) {
        val currentFragment = childFragmentManager.fragments.firstOrNull()
        if (currentFragment != null) {
            currentFragment.exitTransition = MaterialFadeThrough.create()
        }

        val fragment = if (asHierarchy) {
            FoldersTreeFragment()
        } else {
            FoldersNormalFragment()
        }
        fragment.enterTransition = MaterialFadeThrough.create()

        val tag = if (asHierarchy) {
            FragmentScreen.FOLDERS_TREE.tag
        } else {
            FragmentScreen.FOLDERS_NORMAL.tag
        }

        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.folderFragmentContainer, fragment, tag)
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_folders

}