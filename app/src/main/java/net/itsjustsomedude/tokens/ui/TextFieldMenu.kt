package net.itsjustsomedude.tokens.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.ui.theme.TokificationTheme

/** A text field that allows the user to type in to filter down options.
 * Source: https://stackoverflow.com/a/77353891
 */
@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class
)
@Composable
fun TextFieldMenu(
    modifier: Modifier = Modifier,
    /** The label for the text field */
    label: String,
    /** All the available options. */
    options: List<String>,
    /** The selected option. */
    selectedOption: String?,
    /** When the option is selected via tapping on the dropdown option or typing in the option. */
    onOptionSelected: (String?) -> Unit,
    bringIntoViewRequester: BringIntoViewRequester = remember { BringIntoViewRequester() },
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    val focusRequester = remember { FocusRequester() }
    var showAllOptions by remember { mutableStateOf(false) }
    var dropDownExpanded by remember { mutableStateOf(false) }

    // Default our text input to the selected option
    var textInput by remember(selectedOption) {
        mutableStateOf(TextFieldValue(selectedOption.orEmpty()))
    }

    // Update our filtered options everytime our text input changes
    val filteredOptions = remember(textInput, dropDownExpanded) {
        when (dropDownExpanded) {
            true -> options.filter { it.contains(textInput.text, ignoreCase = true) }
            // Skip filtering when we don't need to
            false -> emptyList()
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    ExposedDropdownMenuBox(
        expanded = dropDownExpanded,
        onExpandedChange = {
            dropDownExpanded = !dropDownExpanded

            if (dropDownExpanded)
                textInput = textInput.copy(
                    selection = TextRange(0, textInput.text.length),
                )
        },
        modifier = modifier,
    ) {
        // Text Input
        TextField(
            value = textInput,
            onValueChange = {
//                if (textInput.text == it.text) {
//                    println("Not a text change")
//                    textInput = it
//                    return@TextField
//                }

                // Dropdown may auto hide for scrolling but it's important it always shows when a user
                // does a search
                dropDownExpanded = true
                textInput = it

                // On re-focusing the box while a value is entered, all options will be shown.
                // Stop showing all the options upon typing something.
                if (textInput.text != selectedOption)
                    showAllOptions = textInput.text.isBlank()
            },
            modifier = Modifier
                // Match the parent width
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    println("Focus Changed! All: $showAllOptions")
                    // When only 1 option left when we lose focus, selected it.
                    if (!focusState.isFocused) {
                        // Whenever we lose focus, always hide the dropdown
                        dropDownExpanded = false

                        when (filteredOptions.size) {
                            // Auto accept what they typed.
                            0 -> if (textInput.text != selectedOption) {
                                onOptionSelected(textInput.text)
                            }
                            // Auto select the single option
                            1 -> if (filteredOptions.first() != selectedOption) {
                                onOptionSelected(filteredOptions.first())
                            } else {
                                // The value hasn't changed, show the selected.
                                textInput = textInput.copy(text = selectedOption.orEmpty())
                            }
                            // Nothing to we can auto select - reset our text input to the selected value
                            else -> textInput = textInput.copy(text = selectedOption.orEmpty())
                        }

                        println("Unfocus!")
                        showAllOptions = true
                    } else {
                        println("Focus! ${textInput.text.isNotEmpty()}")

                        // When focused:
                        // Select all the existing text if there is something already.
                        if (textInput.text.isNotEmpty())
                            textInput = textInput.copy(
                                selection = TextRange(0, textInput.text.length),
                            )

                        // Ensure field is visible by scrolling to it
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }

                        // If there's already a value, show all options.
                        showAllOptions = textInput.text.isNotEmpty()

                        // Show the dropdown right away
                        dropDownExpanded = true
                    }
                },
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropDownExpanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            keyboardOptions = KeyboardOptions(
                imeAction = when (filteredOptions.size) {
                    // We will either reset input or auto select the single option
                    0, 1 -> ImeAction.Done
                    // Keyboard will hide to make room for search results
                    else -> ImeAction.Search
                }
            ),
            singleLine = true,
            keyboardActions = KeyboardActions(onAny = {
                when (filteredOptions.size) {
                    // Remove focus to execute our onFocusChanged effect
                    0, 1 -> focusManager.clearFocus(force = true)
                    // Can't auto select option since we have a list, so hide keyboard to give more room for dropdown
                    else -> keyboardController?.hide()
                }
            })
        )

        // Dropdown
        if (dropDownExpanded) {
            val dropdownOptions = remember(textInput, showAllOptions) {
                if (textInput.text.isEmpty() || showAllOptions) {
                    // Show all options if nothing to filter yet
                    options
                } else {
                    filteredOptions
                }
            }

            ExposedDropdownMenu(
                expanded = dropDownExpanded,
                onDismissRequest = {
                    dropDownExpanded = false
                },
            ) {
                dropdownOptions.forEach { option ->
                    DropdownMenuItem(onClick = {
                        dropDownExpanded = false
                        onOptionSelected(option)
                        focusManager.clearFocus(force = true)
                    }, text = {
                        Text(option)
                    })
                }

                if (textInput.text.isNotEmpty() && !options.contains(textInput.text))
                    DropdownMenuItem(onClick = {
                        dropDownExpanded = false
                        onOptionSelected(textInput.text)
                        focusManager.clearFocus(force = true)
                    }, text = {
                        Text(text = "Add \"${textInput.text}\"", fontStyle = FontStyle.Italic)
                    })

            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true, heightDp = 200)
@Composable
private fun PreviewTextFieldMenu() {
    TokificationTheme {
        var selectedString by remember { mutableStateOf<String?>("") }
        val stringOptions = remember {
            listOf(
                "String A", "String B", "String C", "String D", "Last!!!", "String AAA",
//                "String A", "String B", "String C", "String D", "Last!!!", "String AAA",
//                "String A", "String B", "String C", "String D", "Last!!!", "String AAA",
//                "String A", "String B", "String C", "String D", "Last!!!", "String AAA",
//                "String A", "String B", "String C", "String D", "Last!!!", "String AAA",
//                "String A", "String B", "String C", "String D", "Last!!!", "String AAA",
//                "String A", "String B", "String C", "String D", "Last!!!", "String AAA",
//                "String A", "String B", "String C", "String D", "Last!!!", "String AAA",
//                "String A", "String B", "String C", "String D", "Last!!!", "String AAA",
//                "String A", "String B", "String C", "String D", "Last!!!", "String AAA"
            )
        }

        Column(
            modifier = Modifier
                // Reduce column height when keyboard is shown
                // Note: This needs to be set _before_ verticalScroll so that BringIntoViewRequester APIs work
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val nameFocusRequester = remember { FocusRequester() }
            val optionsFocusRequester = remember { FocusRequester() }

            var nameInput by remember { mutableStateOf("") }

            Text(text = "Model Value: $selectedString", color = Color.White)

            // Free Style Input
            OutlinedTextField(
                modifier = Modifier
                    .focusRequester(nameFocusRequester)
                    .fillMaxWidth(),
                label = {
                    Text(
                        text = "Name",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                },
                value = nameInput,
                onValueChange = { nameInput = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { optionsFocusRequester.requestFocus() },
                ),
            )

            TextFieldMenu(
                label = "Testing!",
                selectedOption = selectedString,
                onOptionSelected = { selectedString = it },
                options = stringOptions
            )
        }
    }
}