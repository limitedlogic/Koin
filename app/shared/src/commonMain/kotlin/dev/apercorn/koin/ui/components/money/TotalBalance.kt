package dev.apercorn.koin.ui.components.money

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.apercorn.koin.core.util.CurrencyFormatter
import dev.apercorn.koin.ui.theme.KoinTheme

@Composable
fun TotalBalance(
	totalBalance: Long,
	currency: String,
	modifier: Modifier = Modifier
) {
	Box(modifier = modifier.fillMaxWidth()) {
		Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
			Text(
				text = CurrencyFormatter.format(totalBalance, currency),
				style = MaterialTheme.typography.displayLarge,
				color = MaterialTheme.colorScheme.onSurface
			)

			Row(
				horizontalArrangement = Arrangement.spacedBy(10.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = "+$0.00 (0.00%)",
					style = MaterialTheme.typography.displaySmall,
					color = KoinTheme.colors.profit
				)
				Text(
					text = "last week",
					style = MaterialTheme.typography.labelMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
		}
	}
}