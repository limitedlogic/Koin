package dev.apercorn.koin.ui.screens.transactions.entry

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.ChevronLeft
import dev.seyfarth.tablericons.outlined.QuestionMark
import dev.apercorn.koin.core.domain.model.TransactionType
import dev.apercorn.koin.ui.components.CircularIcon
import dev.apercorn.koin.ui.components.modal.ModalBottomSheet
import dev.apercorn.koin.ui.components.modal.SheetNavController
import dev.apercorn.koin.ui.screens.transactions.components.*
import dev.apercorn.koin.ui.screens.accounts.AccountWithBalance
import dev.apercorn.koin.ui.theme.KoinTheme
import dev.apercorn.koin.ui.util.IconProvider
import kotlinx.datetime.*

private sealed class EntrySheetRoute {
	data object EntryForm : EntrySheetRoute()
	data object AccountPicker : EntrySheetRoute()
}

@Composable
fun Screen.TransactionEntryModal(onDismiss: () -> Unit) {
	val viewModel = koinScreenModel<TransactionEntryViewModel>()
	val state by viewModel.state.collectAsState()
	val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
	val navController = remember { SheetNavController(EntrySheetRoute.EntryForm) }

	// date label — single source of truth
	val dateLabel = remember(state.date) {
		when (state.date) {
			today -> "Today"
			today.minus(1, DateTimeUnit.DAY) -> "Yesterday"
			today.plus(1, DateTimeUnit.DAY) -> "Tomorrow"
			else -> "${state.date.dayOfMonth} ${state.date.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)}"
		}
	}

	// PartyUiModel derivation — single source of truth
	val fromParty = remember(state.selectedAccount) {
		val account = state.selectedAccount
		if (account != null) {
			PartyUiModel(
				id = account.id,
				label = account.name,
				icon = IconProvider.resolve(account.iconName),
				roleTag = "ACCOUNT"
			)
		} else {
			PartyUiModel(
				id = "",
				label = "Select",
				icon = TablerIcons.Outlined.QuestionMark,
				roleTag = "ACCOUNT"
			)
		}
	}

	val toParty = remember(state.selectedCategory, state.transactionType) {
		val roleTag = when (state.transactionType) {
			TransactionType.EXPENSE -> "EXPENSE"
			TransactionType.INCOME -> "INCOME"
			TransactionType.TRANSFER -> "TRANSFER"
		}
		val category = state.selectedCategory
		if (category != null) {
			PartyUiModel(
				id = category.id,
				label = category.name,
				icon = IconProvider.resolve(category.iconName),
				roleTag = roleTag
			)
		} else {
			PartyUiModel(
				id = "",
				label = "Select",
				icon = TablerIcons.Outlined.QuestionMark,
				roleTag = roleTag
			)
		}
	}

	ModalBottomSheet(
		onDismiss = onDismiss,
		confirmDismiss = {
			if (navController.canPop) {
				navController.pop()
				false
			} else {
				true
			}
		},
		showGrabber = true,
		containerColor = KoinTheme.colors.modalOnBackground
	) {
		Column(
			modifier = Modifier
				.padding(horizontal = 16.dp)
				.animateContentSize()
		) {
			when (navController.current) {
				EntrySheetRoute.EntryForm -> {
					// date picker
					DatePicker(
						selectedDateLabel = dateLabel,
						canGoBack = true,
						canGoForward = state.date < today,
						onPrevDay = viewModel::onPrevDay,
						onNextDay = viewModel::onNextDay,
						onPickerOpen = viewModel::onPickerOpen
					)

					Spacer(modifier = Modifier.height(8.dp))

					// from → to party row
					TransactionFlow(
						fromParty = fromParty,
						toParty = toParty,
						onFromClick = { navController.push(EntrySheetRoute.AccountPicker) },
						onToClick = { /* TODO: open category picker */ },
						onArrowClick = { /* TODO: toggle transaction type */ }
					)

					Spacer(modifier = Modifier.height(12.dp))

					// amount display
					AmountDisplay(
						rawExpression = state.rawExpression
					)

					Spacer(modifier = Modifier.height(12.dp))

					// numpad
					Numpad(
						currencyCode = state.currencyCode,
						confirmEnabled = state.confirmEnabled,
						onKey = viewModel::onKey,
						onConfirm = viewModel::onConfirm,
						onAdjustClick = viewModel::onAdjustClick
					)
				}

				EntrySheetRoute.AccountPicker -> {
					AccountPickerContent(
						accounts = state.accounts.map { AccountWithBalance(it, 0L) },
						onAccountSelected = { account ->
							viewModel.setAccount(account)
							navController.pop()
						}
					)
				}
			}
		}
	}
}