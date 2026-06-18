package com.shodo.android.pokemaniac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.shodo.android.coreui.theme.PokeManiacTheme
import com.shodo.android.welcome.WelcomeScreen
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeManiacTheme {
                WelcomeScreen(
                    viewModel = koinViewModel(),
                    onNavigationDone = ::finish
                )
            }
        }
    }
}
