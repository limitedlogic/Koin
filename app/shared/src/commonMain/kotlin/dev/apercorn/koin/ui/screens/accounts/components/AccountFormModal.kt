package dev.apercorn.koin.ui.screens.accounts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.*
import dev.apercorn.koin.core.domain.model.AccountType
import dev.apercorn.koin.core.util.CurrencyInfo
import dev.apercorn.koin.ui.components.ColorIcon
import dev.apercorn.koin.ui.components.SmallPillButton
import dev.apercorn.koin.ui.components.form.*
import dev.apercorn.koin.ui.components.modal.*
import dev.apercorn.koin.ui.util.IconProvider

@Composable
fun AccountFormModal(
	showForm: Boolean,
	onDismiss: () -> Unit,
	onCreateAccount: (name: String, description: String?, type: AccountType, currency: String, initialBalanceCents: Long, iconName: String, colorHex: String) -> Unit
) {
	var showIconPicker by remember { mutableStateOf(false) }
	var showTypePicker by remember { mutableStateOf(false) }
	var showCurrencyPicker by remember { mutableStateOf(false) }
	var accountName by remember { mutableStateOf("") }
	var selectedIconName by remember { mutableStateOf("building-bank") }
	var selectedType by remember { mutableStateOf(AccountType.Checkings) }
	var selectedCurrency by remember { mutableStateOf(CurrencyInfo.find("USD")) }
	var showDescription by remember { mutableStateOf(false) }
	var accountDescription by remember { mutableStateOf("") }
	var showBalanceField by remember { mutableStateOf(false) }
	var balanceText by remember { mutableStateOf("") }

	if (showForm) {
		val typeInfo = when (selectedType) {
			AccountType.Cash -> Triple(
				TablerIcons.Outlined.Cash,
				"Physical cash",
				"Track cash on hand for daily spending"
			)

			AccountType.Checkings -> Triple(
				TablerIcons.Outlined.CreditCard,
				"Best for everyday spending",
				"Use checking accounts for regular expenses, bills, and day-to-day transactions."
			)

			AccountType.Savings -> Triple(
				TablerIcons.Outlined.Coin,
				"Save for the future",
				"Set aside money for goals and earn interest over time"
			)
		}

		FullscreenModal(
			title = "New Account",
			onDismiss = onDismiss,
			actionText = "Create Account",
			actionEnabled = accountName.isNotBlank(),
			onAction = {
				onCreateAccount(
					accountName,
					accountDescription.takeIf { it.isNotBlank() },
					selectedType,
					selectedCurrency?.code ?: "USD",
					centsFromDisplayAmount(balanceText, selectedCurrency),
					selectedIconName,
					"#26547B"
				)
				accountName = ""
				accountDescription = ""
				balanceText = ""
				showDescription = false
				showBalanceField = false
				selectedIconName = "building-bank"
				selectedType = AccountType.Checkings
			}
		) {
			// Icon picker
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 20.dp),
				contentAlignment = Alignment.Center
			) {
				ColorIcon(
					imageVector = IconProvider.resolve(selectedIconName),
					onClick = { showIconPicker = true },
					size = 120.dp,
					color = MaterialTheme.colorScheme.primary,
					contentDescription = "Account icon"
				)
			}

			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(10.dp)
			) {
				// Account name
				TextField(
					label = "Account name",
					value = accountName,
					onValueChange = { accountName = it },
					placeholder = "A short, descriptive name",
					maxLength = 25
				)

				if (showDescription) {
					Spacer(modifier = Modifier.height(10.dp))
					
					TextField(
						label = "Description",
						value = accountDescription,
						onValueChange = { accountDescription = it },
						placeholder = "A short note about this account",
						maxLength = 100
					)
				}

				SmallPillButton(
					text = if (showDescription) "Remove" else "Add description",
					leadingIcon = if (showDescription) TablerIcons.Outlined.Minus else TablerIcons.Outlined.Plus,
					onClick = { showDescription = !showDescription }
				)
			}

			// Account type dropdown
			DropdownField(
				label = "Account type",
				selectedValue = selectedType.name,
				leadingIcon = {
					ColorIcon(
						imageVector = TablerIcons.Outlined.BuildingBank,
						onClick = { showTypePicker = true },
						size = 40.dp,
						color = MaterialTheme.colorScheme.primary,
						contentDescription = null
					)
				},
				onClick = { showTypePicker = true }
			)

			// Info box for selected type
			InfoBox(
				title = typeInfo.second,
				description = typeInfo.third,
				iconPadding = PaddingValues(horizontal = 8.dp),
				leadingIcon = {
					Icon(
						imageVector = TablerIcons.Outlined.InfoSquare,
						contentDescription = null,
						tint = MaterialTheme.colorScheme.onSecondaryContainer,
						modifier = Modifier.size(30.dp)
					)
				}
			)

			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(10.dp)
			) {
				// Currency dropdown
				DropdownField(
					label = "Currency",
					selectedValue = "${selectedCurrency?.code} — ${selectedCurrency?.name}",
					leadingIcon = {
						ColorIcon(
							imageVector = TablerIcons.Outlined.Coin,
							onClick = { showCurrencyPicker = true },
							size = 40.dp,
							color = MaterialTheme.colorScheme.secondary,
							contentDescription = null
						)
					},
					onClick = { showCurrencyPicker = true }
				)

				if (showBalanceField) {
					Spacer(modifier = Modifier.height(10.dp))

					CurrencyField(
						label = "Initial balance",
						value = balanceText,
						onValueChange = { balanceText = it },
						currencySymbol = selectedCurrency?.symbol ?: "$"
					)
				}

				SmallPillButton(
					text = if (showBalanceField) "Remove" else "Set initial balance",
					leadingIcon = if (showBalanceField) TablerIcons.Outlined.Minus else TablerIcons.Outlined.CurrencyDollar,
					onClick = { showBalanceField = !showBalanceField }
				)
			}
		}
	}

	if (showIconPicker) {
		ModalBottomSheet(onDismiss = { showIconPicker = false }) {
			Text(
				text = "Choose an icon",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
				color = MaterialTheme.colorScheme.onSurface
			)
			Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
				IconPickerGrid(
					selectedIconName = selectedIconName,
					onIconSelected = { name ->
						selectedIconName = name
						showIconPicker = false
					}
				)
			}
		}
	}

	if (showTypePicker) {
		FloatingModal(
			title = "Account type",
			onDismiss = { showTypePicker = false }
		) {
			AccountType.entries.forEach { type ->
				val isSelected = selectedType == type
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.clickable {
							selectedType = type
							showTypePicker = false
						}
						.padding(vertical = 14.dp, horizontal = 8.dp),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(12.dp)
				) {
					RadioButton(
						selected = isSelected,
						onClick = {
							selectedType = type
							showTypePicker = false
						}
					)
					Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
						Text(
							text = type.name,
							style = MaterialTheme.typography.bodyLarge,
							fontWeight = FontWeight.Medium,
							color = MaterialTheme.colorScheme.onSurface
						)
						Text(
							text = when (type) {
								AccountType.Cash -> "Physical cash on hand"
								AccountType.Checkings -> "Everyday spending & bills"
								AccountType.Savings -> "Save money & earn interest"
							},
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
					}
				}
			}
		}
	}
if (showCurrencyPicker) {
		FloatingModal(
			title = "Select currency",
			onDismiss = { showCurrencyPicker = false },
			maxHeight = 480.dp
		) {
			// Search button (placeholder — not wired yet)
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.height(48.dp)
					.clip(MaterialTheme.shapes.medium)
					.background(MaterialTheme.colorScheme.secondaryContainer)
					.padding(horizontal = 16.dp),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(12.dp)
			) {
				Icon(
					imageVector = TablerIcons.Outlined.Search,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier.size(20.dp)
				)
				Text(
					text = "Search currencies",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
				)
			}

			Spacer(modifier = Modifier.height(8.dp))

			// Currency list
			CurrencyInfo.ALL.values.sortedBy { it.code }.forEach { currency ->
				val isSelected = selectedCurrency?.code == currency.code
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.height(56.dp)
						.clickable {
							selectedCurrency = currency
							showCurrencyPicker = false
						}
						.padding(horizontal = 8.dp),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(12.dp)
				) {
					Text(
						text = currency.symbol,
						style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
						color = MaterialTheme.colorScheme.primary,
						modifier = Modifier.width(40.dp)
					)
					Column(modifier = Modifier.weight(1f)) {
						Text(
							text = currency.code,
							style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
							color = MaterialTheme.colorScheme.onSurface
						)
						Text(
							text = currency.name,
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
					}
					if (isSelected) {
						Icon(
							imageVector = TablerIcons.Outlined.Check,
							contentDescription = "Selected",
							tint = MaterialTheme.colorScheme.primary,
							modifier = Modifier.size(24.dp)
						)
					}
				}
			}
		}
	}
}

private fun centsFromDisplayAmount(text: String, currency: CurrencyInfo?): Long {
	if (text.isEmpty()) return 0L
	val info = currency ?: CurrencyInfo.findOrDefault("USD")
	return when (info.decimalDigits) {
		0 -> text.toLongOrNull() ?: 0L
		3 -> (text.toDoubleOrNull()?.times(1000)?.toLong() ?: 0L)
		else -> (text.toDoubleOrNull()?.times(100)?.toLong() ?: 0L)
	}
}
