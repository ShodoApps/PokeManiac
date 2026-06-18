package com.shodo.android.myfriends.myfriendlist.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.shodo.android.coreui.R
import com.shodo.android.coreui.theme.PokeManiacTheme
import com.shodo.android.coreui.theme.PokeManiacTheme.colors
import com.shodo.android.coreui.theme.PokeManiacTheme.dimens
import com.shodo.android.coreui.theme.PokeManiacTheme.typography
import com.shodo.android.coreui.ui.SecondaryButton

@Composable
fun MyFriendCard(
    id: String,
    name: String,
    imageUrl: String,
    onFriendPressed: () -> Unit,
    onUnsubscribePressed: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.xSmall),
        shape = RoundedCornerShape(dimens.xSmall),
        colors = CardDefaults.cardColors(containerColor = colors.backgroundCell),
        onClick = onFriendPressed
    ) {
        Row(
            modifier = Modifier
                .height(dimens.xxxLarge)
                .padding(horizontal = dimens.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(dimens.xxLarge),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentScale = Crop,
                contentDescription = name
            )

            Text(
                color = colors.primaryText,
                style = typography.t3,
                text = name,
                modifier = Modifier
                    .padding(dimens.small)
                    .weight(1f)
            )

            SecondaryButton(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = dimens.small, vertical = dimens.small),
                text = stringResource(R.string.unsubscribe)
            ) { onUnsubscribePressed() }
        }
    }
}

//region Previews

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyFriendCard - LightTheme")
@Composable
fun PreviewMyFriendCard_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        MyFriendCard(
            id = "friendId",
            name = "friendName",
            imageUrl = "https://www.superherodb.com/pictures2/portraits/10/100/10831.jpg",
            onFriendPressed = {},
            onUnsubscribePressed = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyFriendCard - DarkTheme")
@Composable
fun PreviewMyFriendCard_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        MyFriendCard(
            id = "friendId",
            name = "friendName",
            imageUrl = "https://www.superherodb.com/pictures2/portraits/10/100/10831.jpg",
            onFriendPressed = {},
            onUnsubscribePressed = {}
        )
    }
}

//endregion Previews
