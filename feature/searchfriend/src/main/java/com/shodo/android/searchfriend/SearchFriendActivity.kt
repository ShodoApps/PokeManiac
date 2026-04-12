package com.shodo.android.searchfriend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import com.shodo.android.coreui.theme.PokeManiacTheme
import org.koin.androidx.compose.koinViewModel

/** Activity entry for Search Friend; resolves [SearchFriendViewModel] via Koin and hosts [SearchFriendScreen]. */
class SearchFriendActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeManiacTheme {
                SearchFriendScreen(
                    modifier = Modifier,
                    viewModel = koinViewModel(),
                    onBackPressed = onBackPressedDispatcher::onBackPressed
                )
            }
        }
    }
}