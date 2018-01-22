package dev.olog.presentation.activity_neural_network

import android.os.Bundle
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseActivity
import javax.inject.Inject

class NeuralNetworkActivity : BaseActivity() {

    @Inject lateinit var viewModel: NeuralNetworkActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_neural_network)
    }


}