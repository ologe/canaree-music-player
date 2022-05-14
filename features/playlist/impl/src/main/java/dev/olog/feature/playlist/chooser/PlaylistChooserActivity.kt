package dev.olog.feature.playlist.chooser

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.playlist.R
import dev.olog.feature.shortcuts.api.AppShortcuts
import dev.olog.ui.activity.ThemedActivity
import dev.olog.shared.extension.lazyFast
import dev.olog.shared.extension.subscribe
import dev.olog.shared.extension.toast
import kotlinx.android.synthetic.main.activity_playlist_chooser.*
import javax.inject.Inject

@AndroidEntryPoint
class PlaylistChooserActivity : ThemedActivity() {

    @Inject
    lateinit var appShortcuts: AppShortcuts

    private val viewModel by viewModels<PlaylistChooserActivityViewModel>()

    private val adapter by lazyFast {
        PlaylistChooserActivityAdapter(
            activity = this,
            shortcuts = appShortcuts
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_chooser)

        viewModel.observeData()
            .subscribe(this) { list ->
                if (list.isEmpty()){
                    toast("No playlist found")
                    finish()
                } else {
                    adapter.submitList(list)
                }
            }

        list.adapter = adapter
        list.layoutManager = GridLayoutManager(this, 2)
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { finish() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

}