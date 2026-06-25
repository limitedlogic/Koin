package dev.apercorn.koin.ui.screens.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.*
import dev.apercorn.koin.core.domain.model.TransactionType
import dev.apercorn.koin.core.util.CurrencyFormatter
import dev.apercorn.koin.ui.components.layout.ActionItem
import dev.apercorn.koin.ui.components.layout.ScreenLayout
import dev.apercorn.koin.ui.components.layout.TopBar
import dev.apercorn.koin.ui.components.money.TotalBalance
import dev.apercorn.koin.ui.screens.transactions.components.DateRangeModeSheet
import dev.apercorn.koin.ui.screens.transactions.components.DateRangeSelector
import dev.apercorn.koin.ui.screens.transactions.entry.TransactionEntryModal
import kotlinx.datetime.*

object TransactionScreen : Screen {

	@OptIn(ExperimentalMaterial3Api::class)
	@Composable
	override fun Content() {
		val viewModel = koinScreenModel<TransactionViewModel>()
		val state by viewModel.state.collectAsState()
		var showAddSheet by remember { mutableStateOf(false) }
		var showStartPicker by remember { mutableStateOf(false) }
		var showEndPicker by remember { mutableStateOf(false) }
		var showModeSheet by remember { mutableStateOf(false) }

		ScreenLayout(
			topBar = {
				TopBar(
					title = "Transactions",
					actions = listOf(
						ActionItem(
							icon = TablerIcons.Outlined.Plus,
							contentDescription = "Add transaction",
							onClick = { showAddSheet = true }
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
			LazyColumn(
				verticalArrangement = Arrangement.spacedBy(8.dp),
				modifier = Modifier.fillMaxSize()
			) {
				// Total balance
				item {
					TotalBalance(
						totalBalance = state.totalBalance,
						currency = state.currency
					)
				}

				item {
					Spacer(modifier = Modifier.height(8.dp))
				}

				// Date range selector
				item {
					DateRangeSelector(
						startMonthLabel = state.startMonthLabel,
						startDateLabel = state.startDateLabel,
						endMonthLabel = state.endMonthLabel,
						endDateLabel = state.endDateLabel,
						canGoBack = state.canGoBack,
						canGoForward = state.canGoForward,
						onPrev = viewModel::onPrev,
						onNext = viewModel::onNext,
						onStartClick = { showStartPicker = true },
						onEndClick = { showEndPicker = true },
						onEditClick = { showModeSheet = true },
						onResetClick = viewModel::onReset
					)
				}

				item {
					Spacer(modifier = Modifier.height(16.dp))
				}

				// Transaction list
				items(state.transactions) { transaction ->
					TransactionItem(transaction = transaction)
				}

				item {
					Spacer(modifier = Modifier.height(140.dp))
				}
			}
		}

		// Start date picker
		if (showStartPicker) {
			val datePickerState = rememberDatePickerState(
				initialSelectedDateMillis = state.rangeStart
					.atTime(0, 0, 0)
					.toInstant(TimeZone.currentSystemDefault())
					.toEpochMilliseconds()
			)
			DatePickerDialog(
				onDismissRequest = { showStartPicker = false },
				confirmButton = {
					TextButton(onClick = {
						datePickerState.selectedDateMillis?.let { millis ->
							val date = Instant.fromEpochMilliseconds(millis)
								.toLocalDateTime(TimeZone.currentSystemDefault()).date
							viewModel.onStartDatePicked(date)
						}
						showStartPicker = false
					}) { Text("OK") }
				},
				dismissButton = {
					TextButton(onClick = { showStartPicker = false }) { Text("Cancel") }
				}
			) {
				DatePicker(state = datePickerState)
			}
		}

		// End date picker
		if (showEndPicker) {
			val datePickerState = rememberDatePickerState(
				initialSelectedDateMillis = state.rangeEnd
					.atTime(0, 0, 0)
					.toInstant(TimeZone.currentSystemDefault())
					.toEpochMilliseconds()
			)
			DatePickerDialog(
				onDismissRequest = { showEndPicker = false },
				confirmButton = {
					TextButton(onClick = {
						datePickerState.selectedDateMillis?.let { millis ->
							val date = Instant.fromEpochMilliseconds(millis)
								.toLocalDateTime(TimeZone.currentSystemDefault()).date
							viewModel.onEndDatePicked(date)
						}
						showEndPicker = false
					}) { Text("OK") }
				},
				dismissButton = {
					TextButton(onClick = { showEndPicker = false }) { Text("Cancel") }
				}
			) {
				DatePicker(state = datePickerState)
			}
		}

		if (showAddSheet) {
			TransactionEntryModal(onDismiss = { showAddSheet = false })
		}

		if (showModeSheet) {
			DateRangeModeSheet(
				selectedMode = state.rangeMode,
				onModeSelected = { viewModel.onRangeModeChanged(it); showModeSheet = false },
				onDismiss = { showModeSheet = false }
			)
		}
	}
}

@Composable
private fun TransactionItem(
	transaction: dev.apercorn.koin.core.domain.model.Transaction
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 12.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = transaction.note ?: "${transaction.type.name} · ${transaction.date}",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurface
			)
			Text(
				text = CurrencyFormatter.format(transaction.amount, transaction.currency),
				style = MaterialTheme.typography.labelLarge,
				color = when (transaction.type) {
					TransactionType.INCOME -> MaterialTheme.colorScheme.primary
					TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
					TransactionType.TRANSFER -> MaterialTheme.colorScheme.onSurface
				}
			)
		}
	}
}
