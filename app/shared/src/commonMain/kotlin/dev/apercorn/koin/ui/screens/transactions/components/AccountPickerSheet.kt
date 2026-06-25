package dev.apercorn.koin.ui.screens.transactions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.apercorn.koin.core.domain.model.Account
import dev.apercorn.koin.ui.screens.accounts.AccountWithBalance
import dev.apercorn.koin.ui.screens.accounts.components.AccountListContent

@Composable
fun AccountPickerContent(
	accounts: List<AccountWithBalance>,
	onAccountSelected: (Account) -> Unit,
	modifier: Modifier = Modifier
) {
	var selectedTab by remember { mutableStateOf(0) }
	val tabs = listOf("Banking", "External")

	Column(modifier = modifier.fillMaxWidth()) {
		// Tab pills
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
			verticalAlignment = Alignment.CenterVertically
		) {
			tabs.forEachIndexed { index, title ->
				val isSelected = selectedTab == index
				Text(
					text = title,
					style = MaterialTheme.typography.labelLarge,
					fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
					color = if (isSelected)
						MaterialTheme.colorScheme.onSurface
					else
						MaterialTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier
						.clip(RoundedCornerShape(50))
						.background(
							if (isSelected)
								MaterialTheme.colorScheme.tertiaryContainer
							else
								MaterialTheme.colorScheme.secondaryContainer
						)
						.clickable { selectedTab = index }
						.padding(horizontal = 20.dp, vertical = 10.dp)
				)
			}
		}

		Spacer(modifier = Modifier.height(12.dp))

		when (selectedTab) {
			0 -> AccountListContent(
				accounts = accounts,
				onAccountClick = onAccountSelected,
				modifier = Modifier.padding(horizontal = 16.dp)
			)

			1 -> Text(
				text = "No external accounts yet",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
			)
		}
	}
}