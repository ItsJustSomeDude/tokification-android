package net.itsjustsomedude.tokens.ui
//
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.relocation.BringIntoViewRequester
//import androidx.compose.foundation.relocation.bringIntoViewRequester
//import androidx.compose.foundation.text.KeyboardActions
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.ExposedDropdownMenuBox
//import androidx.compose.material3.ExposedDropdownMenuDefaults
//import androidx.compose.material3.MenuAnchorType
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.focus.FocusRequester
//import androidx.compose.ui.focus.focusRequester
//import androidx.compose.ui.focus.onFocusChanged
//import androidx.compose.ui.platform.LocalFocusManager
//import androidx.compose.ui.platform.LocalSoftwareKeyboardController
//import androidx.compose.ui.text.input.ImeAction
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//
//@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
//@Composable
//fun SearchableDropdown(
//    items: List<String>,
//    selectedItem: String,
//    onItemSelected: (String) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var expanded by remember { mutableStateOf(false) }
//    var searchQuery by remember { mutableStateOf("") }
//    var currentItems by remember { mutableStateOf(items) }
//
//
//    val filteredItems = currentItems.filter { it.contains(searchQuery, ignoreCase = true) }
//
//    val focusRequester = remember { FocusRequester() }
//    val bringRequester = remember { BringIntoViewRequester() }
//
//    val keyboardController = LocalSoftwareKeyboardController.current
//    val focusManager = LocalFocusManager.current
//
//    ExposedDropdownMenuBox(
//        modifier = modifier,
//        expanded = expanded,
//        onExpandedChange = {
//            expanded = !expanded
//        }
//    ) {
//
//        TextField(
//            modifier = Modifier
//                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
//                .fillMaxWidth()
//                .bringIntoViewRequester(bringRequester)
//                .focusRequester(focusRequester)
//                .onFocusChanged { focusState ->
//                    // When only 1 option left when we lose focus, selected it.
//                    if (!focusState.isFocused) {
//                        // Whenever we lose focus, always hide the dropdown
//                        expanded = false
//
//                        when (filteredItems.size) {
//                            // Auto select the single option
//                            1 -> if (filteredItems.first() != selectedItem) {
//                                onOptionSelected(filteredOptions.first())
//                            }
//                            // Nothing to we can auto select - reset our text input to the selected value
//                            else -> textInput = selectedOptionText
//                        }
//                    } else {
//                        // When focused:
//                        // Ensure field is visible by scrolling to it
//                        coroutineScope.launch {
//                            bringIntoViewRequester.bringIntoView()
//                        }
//                        // Show the dropdown right away
//                        dropDownExpanded = true
//                    }
//                },
//            ,
//
//            singleLine = true,
//            value = searchQuery,
//            onValueChange = {
//                expanded = true
//                searchQuery = it
//            },
//            label = { Text("Search Player") },
//            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
//            colors = ExposedDropdownMenuDefaults.textFieldColors(),
//            keyboardOptions = KeyboardOptions.Default.copy(
//                imeAction = ImeAction.Done
//            ),
//            keyboardActions = KeyboardActions(onDone = { expanded = false })
//        )
//        ExposedDropdownMenu(
////            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable),
//            expanded = expanded,
//            onDismissRequest = { expanded = false },
//        ) {
//            filteredItems.forEach { item ->
//                DropdownMenuItem(
//                    text = { Text(item) },
//                    onClick = {
//                        onItemSelected(item)
//                        searchQuery = ""
//                        expanded = false
//                    }
//                )
//            }
////            players.forEach { player ->
////                DropdownMenuItem(
////                    text = { Text(text = player) },
////                    onClick = {
////                        customPlayerMode = false
////                        selectedPlayer = player
////                        playerMenuExpanded = false
////
////                        onChanged(
////                            event.copy(
////                                person = player
////                            )
////                        )
////                    },
////                )
////            }
//
////            DropdownMenuItem(
////                text = {
////                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
////                        Icon(
////                            painter = painterResource(R.drawable.offline_bolt),
////                            contentDescription = ""
////                        )
////                        Text(text = "Sink", fontStyle = FontStyle.Italic)
////                    }
////                },
////                onClick = {
////                    customPlayerMode = false
////                    selectedPlayer = "Sink"
////                    playerMenuExpanded = false
////                },
////            )
//
////            DropdownMenuItem(
////                text = {
////                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
////                        Icon(
////                            imageVector = Icons.Default.Edit,
////                            contentDescription = ""
////                        )
////                        Text(text = "Add a player", fontStyle = FontStyle.Italic)
////                    }
////                },
////                onClick = {
////                    selectedPlayer = "Add a player"
////                    playerMenuExpanded = false
////                    customPlayerMode = true
////                },
////            )
//        }
//    }
//
//
////    Column(modifier = modifier) {
////        // TextField for search
////        TextField(
////            value = searchQuery,
////            onValueChange = { searchQuery = it },
////            modifier = Modifier.fillMaxWidth(),
////            placeholder = { Text("Search or add item") },
////            singleLine = true
////        )
////
////        Spacer(modifier = Modifier.height(8.dp))
////
////        // Dropdown button
////        Button(
////            onClick = { expanded = true },
////            modifier = Modifier.fillMaxWidth()
////        ) {
////            Text("Select an item")
////        }
////
////        // Dropdown menu
////        DropdownMenu(
////            expanded = expanded,
////            onDismissRequest = { expanded = false }
////        ) {
////            // Show "Add Option" if the search query isn't an existing item
////            if (searchQuery.isNotBlank() && !currentItems.contains(searchQuery)) {
////                DropdownMenuItem(
////                    text = { Text("Add \"$searchQuery\"") },
////                    onClick = {
////                        currentItems = currentItems + searchQuery
////                        onItemSelected(searchQuery)
////                        searchQuery = ""
////                        expanded = false
////                    }
////                )
////            }
////
////            // Display filtered items
////            filteredItems.forEach { item ->
////                DropdownMenuItem(
////                    text = { Text(item) },
////                    onClick = {
////                        onItemSelected(item)
////                        searchQuery = ""
////                        expanded = false
////                    }
////                )
////            }
////        }
////    }
//}
//
//@Preview(showBackground = true, device = "id:pixel_5", showSystemUi = true)
//@Composable
//fun PreviewSearchableDropdown() {
//    val items = listOf("Apple", "Banana", "Orange", "Grapes")
//    SearchableDropdown(
//        items = items,
//        onItemSelected = { selected -> println("Selected: $selected") },
//        modifier = Modifier
//            .padding(16.dp)
//            .fillMaxWidth()
//    )
//}