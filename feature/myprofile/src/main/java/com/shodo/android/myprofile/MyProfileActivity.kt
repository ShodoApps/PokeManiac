package com.shodo.android.myprofile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import com.shodo.android.coreui.theme.PokeManiacTheme
import org.koin.androidx.compose.koinViewModel

class MyProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeManiacTheme {
                MyProfileScreen(
                    modifier = Modifier,
                    viewModel = koinViewModel(),
                    onBackPressed = onBackPressedDispatcher::onBackPressed
                )
            }
        }
    }
}
