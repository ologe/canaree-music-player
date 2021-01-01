package dev.olog.feature.edit.playlist.choose

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.domain.AppShortcuts
import dev.olog.feature.base.base.BaseActivity
import dev.olog.feature.edit.playlist.R
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.activity_playlist_chooser.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class PlaylistChooserActivity : BaseActivity() {

    @Inject
    lateinit var appShortcuts: AppShortcuts

    private val viewModel by viewModels<PlaylistChooserActivityViewModel>()

    private val adapter by lazyFast {
        PlaylistChooserActivityAdapter(this, appShortcuts)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_chooser)

        viewModel.observeData()
            .onEach { list ->
                if (list.isEmpty()){
                    toast("No playlist found")
                    finish()
                } else {
                    adapter.submitList(list)
                }
            }.launchIn(lifecycleScope)

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