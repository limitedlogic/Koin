package dev.apercorn.koin.ui.screens.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.*
import dev.apercorn.koin.ui.components.layout.*
import dev.apercorn.koin.ui.components.money.TotalBalance
import dev.apercorn.koin.ui.screens.accounts.components.*


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

			LazyColumn(
				verticalArrangement = Arrangement.spacedBy(12.dp),
				modifier = Modifier.fillMaxSize()
			) {
				item {
					AccountListContent(
						accounts = state.accounts,
						onAccountClick = { selectedAccount = AccountWithBalance(it, 0L) }
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
