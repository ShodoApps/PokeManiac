package com.shodo.android.dashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.shodo.android.coreui.theme.PokeManiacTheme
import org.koin.androidx.compose.koinViewModel

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeManiacTheme {
                DashboardScreen(viewModel = koinViewModel())
            }
        }
    }
}
