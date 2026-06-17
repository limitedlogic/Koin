package dev.apercorn.koin.ui.screens.accounts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.*
import dev.apercorn.koin.core.util.CurrencyFormatter
import dev.apercorn.koin.ui.components.layout.*
import dev.apercorn.koin.ui.screens.accounts.components.*
import dev.apercorn.koin.ui.theme.KoinTheme

object AccountsScreen : Screen {

	@Composable
	override fun Content() {
		val viewModel = koinScreenModel<AccountsViewModel>()
		val state by viewModel.state.collectAsState()
		var showForm by remember { mutableStateOf(false) }
		var selectedAccount by remember { mutableStateOf<AccountWithBalance?>(null) }

		ScreenLayout(
			topBar = {
				TopBar(
					title = "Accounts",
					actions = listOf(
						ActionItem(
							icon = TablerIcons.Outlined.Plus,
							contentDescription = "Add account",
							onClick = { showForm = true }
						),
						ActionItem(
							icon = TablerIcons.Outlined.Dots,
							contentDescription = "More",
							onClick = { }
						)
					)
				)
			},
			isLoading = state.isLoading
		) {
			TotalBalance(
				totalBalance = state.totalBalance,
				currency = state.currency
			)

			Spacer(modifier = Modifier.height(30.dp))

			Text(
				text = "Savings Account",
				style = MaterialTheme.typography.labelLarge,
				color = MaterialTheme.colorScheme.onSurface,
			)

			Spacer(modifier = Modifier.height(15.dp))

			LazyColumn(
				verticalArrangement = Arrangement.spacedBy(12.dp),
				modifier = Modifier.fillMaxSize()
			) {
				items(state.accounts) { accountWithBalance ->
					AccountCard(
						accountWithBalance = accountWithBalance,
						onClick = { selectedAccount = accountWithBalance }
					)
				}

				// Extra spacing at the end of the list to allow scrolling past the floating nav bar
				item {
					Spacer(modifier = Modifier.height(140.dp))
				}
			}
		}

		AccountFormModal(
			showForm = showForm,
			onDismiss = { showForm = false },
			onCreateAccount = { name, description, type, currency, initialBalanceCents, iconName, colorHex ->
				viewModel.addAccount(name, description, type, currency, initialBalanceCents, iconName, colorHex)
				showForm = false
			}
		)

		selectedAccount?.let { accountWithBalance ->
			AccountDetailsSheet(
				onDismiss = { selectedAccount = null },
				account = accountWithBalance.account
			)
		}
	}
}

/**
 * Displays the total balance of all accounts
 */
@Composable
private fun TotalBalance(
	totalBalance: Long,
	currency: String
) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(10.dp)
		) {
			// Money display
			Text(
				text = CurrencyFormatter.format(totalBalance, currency),
				style = MaterialTheme.typography.displayLarge,
				color = MaterialTheme.colorScheme.onSurface
			)

			// Subtitle row
			Row(
				horizontalArrangement = Arrangement.spacedBy(10.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				// Absolute and percentage change
				Text(
					text = "+$0.00 (0.00%)",
					style = MaterialTheme.typography.displaySmall,
					color = KoinTheme.colors.profit
				)
				// Range
				Text(
					text = "last week",
					style = MaterialTheme.typography.labelMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
		}
	}
}
