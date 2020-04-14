package dev.olog.feature.library.folder

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dev.olog.feature.library.R
import javax.inject.Inject

internal class FoldersFragmentPresenter @Inject constructor(
    private val context: Context,
    private val preferences: SharedPreferences
) {

    fun showFolderAsHierarchy(): Boolean {
        return preferences.getBoolean(context.getString(R.string.prefs_folder_tree_view_key), false)
    }

    fun setShowFolderAsHierarchy(enable: Boolean) {
        return preferences.edit {
            putBoolean(context.getString(R.string.prefs_folder_tree_view_key), enable)
        }
    }

}