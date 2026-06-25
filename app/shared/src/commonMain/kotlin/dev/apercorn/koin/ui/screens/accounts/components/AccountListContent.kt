package dev.apercorn.koin.ui.screens.accounts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.apercorn.koin.core.domain.model.Account
import dev.apercorn.koin.core.domain.model.AccountType
import dev.apercorn.koin.core.util.CurrencyFormatter
import dev.apercorn.koin.ui.screens.accounts.AccountWithBalance
import dev.apercorn.koin.ui.theme.KoinTheme

@Composable
fun AccountListContent(
	accounts: List<AccountWithBalance>,
	onAccountClick: (Account) -> Unit,
	modifier: Modifier = Modifier
) {
	val grouped = accounts.groupBy { it.account.type }

	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		AccountType.entries.forEach { type ->
			val typeAccounts = grouped[type].orEmpty()
			if (typeAccounts.isNotEmpty()) {
				AccountTypeSection(
					type = type,
					accounts = typeAccounts,
					onAccountClick = onAccountClick
				)
			}
		}
	}
}

@Composable
private fun AccountTypeSection(
	type: AccountType,
	accounts: List<AccountWithBalance>,
	onAccountClick: (Account) -> Unit
) {
	val totalBalance = accounts.sumOf { it.balance }
	val currency = accounts.firstOrNull()?.account?.currency ?: "USD"

	Column {
		// Section header: type name + cumulated balance
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 4.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = type.name,
				style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
				color = MaterialTheme.colorScheme.onSurface
			)
			Text(
				text = CurrencyFormatter.format(totalBalance, currency),
				style = MaterialTheme.typography.displaySmall,
				color = KoinTheme.colors.profit
			)
		}

		Spacer(modifier = Modifier.height(8.dp))

		// Account cards
		accounts.forEach { accountWithBalance ->
			AccountCard(
				accountWithBalance = accountWithBalance,
				onClick = { onAccountClick(accountWithBalance.account) }
			)
			Spacer(modifier = Modifier.height(8.dp))
		}
	}
}