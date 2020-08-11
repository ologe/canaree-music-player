package dev.olog.feature.library.library

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.adapter.DataBoundViewHolder
import dev.olog.feature.presentation.base.adapter.SimpleAdapter
import dev.olog.feature.presentation.base.fragment.BaseBottomSheetFragment
import dev.olog.feature.presentation.base.prefs.CommonPreferences
import dev.olog.navigation.screens.LibraryPage
import dev.olog.shared.android.extensions.colorAccent
import dev.olog.shared.android.extensions.textColorPrimary
import kotlinx.android.synthetic.main.fragment_library_chooser.*
import kotlinx.android.synthetic.main.item_library_chooser.*
import javax.inject.Inject

@AndroidEntryPoint
class LibraryChooserFragment : BaseBottomSheetFragment() {

    @Inject
    internal lateinit var prefs: CommonPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val page = prefs.getLastLibraryPage()

        list.adapter = LibraryChooserAdapter(page, this::onClick).apply {
            submitList(LibraryPage.values().toList())
        }
        list.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun onClick(page: LibraryPage) {
        prefs.setLibraryPage(page)
        dismiss()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_library_chooser
}

private class LibraryChooserAdapter(
    private val current: LibraryPage,
    private val onClick: (LibraryPage) -> Unit
) : SimpleAdapter<LibraryPage>() {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.itemView.setOnClickListener {
            onClick(getItem(viewHolder.adapterPosition))
        }
    }

    override fun bind(holder: DataBoundViewHolder, item: LibraryPage, position: Int) = with (holder) {
        val context = itemView.context
        val isSelected = current == item
        title.text = item.textify(context)
        title.setTextColor(if (isSelected) context.colorAccent() else context.textColorPrimary())
        itemView.isSelected = isSelected
    }

    private fun LibraryPage.textify(context: Context): String = when (this) {
        LibraryPage.FOLDERS -> context.getString(R.string.common_folders)
        LibraryPage.TRACKS -> context.getString(R.string.common_tracks)
        LibraryPage.ALBUMS -> context.getString(R.string.common_albums)
        LibraryPage.ARTISTS -> context.getString(R.string.common_artists)
        LibraryPage.GENRES -> context.getString(R.string.common_genres)
        LibraryPage.PODCASTS -> context.getString(R.string.common_podcasts)
        LibraryPage.PODCSATS_ARTISTS -> context.getString(R.string.common_podcast_artist)
    }

    override fun getItemViewType(position: Int): Int = R.layout.item_library_chooser
}