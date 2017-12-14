package dev.olog.presentation.activity_splash

import android.Manifest
import android.os.Bundle
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide
import dagger.android.AndroidInjection
import dev.olog.presentation.R
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.utils.extension.hasPermission
import dev.olog.presentation.utils.extension.subscribe
import kotlinx.android.synthetic.main.activity_splash.*
import javax.inject.Inject

class SplashActivity : IntroActivity() {

    @Inject lateinit var presenter: SplashPresenter
    @Inject lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        val hasStoragePermission = checkStoragePermission()

        if (presenter.isFirstAccess(hasStoragePermission)){
            setupSlides()
//            setContentView(R.layout.activity_splash)
//            viewPager.adapter = adapter.get()
//            subscribeToStorageRequest()
        } else {
            navigator.toMainActivity()
        }
    }

    private fun setupSlides(){
        val presentationSlide = FragmentSlide.Builder()
                .fragment(R.layout.fragment_splash)
                .build()

        val tutorialSlide = FragmentSlide.Builder()
                .fragment(R.layout.fragment_splash_tutorial)
                .build()

        addSlide(presentationSlide)
        addSlide(tutorialSlide)
    }

    private fun checkStoragePermission() : Boolean {
        return hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun subscribeToStorageRequest(){
        presenter.subscribeToStoragePermission(root)
                .subscribe(this, { navigator.toMainActivity() })
    }

}