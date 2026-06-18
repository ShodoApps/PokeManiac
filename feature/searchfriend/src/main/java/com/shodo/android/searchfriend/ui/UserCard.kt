package com.shodo.android.searchfriend.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.shodo.android.coreui.R
import com.shodo.android.coreui.theme.PokeManiacTheme.colors
import com.shodo.android.coreui.theme.PokeManiacTheme.dimens
import com.shodo.android.coreui.theme.PokeManiacTheme.typography
import com.shodo.android.coreui.ui.PrimaryButton
import com.shodo.android.coreui.ui.SecondaryButton
import com.shodo.android.presentation.searchfriend.SubscriptionState
import com.shodo.android.presentation.searchfriend.SubscriptionState.NotSubscribed
import com.shodo.android.presentation.searchfriend.SubscriptionState.Subscribed
import com.shodo.android.presentation.searchfriend.SubscriptionState.UpdatingSubscribe

/**
 * Single user row: avatar, name, subscribe / unsubscribe actions driven by [SubscriptionState] from shared presentation.
 */
@Composable
fun UserCard(
    id: String,
    name: String,
    imageUrl: String,
    subscriptionState: SubscriptionState,
    onSubscribeUserPressed: (String) -> Unit,
    onUnsubscribeUserPressed: (String) -> Unit
) {
    val onSubscribe = remember(id, onSubscribeUserPressed) { { onSubscribeUserPressed(id) } }
    val onUnsubscribe = remember(id, onUnsubscribeUserPressed) { { onUnsubscribeUserPressed(id) } }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.xxSmall),
        shape = RoundedCornerShape(dimens.small),
        colors = CardDefaults.cardColors(containerColor = colors.backgroundCell)
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            horizontalAlignment = CenterHorizontally
        ) {
            AsyncImage(
                modifier = Modifier
                    .padding(top = dimens.standard)
                    .clip(CircleShape)
                    .wrapContentSize()
                    .size(dimens.xxxLarge),
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
                modifier = Modifier.padding(
                    top = dimens.small,
                    start = dimens.small,
                    end = dimens.small
                )
            )

            SubscriptionStateContent(
                subscriptionState = subscriptionState,
                onUnsubscribeUserPressed = onUnsubscribe,
                onSubscribeUserPressed = onSubscribe
            )
        }
    }
}

@Composable
private fun ColumnScope.SubscriptionStateContent(
    subscriptionState: SubscriptionState,
    onUnsubscribeUserPressed: () -> Unit,
    onSubscribeUserPressed: () -> Unit
) {
    when (subscriptionState) {
        Subscribed -> SecondaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.small, vertical = dimens.small),
            text = stringResource(R.string.unsubscribe)
        ) { onUnsubscribeUserPressed() }

        NotSubscribed -> PrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.small, vertical = dimens.small),
            text = stringResource(R.string.subscribe),
            onClick = { onSubscribeUserPressed() }
        )

        UpdatingSubscribe -> CircularProgressIndicator(
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(vertical = dimens.small),
            color = colors.primaryText
        )
    }
}
