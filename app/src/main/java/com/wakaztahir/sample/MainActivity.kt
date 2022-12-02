package com.wakaztahir.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.wakaztahir.sample.controller.AppState
import com.wakaztahir.sample.ui.components.main.MainViewModel
import com.wakaztahir.sample.ui.theme.SampleProjectTheme
import com.wakaztahir.sample.ui.theme.SetSystemBarColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {

    private val model by viewModels<MainViewModel>()

    @Inject
    lateinit var appState : AppState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val darkTheme = appState.isDarkTheme()

            SampleProjectTheme(darkTheme) {
                SetSystemBarColors(darkTheme)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Text(text = "Hello Android!")
                }
            }
        }

        keepDisplayingAds()
    }

    private fun keepDisplayingAds(){
        lifecycleScope.launch {
            appState.loadAd(onLoaded = {
                it.show(this@MainActivity)
            },onShown = {
                model.viewModelScope.launch {
                    delay(120.seconds.inWholeMilliseconds)
                    keepDisplayingAds()
                }
            })
        }
    }

}