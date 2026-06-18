package com.shodo.android.myprofile.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.shodo.android.coreui.R
import com.shodo.android.coreui.theme.PokeManiacTheme
import com.shodo.android.coreui.theme.PokeManiacTheme.colors
import com.shodo.android.coreui.theme.PokeManiacTheme.dimens
import com.shodo.android.coreui.theme.PokeManiacTheme.typography
import com.shodo.android.coreui.ui.GenericEmptyScreen
import com.shodo.android.presentation.myprofile.MyProfilePokemonCardUiModel
import com.shodo.android.presentation.myprofile.MyProfileUiModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun MyProfileContent(profile: MyProfileUiModel, modifier: Modifier = Modifier) {
    val totalVotes = remember(profile.pokemonCards) { profile.pokemonCards.sumOf { it.totalVotes } }
    Column(
        modifier = modifier.fillMaxSize().background(colors.backgroundApp),
        horizontalAlignment = CenterHorizontally
    ) {
        MyProfileImage(profile.imageUrl, profile.name)
        MyProfileDescription(totalVotes, profile.name)
        MyProfileCardsContent(profile.pokemonCards)
    }
}

@Composable
private fun ColumnScope.MyProfileImage(imageUrl: String?, name: String?) {
    imageUrl?.let {
        AsyncImage(
            modifier = Modifier
                .padding(top = dimens.xxLarge)
                .clip(CircleShape)
                .size(dimens.xxxxLarge),
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentScale = Crop,
            contentDescription = name
        )
    } ?: run {
        Image(
            modifier = Modifier
                .padding(top = dimens.large)
                .clip(CircleShape)
                .size(dimens.xxxLarge),
            painter = painterResource(id = R.drawable.pokemaniac),
            contentDescription = name
        )
    }
}

@Composable
private fun ColumnScope.MyProfileDescription(totalVotes: Int, name: String?) {
    Text(
        modifier = Modifier.padding(
            top = dimens.small,
            start = dimens.standard,
            end = dimens.standard
        ),
        color = colors.primaryText,
        style = typography.t4,
        text = name ?: stringResource(R.string.default_username)
    )

    Text(
        modifier = Modifier.padding(
            top = dimens.large,
            start = dimens.standard,
            end = dimens.standard
        ).align(CenterHorizontally),
        color = colors.primaryText,
        style = typography.t5,
        text = totalVotes.takeIf { it > 0 }?.let {
            stringResource(R.string.my_profile_total_votes, it)
        } ?: stringResource(R.string.my_profile_total_votes_none)
    )
}

@Composable
private fun ColumnScope.MyProfileCardsContent(pokemonCards: PersistentList<MyProfilePokemonCardUiModel>) {
    if (pokemonCards.isEmpty()) {
        GenericEmptyScreen(stringResource(R.string.my_profile_no_activity_yet))
    } else {
        // Separator
        Box(
            modifier = Modifier
                .padding(top = dimens.small)
                .fillMaxWidth()
                .height(dimens.separator)
                .background(color = colors.tertiary)
        )
        MyActivitiesContent(
            modifier = Modifier
                .padding(top = dimens.standard)
                .weight(1f),
            pokemonCards = pokemonCards
        )
    }
}

@Composable
private fun MyActivitiesContent(modifier: Modifier = Modifier, pokemonCards: PersistentList<MyProfilePokemonCardUiModel>) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(minSize = dimens.minGridUserCellSize),
        contentPadding = PaddingValues(dimens.xSmall)
    ) {
        items(
            items = pokemonCards,
            key = { it.id }
        ) { card ->
            Image(
                modifier = Modifier.clip(RectangleShape),
                painter = rememberAsyncImagePainter(card.imageUri),
                contentDescription = null
            )
        }
    }
}

//region Previews

//region MyProfileContent

@Preview(showBackground = true, name = "MyProfileContent - With Posts - DarkTheme")
@Composable
private fun PreviewMyProfileContent_WithPosts_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        MyProfileContent(profile = previewProfileWithPosts())
    }
}

@Preview(showBackground = true, name = "MyProfileContent - With Posts - LightTheme")
@Composable
private fun PreviewMyProfileContent_WithPosts_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        MyProfileContent(profile = previewProfileWithPosts())
    }
}

@Preview(showBackground = true, name = "MyProfileContent - No Posts - DarkTheme")
@Composable
private fun PreviewMyProfileContent_NoPosts_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        MyProfileContent(profile = previewProfileNoPosts())
    }
}

@Preview(showBackground = true, name = "MyProfileContent - No Posts - LightTheme")
@Composable
private fun PreviewMyProfileContent_NoPosts_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        MyProfileContent(profile = previewProfileNoPosts())
    }
}

//endregion MyProfileContent

//region MyProfileDescription

@Preview(showBackground = true, name = "MyProfileDescription - With Name - DarkTheme")
@Composable
private fun PreviewMyProfileDescription_WithName_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        Column(Modifier.background(colors.backgroundApp)) { MyProfileDescription(totalVotes = 17, name = "Ash") }
    }
}

@Preview(showBackground = true, name = "MyProfileDescription - With Name - LightTheme")
@Composable
private fun PreviewMyProfileDescription_WithName_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        Column(Modifier.background(colors.backgroundApp)) { MyProfileDescription(totalVotes = 17, name = "Ash") }
    }
}

@Preview(showBackground = true, name = "MyProfileDescription - No Name - DarkTheme")
@Composable
private fun PreviewMyProfileDescription_NoName_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        Column(Modifier.background(colors.backgroundApp)) { MyProfileDescription(totalVotes = 0, name = null) }
    }
}

@Preview(showBackground = true, name = "MyProfileDescription - No Name - LightTheme")
@Composable
private fun PreviewMyProfileDescription_NoName_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        Column(Modifier.background(colors.backgroundApp)) { MyProfileDescription(totalVotes = 0, name = null) }
    }
}

//endregion MyProfileDescription

//region MyProfileCardsContent

@Preview(showBackground = true, name = "MyProfileCardsContent - No PokemonCards - DarkTheme")
@Composable
private fun PreviewMyProfileCardsContent_NoPokemonCards_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        Column(Modifier.background(colors.backgroundApp)) { MyProfileCardsContent(pokemonCards = persistentListOf()) }
    }
}

@Preview(showBackground = true, name = "MyProfileCardsContent - No PokemonCards - LightTheme")
@Composable
private fun PreviewMyProfileCardsContent_NoPokemonCards_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        Column(Modifier.background(colors.backgroundApp)) { MyProfileCardsContent(pokemonCards = persistentListOf()) }
    }
}

@Preview(showBackground = true, name = "MyProfileCardsContent - With PokemonCards - DarkTheme")
@Composable
private fun PreviewMyProfileCardsContent_WithPokemonCards_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        Column(Modifier.background(colors.backgroundApp)) { MyProfileCardsContent(pokemonCards = previewPokemonCards()) }
    }
}

@Preview(showBackground = true, name = "MyProfileCardsContent - With PokemonCards - LightTheme")
@Composable
private fun PreviewMyProfileCardsContent_WithPokemonCards_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        Column(Modifier.background(colors.backgroundApp)) { MyProfileCardsContent(pokemonCards = previewPokemonCards()) }
    }
}

//endregion MyProfileDescription

//region Preview Data Helpers

private fun previewProfileWithPosts() = MyProfileUiModel(
    name = "Ash Ketchum",
    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png",
    pokemonCards = previewPokemonCards()
)

private fun previewProfileNoPosts() = MyProfileUiModel(
    name = "Ash Ketchum",
    imageUrl = null,
    pokemonCards = persistentListOf()
)

private fun previewPokemonCards(): PersistentList<MyProfilePokemonCardUiModel> = persistentListOf(
    MyProfilePokemonCardUiModel(id = "1", imageUri = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png", totalVotes = 10, name = "pokemonName1"),
    MyProfilePokemonCardUiModel(id = "2", imageUri = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/4.png", totalVotes = 5, name = "pokemonName4"),
    MyProfilePokemonCardUiModel(id = "3", imageUri = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/7.png", totalVotes = 2, name = "pokemonName2")
)

//endregion Preview Data Helpers

//endregion Previews
