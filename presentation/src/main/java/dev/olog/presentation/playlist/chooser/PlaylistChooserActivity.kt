package dev.olog.presentation.playlist.chooser

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseActivity
import dev.olog.presentation.databinding.ActivityPlaylistChooserBinding
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.extensions.viewBinding
import dev.olog.shared.lazyFast

@AndroidEntryPoint
class PlaylistChooserActivity : BaseActivity() {

    private val viewModel by viewModels<PlaylistChooserActivityViewModel>()

    private val binding by viewBinding(ActivityPlaylistChooserBinding::bind)
    private val adapter by lazyFast { PlaylistChooserActivityAdapter(this) }

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

        binding.list.adapter = adapter
        binding.list.layoutManager = GridLayoutManager(this, 2)
    }

    override fun onResume() {
        super.onResume()
        binding.back.setOnClickListener { finish() }
    }

    override fun onPause() {
        super.onPause()
        binding.back.setOnClickListener(null)
    }

}