package net.itsjustsomedude.tokens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import net.itsjustsomedude.tokens.models.MainScreenViewModel
import net.itsjustsomedude.tokens.ui.CoopInfo
import net.itsjustsomedude.tokens.ui.EventEdit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(model: MainScreenViewModel = viewModel()) {
    val context = LocalContext.current as? ComponentActivity

    println("Model: $model")

    Scaffold(
        topBar = {
            TopAppBar(
//                navigationIcon = {
//                    IconButton(onClick = {
//                        context?.finish()
//                    }) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Back"
//                        )
//                    }
//                },
                title = { Text("Tokification") },
                actions = {
                    IconButton(onClick = {
                        context?.startActivity(Intent(context, ListCoops2::class.java))
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Create Coop"
                        )
                    }

                    IconButton(onClick = {
                        model.insertEvent()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Create Event"
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
        val coop by model.selectedCoop.observeAsState()

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            coop?.let {
                println("Drawing Coop Info")
                CoopInfo(coopId = it.id)
            } ?: Text(
                text = "No Co-op Selected.",
                style = MaterialTheme.typography.titleLarge
            )

            EventEdit(7)
        }
    }
}
