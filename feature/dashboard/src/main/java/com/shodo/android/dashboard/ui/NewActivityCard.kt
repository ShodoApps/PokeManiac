package com.shodo.android.dashboard.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.platform.LocalContext
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
import com.shodo.android.presentation.dashboard.DashboardImageSourceUiModel.FileSource
import com.shodo.android.presentation.dashboard.DashboardImageSourceUiModel.UrlSource
import com.shodo.android.presentation.dashboard.NewActivityTypeUiModel.Purchase
import com.shodo.android.presentation.dashboard.NewActivityTypeUiModel.Sale
import com.shodo.android.presentation.dashboard.NewActivityUiModel
import com.shodo.android.presentation.dashboard.PokemonCardUiModel

@Composable
fun NewActivityCard(newActivity: NewActivityUiModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = colors.backgroundCell)
    ) {
        Column(
            modifier = Modifier.wrapContentSize()
        ) {
            NewActivityHeader(newActivity.friendImageUrl, newActivity.friendName, newActivity.date)
            Text(
                modifier = Modifier
                    .padding(dimens.standard)
                    .align(CenterHorizontally),
                color = colors.primaryText,
                style = typography.t6,
                text = when (newActivity.activityType) {
                    Purchase -> stringResource(R.string.activity_purchase, newActivity.pokemonCard.name, newActivity.price)
                    Sale -> stringResource(R.string.activity_sale, newActivity.pokemonCard.name, newActivity.price)
                }
            )
            NewActivityImage(pokemonCard = newActivity.pokemonCard)
            // TODO Number of likes
            // TODO Button to like
            // TODO Comments section
        }
    }
}

@Composable
private fun NewActivityHeader(friendImageUrl: String?, friendName: String, date: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = dimens.standard,
                top = dimens.small,
                end = dimens.small
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        friendImageUrl?.let { imageUrl ->
            AsyncImage(
                modifier = Modifier
                    .padding(
                        end = dimens.small
                    )
                    .clip(CircleShape)
                    .size(dimens.xLarge),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentScale = Crop,
                contentDescription = friendName
            )
        }
        Column(
            modifier = Modifier.padding(
                top = dimens.small,
                end = dimens.small
            )
        ) {
            Text(
                modifier = Modifier.padding(
                    end = dimens.small
                ),
                color = colors.primaryText,
                style = typography.t4,
                text = friendName
            )
            Text(
                modifier = Modifier.padding(
                    top = dimens.xxSmall,
                    end = dimens.small
                ),
                color = colors.primaryText,
                style = typography.t5,
                text = date
            )
        }
    }
}

@Composable
private fun NewActivityImage(pokemonCard: PokemonCardUiModel) {
    when (val src = pokemonCard.imageSource) {
        is UrlSource -> AsyncImage(
            modifier = Modifier
                .padding(top = dimens.small)
                .fillMaxWidth()
                .aspectRatio(1f),
            model = ImageRequest.Builder(LocalContext.current)
                .data(src.imageUrl)
                .crossfade(true)
                .build(),
            contentScale = Crop,
            contentDescription = pokemonCard.name
        )

        is FileSource -> Image(
            modifier = Modifier
                .padding(top = dimens.small)
                .fillMaxWidth()
                .aspectRatio(1f),
            painter = rememberAsyncImagePainter(src.fileUri),
            contentScale = Crop,
            contentDescription = pokemonCard.name
        )
    }
}

//region previews

//region NewActivityHeader

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "NewActivityHeader - With image - LightTheme")
@Composable
fun PreviewNewActivityHeader_WithImage_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        Box(modifier = Modifier.background(colors.backgroundApp)) {
            NewActivityHeader(
                friendName = "friendName2",
                friendImageUrl = "https://www.superherodb.com/pictures2/portraits/10/100/10831.jpg",
                date = "01/06/2025 12:30"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "NewActivityHeader - With image - DarkTheme")
@Composable
fun PreviewNewActivityHeader_WithImage_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        Box(modifier = Modifier.background(colors.backgroundApp)) {
            NewActivityHeader(
                friendName = "friendName2",
                friendImageUrl = "https://www.superherodb.com/pictures2/portraits/10/100/10831.jpg",
                date = "01/06/2025 12:30"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "NewActivityHeader - No image - LightTheme")
@Composable
fun PreviewNewActivityHeader_NoImage_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        Box(modifier = Modifier.background(colors.backgroundApp)) {
            NewActivityHeader(
                friendName = "friendName2",
                friendImageUrl = null,
                date = "01/06/2025 12:30"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "NewActivityHeader - With user image - DarkTheme")
@Composable
fun PreviewNewActivityHeader_NoImage_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        Box(modifier = Modifier.background(colors.backgroundApp)) {
            NewActivityHeader(
                friendName = "friendName2",
                friendImageUrl = null,
                date = "01/06/2025 12:30"
            )
        }
    }
}

//endregion NewActivityHeader

//region NewActivityImage

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "NewActivityImage - LightTheme")
@Composable
fun PreviewNewActivityImage_NoImage_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        Box(modifier = Modifier.background(colors.backgroundApp)) {
            NewActivityImage(previewPokemonCardUI())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "NewActivityImage - DarkTheme")
@Composable
fun PreviewNewActivityImage_NoImage_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        Box(modifier = Modifier.background(colors.backgroundApp)) {
            NewActivityImage(previewPokemonCardUI())
        }
    }
}

//endregion NewActivityImage

//region NewActivityCard

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "NewActivityCard - No user image - LightTheme")
@Composable
fun PreviewNewActivityCard_NoUserImage_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        NewActivityCard(
            NewActivityUiModel(
                id = "friendName2" + "01/06/2025 12:30",
                friendName = "friendName2",
                friendImageUrl = null,
                date = "01/06/2025 12:30",
                activityType = Purchase,
                pokemonCard = previewPokemonCardUI(),
                price = 30
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "NewActivityCard - No image - DarkTheme")
@Composable
fun PreviewNewActivityCard_NoUserImage_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        NewActivityCard(
            NewActivityUiModel(
                id = "friendName2" + "01/06/2025 12:30",
                friendName = "friendName2",
                friendImageUrl = null,
                date = "01/06/2025 12:30",
                activityType = Purchase,
                pokemonCard = previewPokemonCardUI(),
                price = 30
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "NewActivityCard - With user image - LightTheme")
@Composable
fun PreviewNewActivityCard_WithUserImage_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        NewActivityCard(
            NewActivityUiModel(
                id = "friendName2" + "01/06/2025 12:30",
                friendName = "friendName2",
                friendImageUrl = "https://www.superherodb.com/pictures2/portraits/10/100/10831.jpg",
                date = "01/06/2025 12:30",
                activityType = Purchase,
                pokemonCard = previewPokemonCardUI(),
                price = 30
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "NewActivityCard - With user image - DarkTheme")
@Composable
fun PreviewNewActivityCard_WithUserImage_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        NewActivityCard(
            NewActivityUiModel(
                id = "friendName2" + "01/06/2025 12:30",
                friendName = "friendName2",
                friendImageUrl = "https://www.superherodb.com/pictures2/portraits/10/100/10831.jpg",
                date = "01/06/2025 12:30",
                activityType = Purchase,
                pokemonCard = previewPokemonCardUI(),
                price = 30
            )
        )
    }
}

//endregion NewActivityCard

private fun previewPokemonCardUI() = PokemonCardUiModel(
    name = "pokemonName",
    imageSource = UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/2.png")
)

//endregion previews
