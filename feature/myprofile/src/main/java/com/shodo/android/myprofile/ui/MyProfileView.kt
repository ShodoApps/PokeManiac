package com.shodo.android.myprofile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.shodo.android.coreui.theme.PokeManiacTheme
import com.shodo.android.coreui.theme.PokeManiacTheme.colors
import com.shodo.android.coreui.ui.GenericLoader
import com.shodo.android.presentation.myprofile.MyProfilePokemonCardUiModel
import com.shodo.android.presentation.myprofile.MyProfileUiModel
import com.shodo.android.presentation.myprofile.MyProfileUiState
import com.shodo.android.presentation.myprofile.MyProfileUiState.Data
import com.shodo.android.presentation.myprofile.MyProfileUiState.Loading
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

/**
 * Main Composable displaying the application's MyProfile screen.
 * It uses a `Scaffold` to display a top bar (`MyProfileTopBar`),
 * main content, and a `SnackbarHost` for displaying temporary messages.
 *
 * @param modifier           Modifier to customize the root layout.
 * @param uiState            UI state representing the current screen state. Possible states:
 *                           - `Loading`: Displays a loading indicator.
 *                           - `Data`: Displays My Profile detailed data.
 *
 * @param onPostTransactionPressed      Callback to navigate to PostTransaction screen.
 * @param onBackPressed                 Callback to navigate back.
 * @param snackbarHostState             State of the `SnackbarHost` to display temporary messages.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileView(
    modifier: Modifier = Modifier,
    uiState: MyProfileUiState,
    onBackPressed: () -> Unit,
    onPostTransactionPressed: () -> Unit,
    onBillingPressed: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(color = colors.backgroundApp),
        topBar = {
            MyProfileTopBar(
                onBackPressed = onBackPressed,
                onBillingPressed = onBillingPressed,
                onPostTransaction = onPostTransactionPressed
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.backgroundApp)
                .padding(innerPadding),
        ) {
            when (val state = uiState) {
                Loading -> GenericLoader()
                is Data -> MyProfileContent(profile = state.profile)
            }
        }
    }
}

//region Previews

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyProfileView - Loading - LightTheme")
@Composable
fun PreviewMyProfileView_Loading_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        MyProfileView(
            onBackPressed = {},
            uiState = Loading,
            onPostTransactionPressed = {},
            onBillingPressed = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyProfileView - Loading - DarkTheme")
@Composable
fun PreviewMyFriendsListView_Loading_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        MyProfileView(
            onBackPressed = {},
            uiState = Loading,
            onPostTransactionPressed = {},
            onBillingPressed = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyProfileView - Data - LightTheme")
@Composable
fun PreviewMyProfileView_Data_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        MyProfileView(
            onBackPressed = {},
            uiState = Data(MyProfileUiModel(
                name = "Ash Ketchum",
                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png",
                pokemonCards = previewPokemonCards()
            )),
            onPostTransactionPressed = {},
            onBillingPressed = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyProfileView - Data - DarkTheme")
@Composable
fun PreviewMyFriendsListView_Data_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        MyProfileView(
            onBackPressed = {},
            uiState = Data(MyProfileUiModel(
                name = "Ash Ketchum",
                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png",
                pokemonCards = previewPokemonCards()
            )),
            onPostTransactionPressed = {},
            onBillingPressed = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}

private fun previewPokemonCards(): PersistentList<MyProfilePokemonCardUiModel> = persistentListOf(
    MyProfilePokemonCardUiModel(id = "1", imageUri = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png", totalVotes = 10, name = "pokemonName1"),
    MyProfilePokemonCardUiModel(id = "2", imageUri = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/4.png", totalVotes = 5, name = "pokemonName4"),
    MyProfilePokemonCardUiModel(id = "3", imageUri = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/7.png", totalVotes = 2, name = "pokemonName2")
)

//endregion Previews
