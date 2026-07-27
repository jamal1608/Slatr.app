package com.tonespace.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.android.gms.ads.MobileAds
import com.tonespace.app.ads.AdManager
import com.tonespace.app.ui.navigation.ToneShareNavGraph
import com.tonespace.app.ui.theme.ToneShareTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var adManager: AdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        MobileAds.initialize(this) { }

        setContent {
            ToneShareTheme {
                ToneShareNavGraph()
            }
        }
    }
}