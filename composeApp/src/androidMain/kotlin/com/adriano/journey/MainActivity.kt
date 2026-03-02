package com.adriano.journey

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.adriano.journey.domain.DownloadState
import com.adriano.journey.domain.LargeLanguageModel
import com.adriano.journey.domain.LargeLanguageModelMediaPipe
import com.adriano.journey.domain.ModelDownloader
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent {

    private val modelDownloader: ModelDownloader by inject()
    private val LLM: LargeLanguageModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val test = LLM.generateResponse("hallo")
            Log.d("qwer", test)
        }

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
