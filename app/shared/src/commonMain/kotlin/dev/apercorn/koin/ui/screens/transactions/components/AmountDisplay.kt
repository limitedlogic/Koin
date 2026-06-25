package dev.apercorn.koin.ui.screens.transactions.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AmountDisplay(
	rawExpression: String,
	modifier: Modifier = Modifier
) {
	Text(
		text = rawExpression,
		style = MaterialTheme.typography.displayLarge.copy(
			fontSize = 36.sp,
			fontWeight = FontWeight.Bold
		),
		color = MaterialTheme.colorScheme.onSurface,
		textAlign = TextAlign.Center,
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 8.dp)
	)
}