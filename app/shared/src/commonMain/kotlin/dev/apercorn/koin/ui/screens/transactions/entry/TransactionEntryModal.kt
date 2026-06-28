package dev.apercorn.koin.ui.screens.transactions.entry

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.QuestionMark
import dev.apercorn.koin.core.domain.model.CategoryType
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
	data object CategoryPicker : EntrySheetRoute()
	data object Review : EntrySheetRoute()
}

private val emptyParty = PartyUiModel(
	id = "",
	label = "Select",
	icon = TablerIcons.Outlined.QuestionMark,
	roleTag = "—"
)

private fun PartySelection?.toPartyUiModel(): PartyUiModel = when (this) {
	is PartySelection.Account -> PartyUiModel(
		id = account.id,
		label = account.name,
		icon = IconProvider.resolve(account.iconName),
		roleTag = "ACCOUNT"
	)
	is PartySelection.IncomeCategory -> PartyUiModel(
		id = category.id,
		label = category.name,
		icon = IconProvider.resolve(category.iconName),
		roleTag = "INCOME"
	)
	is PartySelection.ExpenseCategory -> PartyUiModel(
		id = category.id,
		label = category.name,
		icon = IconProvider.resolve(category.iconName),
		roleTag = "EXPENSE"
	)
	null -> emptyParty
}

@Composable
fun Screen.TransactionEntryModal(onDismiss: () -> Unit) {
	val viewModel = koinScreenModel<TransactionEntryViewModel>()
	val state by viewModel.state.collectAsState()
	val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
	val navController = remember { SheetNavController(EntrySheetRoute.EntryForm) }

	val dateLabel = remember(state.date) {
		when (state.date) {
			today -> "Today"
			today.minus(1, DateTimeUnit.DAY) -> "Yesterday"
			today.plus(1, DateTimeUnit.DAY) -> "Tomorrow"
			else -> "${state.date.dayOfMonth} ${
				state.date.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
			}"
		}
	}

	val fromParty = remember(state.fromSelection) { state.fromSelection.toPartyUiModel() }
	val toParty = remember(state.toSelection) { state.toSelection.toPartyUiModel() }

	// derive review params from selections
	val reviewAccount = remember(state.fromSelection, state.toSelection) {
		val from = state.fromSelection
		val to = state.toSelection
		when {
			from is PartySelection.Account -> from.account
			to is PartySelection.Account -> to.account
			else -> null
		}
	}
	val reviewCategory = remember(state.fromSelection, state.toSelection) {
		val from = state.fromSelection
		val to = state.toSelection
		when {
			from is PartySelection.IncomeCategory -> from.category
			to is PartySelection.ExpenseCategory -> to.category
			else -> null
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
						onToClick = { navController.push(EntrySheetRoute.CategoryPicker) }
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
						onConfirm = {
							viewModel.evaluateExpression()
							navController.push(EntrySheetRoute.Review)
						},
						onAdjustClick = viewModel::onAdjustClick
					)
				}

				EntrySheetRoute.AccountPicker -> {
					val initialTab = when (state.fromSelection) {
						is PartySelection.IncomeCategory -> 1
						else -> 0
					}
					AccountPickerContent(
						accounts = state.accounts.map { AccountWithBalance(it, 0L) },
						incomeCategories = state.categories.filter { it.type == CategoryType.INCOME },
						initialTab = initialTab,
						onAccountSelected = { account ->
							viewModel.setFromAccount(account)
							navController.pop()
						},
						onIncomeCategorySelected = { category ->
							viewModel.setFromIncomeCategory(category)
							navController.pop()
						}
					)
				}

				EntrySheetRoute.CategoryPicker -> {
					val initialTab = when (state.toSelection) {
						is PartySelection.Account -> 1
						else -> 0
					}
					CategoryPickerContent(
						categories = state.categories,
						accounts = state.accounts.map { AccountWithBalance(it, 0L) },
						initialTab = initialTab,
						onCategorySelected = { category ->
							viewModel.setToExpenseCategory(category)
							navController.pop()
						},
						onAccountSelected = { account ->
							viewModel.setToAccount(account)
							navController.pop()
						}
					)
				}

				EntrySheetRoute.Review -> {
					TransactionReviewContent(
						date = state.date,
						account = reviewAccount,
						category = reviewCategory,
						transactionType = state.transactionType,
						amountCents = state.amountCents,
						currencyCode = state.currencyCode,
						title = state.title,
						onTitleChange = viewModel::setTitle,
						onBack = { navController.pop() },
						onSave = {
							viewModel.saveTransaction()
							onDismiss()
						}
					)
				}
			}
		}
	}
}