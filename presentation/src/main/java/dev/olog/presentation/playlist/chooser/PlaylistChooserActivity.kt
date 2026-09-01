package dev.olog.presentation.playlist.chooser

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseActivity
import dev.olog.presentation.databinding.ActivityPlaylistChooserBinding
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.lazyFast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistChooserActivity : BaseActivity() {

    private val viewModel by viewModels<PlaylistChooserActivityViewModel>()

    private val adapter by lazyFast { PlaylistChooserActivityAdapter(this) }

    private lateinit var binding: ActivityPlaylistChooserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistChooserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.observeData()
            .subscribe(this) { list ->
                if (list.isEmpty()){
                    toast("No playlist found")
                    finish()
                } else {
                    adapter.updateDataSet(list)
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