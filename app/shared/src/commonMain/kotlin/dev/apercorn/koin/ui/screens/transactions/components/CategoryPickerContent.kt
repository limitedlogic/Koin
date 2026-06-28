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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.apercorn.koin.core.domain.model.Account
import dev.apercorn.koin.core.domain.model.Category
import dev.apercorn.koin.core.domain.model.CategoryType
import dev.apercorn.koin.ui.screens.accounts.AccountWithBalance
import dev.apercorn.koin.ui.screens.accounts.components.AccountListContent
import dev.apercorn.koin.ui.util.IconProvider

@Composable
fun CategoryPickerContent(
	categories: List<Category>,
	accounts: List<AccountWithBalance>,
	onCategorySelected: (Category) -> Unit,
	onAccountSelected: (Account) -> Unit,
	modifier: Modifier = Modifier,
	initialTab: Int = 0
) {
	var selectedTab by remember(initialTab) { mutableStateOf(initialTab) }
	val tabs = listOf("Expense", "Banking")

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
			0 -> {
				// Expense categories
				val expenseCategories = categories.filter { it.type == CategoryType.EXPENSE }
				Column(
					modifier = Modifier.padding(horizontal = 16.dp),
					verticalArrangement = Arrangement.spacedBy(8.dp)
				) {
					expenseCategories.forEach { category ->
						val icon = IconProvider.resolve(category.iconName)
						Row(
							modifier = Modifier
								.fillMaxWidth()
								.clip(RoundedCornerShape(12.dp))
								.background(MaterialTheme.colorScheme.surface)
								.clickable { onCategorySelected(category) }
								.padding(horizontal = 16.dp, vertical = 14.dp),
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.spacedBy(12.dp)
						) {
							Icon(
								imageVector = icon,
								contentDescription = category.name,
								tint = MaterialTheme.colorScheme.primary,
								modifier = Modifier.size(22.dp)
							)
							Text(
								text = category.name,
								style = MaterialTheme.typography.bodyLarge,
								color = MaterialTheme.colorScheme.onSurface,
								modifier = Modifier.weight(1f)
							)
						}
					}
				}
			}

			1 -> {
				// Banking accounts (for transfers)
				AccountListContent(
					accounts = accounts,
					onAccountClick = onAccountSelected,
					modifier = Modifier.padding(horizontal = 16.dp)
				)
			}
		}
	}
}