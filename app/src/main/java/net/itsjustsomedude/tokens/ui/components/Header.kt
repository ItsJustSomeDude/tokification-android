package net.itsjustsomedude.tokens.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.itsjustsomedude.tokens.ui.theme.TokificationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(
	title: @Composable () -> Unit,
	navigation: @Composable () -> Unit = {},
	actions: @Composable (RowScope.() -> Unit) = {},
	content: @Composable ColumnScope.() -> Unit
) {
	TokificationTheme {
		Scaffold(
			topBar = {
				TopAppBar(
					title = title,
					navigationIcon = navigation,

					actions = actions,
					colors = TopAppBarDefaults.topAppBarColors(
						containerColor = MaterialTheme.colorScheme.primaryContainer,
						titleContentColor = MaterialTheme.colorScheme.primary,
					),
				)
			}
		) { paddingValues ->
			Column(
				modifier = Modifier
					.verticalScroll(rememberScrollState())
					.padding(
						PaddingValues(
							start = 8.dp,
							end = 8.dp,
							top = paddingValues.calculateTopPadding() + 8.dp,
							bottom = paddingValues.calculateBottomPadding(),
						)
					)
					.fillMaxSize(),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				content()
			}
		}
	}
}