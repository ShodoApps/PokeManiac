package com.shodo.android.searchfriend.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.shodo.android.coreui.R
import com.shodo.android.coreui.theme.PokeManiacTheme.colors
import com.shodo.android.coreui.theme.PokeManiacTheme.typography
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFriendTopBar(onSearchFriend: (String) -> Unit, onBackPressed: () -> Unit) {
    TopAppBar(
        colors = topAppBarColors(
            containerColor = colors.backgroundApp
        ),
        title = {
            DebouncedSearchTextField(
                placeholderText = stringResource(R.string.search_friends),
                onDebouncedQueryChange = onSearchFriend
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.primaryText
                )
            }
        }
    )
}

@OptIn(FlowPreview::class)
@Composable
fun DebouncedSearchTextField(
    placeholderText: String,
    onDebouncedQueryChange: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        snapshotFlow { searchQuery }
            .debounce(500)
            .collect { debouncedText ->
                onDebouncedQueryChange(debouncedText)
            }
    }

    val customTextSelectionColors = TextSelectionColors(
        handleColor = colors.primaryText,
        backgroundColor = colors.fourthText
    )
    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(placeholderText) },
            singleLine = true,
            textStyle = typography.t7.copy(color = colors.primaryText),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colors.backgroundCell,
                unfocusedContainerColor = colors.backgroundCell,
                cursorColor = colors.backgroundCell,
                focusedLabelColor = Color.Transparent,
                unfocusedPlaceholderColor = colors.fourthText,
                focusedPlaceholderColor = colors.thirdText,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}