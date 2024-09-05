package net.itsjustsomedude.tokens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.models.CoopListViewModel
import net.itsjustsomedude.tokens.ui.CoopList


class ListCoops2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content()
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(model: CoopListViewModel = viewModel()) {
    val context = LocalContext.current as? ComponentActivity

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        context?.finish()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = { Text("Select Coop") },
                actions = {
                    IconButton(onClick = {
                        model.insert(Coop())
                        //TODO: no idea...
                        context?.finish()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Coop"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
            )
        }
    ) { paddingValues ->
        CoopList(
            modifier = Modifier.padding(paddingValues),
            onSelect = {
                println("Thing Selected.")

                // TODO: This must be better...
                model.setSelected(it)
                context?.finish()
            })
    }
}
