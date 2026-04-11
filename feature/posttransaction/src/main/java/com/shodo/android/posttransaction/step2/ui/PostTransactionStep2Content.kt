package com.shodo.android.posttransaction.step2.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardOptions.Companion.Default
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults.colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction.Companion.Done
import androidx.compose.ui.text.input.ImeAction.Companion.Next
import androidx.compose.ui.text.input.KeyboardType.Companion.Number
import com.shodo.android.coreui.R
import com.shodo.android.coreui.theme.PokeManiacTheme.colors
import com.shodo.android.coreui.theme.PokeManiacTheme.dimens
import com.shodo.android.coreui.theme.PokeManiacTheme.typography
import com.shodo.android.coreui.ui.PrimaryButton
import com.shodo.android.domain.repositories.entities.NewActivityType
import com.shodo.android.domain.repositories.entities.NewActivityType.Purchase
import com.shodo.android.domain.repositories.entities.NewActivityType.Sale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostTransactionStep2Content(
    modifier: Modifier = Modifier,
    imageUri: Uri,
    onSaveActivity: (pokemonName: String, pokemonNumber: Int, transactionType: NewActivityType, transactionPrice: Int, uri: Uri) -> Unit
) {
    // Focus
    val focusManager = LocalFocusManager.current

    val numberFocusRequester = remember { FocusRequester() }
    val nameFocusRequester = remember { FocusRequester() }
    val priceFocusRequester = remember { FocusRequester() }

    // Fields
    var pokemonNumber by remember { mutableStateOf("") }
    var pokemonName by remember { mutableStateOf("") }
    var transactionPrice by remember { mutableStateOf("") }

    val transactionTypes = listOf(
        stringResource(R.string.new_post_step_2_transaction_type_purchase),
        stringResource(R.string.new_post_step_2_transaction_type_sale)
    )
    var selectedTransactionType by remember { mutableStateOf(transactionTypes[0]) }

    val textFieldColors = colors(
        focusedContainerColor = colors.backgroundCell,
        unfocusedContainerColor = colors.backgroundCell,
        cursorColor = colors.backgroundCell,
        focusedLabelColor = Transparent,
        unfocusedPlaceholderColor = colors.fourthText,
        focusedPlaceholderColor = colors.thirdText,
        focusedIndicatorColor = Transparent,
        unfocusedIndicatorColor = Transparent
    )

    Column(
        modifier = Modifier
            .padding(horizontal = dimens.standard)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Start,
        verticalArrangement = spacedBy(dimens.standard)
    ) {
        Text(
            text = stringResource(R.string.new_post_step_2_title),
            modifier = modifier.padding(top = dimens.standard),
            color = colors.primaryText,
            style = typography.t4
        )

        // Pokemon Number
        TransactionField(
            title = stringResource(R.string.new_post_step_2_pokemon_number),
            value = pokemonNumber,
            placeholder = stringResource(R.string.new_post_step_2_pokemon_number_placeholder),
            textFieldColors = textFieldColors,
            currentFocus = numberFocusRequester,
            keyboardOptions = Default.copy(keyboardType = Number, imeAction = Next),
            keyboardActions = KeyboardActions(onNext = { nameFocusRequester.requestFocus() }),
        ) { if (it.all { char -> char.isDigit() }) pokemonNumber = it }

        // Pokemon Name
        TransactionField(
            title = stringResource(R.string.new_post_step_2_pokemon_name),
            value = pokemonName,
            placeholder = stringResource(R.string.new_post_step_2_pokemon_name_placeholder),
            textFieldColors = textFieldColors,
            currentFocus = nameFocusRequester,
            keyboardOptions = Default.copy(imeAction = Next),
            keyboardActions = KeyboardActions(onNext = { priceFocusRequester.requestFocus() })
        ) { pokemonName = it }

        // Transaction Price
        TransactionField(
            title = stringResource(R.string.new_post_step_2_price),
            value = transactionPrice,
            placeholder = stringResource(R.string.new_post_step_2_price_placeholder),
            textFieldColors = textFieldColors,
            currentFocus = priceFocusRequester,
            keyboardOptions = Default.copy(keyboardType = Number, imeAction = Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        ) { if (it.all { char -> char.isDigit() }) transactionPrice = it }

        // Transaction Type
        TransactionTypeDropdown(
            modifier = modifier,
            transactionTypes = transactionTypes,
            selectedTransactionType = selectedTransactionType,
            textFieldColors = textFieldColors
        ) { selectedTransactionType = it }

        // Save Button
        if (pokemonName.isNotEmpty() && pokemonNumber.isNotEmpty() && transactionPrice.isNotEmpty()) {
            SaveTransactionButton(
                onSaveActivity = onSaveActivity,
                pokemonName = pokemonName,
                pokemonNumber = try { pokemonNumber.toInt() } catch (_: NumberFormatException) { 0 },
                transactionType = if (transactionTypes[0] == selectedTransactionType) Purchase else Sale,
                transactionPrice = try { transactionPrice.toInt() } catch (_: NumberFormatException) { 0 },
                imageUri = imageUri
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.TransactionField(
    title: String,
    value: String,
    placeholder: String,
    textFieldColors: TextFieldColors,
    currentFocus: FocusRequester,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    onValueChange: (String) -> Unit
) {
    Text(
        text = title,
        modifier = Modifier.padding(top = dimens.large),
        color = colors.primaryText,
        style = typography.t7
    )
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        textStyle = typography.t7.copy(color = colors.primaryText),
        colors = textFieldColors,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(currentFocus),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.TransactionTypeDropdown(
    modifier: Modifier,
    transactionTypes: List<String>,
    selectedTransactionType: String,
    textFieldColors: TextFieldColors,
    onSelectTransactionType: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Text(
        text = stringResource(R.string.new_post_step_2_transaction_type),
        modifier = Modifier.padding(top = dimens.large),
        color = colors.primaryText,
        style = typography.t7
    )
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth(),
            readOnly = true,
            value = selectedTransactionType,
            onValueChange = {},
            placeholder = { Text(stringResource(R.string.new_post_step_2_transaction_type)) },
            textStyle = typography.t7.copy(color = colors.primaryText),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = colors.primaryText,
                    modifier = modifier.rotate(if (expanded) 180f else 0f)
                )
            },
            colors = textFieldColors
        )

        DropdownMenu(
            modifier = Modifier.background(color = colors.backgroundCell),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            transactionTypes.forEach { label ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            modifier = modifier,
                            color = colors.primaryText,
                            style = typography.t5
                        )
                    },
                    onClick = {
                        onSelectTransactionType(label)
                        expanded = false
                    },
                    modifier = Modifier.background(color = colors.backgroundCell)
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.SaveTransactionButton(
    onSaveActivity: (pokemonName: String, pokemonNumber: Int, transactionType: NewActivityType, transactionPrice: Int, uri: Uri) -> Unit,
    pokemonName: String,
    pokemonNumber: Int,
    transactionType: NewActivityType, transactionPrice: Int,
    imageUri: Uri
) {
    PrimaryButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = dimens.large,
                start = dimens.small,
                end = dimens.small,
                bottom = dimens.xxLarge
            ),
        text = stringResource(R.string.next),
        onClick = {
            onSaveActivity(
                pokemonName,
                pokemonNumber,
                transactionType,
                transactionPrice,
                imageUri
            )
        }
    )
}