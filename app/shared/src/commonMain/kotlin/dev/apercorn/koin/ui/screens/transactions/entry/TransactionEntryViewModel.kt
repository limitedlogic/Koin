package dev.apercorn.koin.ui.screens.transactions.entry

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.apercorn.koin.core.data.repository.AccountRepository
import dev.apercorn.koin.core.data.repository.CategoryRepository
import dev.apercorn.koin.core.data.repository.TransactionRepository
import dev.apercorn.koin.core.domain.model.Account
import dev.apercorn.koin.core.domain.model.Category
import dev.apercorn.koin.core.domain.model.CategoryType
import dev.apercorn.koin.core.domain.model.TransactionType
import dev.apercorn.koin.core.util.CurrencyInfo
import dev.apercorn.koin.ui.screens.transactions.components.NumpadKey
import dev.apercorn.koin.ui.screens.transactions.components.Op
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*

sealed class PartySelection {
	data class Account(val account: dev.apercorn.koin.core.domain.model.Account) : PartySelection()
	data class IncomeCategory(val category: Category) : PartySelection()
	data class ExpenseCategory(val category: Category) : PartySelection()
}

data class TransactionEntryState(
	val date: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
	val accounts: List<Account> = emptyList(),
	val categories: List<Category> = emptyList(),
	val fromSelection: PartySelection? = null,
	val toSelection: PartySelection? = null,
	val rawExpression: String = "0",
	val amountCents: Long = 0L,
	val currencyCode: String = "USD",
	val title: String = "",
	val confirmEnabled: Boolean = false,
	val isLoading: Boolean = true
) {
	// computed
	val transactionType: TransactionType
		get() = when {
			fromSelection is PartySelection.Account && toSelection is PartySelection.ExpenseCategory -> TransactionType.EXPENSE
			fromSelection is PartySelection.IncomeCategory && toSelection is PartySelection.Account -> TransactionType.INCOME
			fromSelection is PartySelection.Account && toSelection is PartySelection.Account -> TransactionType.TRANSFER
			else -> TransactionType.EXPENSE
		}
}

class TransactionEntryViewModel(
	private val accountRepository: AccountRepository,
	private val categoryRepository: CategoryRepository,
	private val transactionRepository: TransactionRepository
) : ScreenModel {

	private val _state = MutableStateFlow(TransactionEntryState())
	val state: StateFlow<TransactionEntryState> = _state.asStateFlow()

	// expression state machine
	private var leftOperand: Long? = null
	private var pendingOperator: Op? = null
	private var currentInput: String = ""

	init {
		loadData()
	}

	private fun loadData() {
		screenModelScope.launch {
			combine(
				accountRepository.getAllAccounts(),
				categoryRepository.getAllCategories()
			) { accounts, categories ->
				Pair(accounts, categories)
			}.collect { (accounts, categories) ->
				val firstAccount = accounts.firstOrNull()
				val firstExpense = categories.firstOrNull { it.type == CategoryType.EXPENSE }
				_state.update {
					it.copy(
						accounts = accounts,
						categories = categories,
						fromSelection = firstAccount?.let { a -> PartySelection.Account(a) },
						toSelection = firstExpense?.let { c -> PartySelection.ExpenseCategory(c) },
						currencyCode = firstAccount?.currency ?: "USD",
						isLoading = false
					)
				}
			}
		}
	}

	fun onPrevDay() {
		_state.update { it.copy(date = it.date.minus(1, DateTimeUnit.DAY)) }
	}

	fun onNextDay() {
		val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
		val next = _state.value.date.plus(1, DateTimeUnit.DAY)
		if (next <= today) {
			_state.update { it.copy(date = next) }
		}
	}

	fun onPickerOpen() {
		// TODO: open calendar picker sheet
	}

	fun setFromAccount(account: Account) {
		_state.update {
			it.copy(
				fromSelection = PartySelection.Account(account),
				currencyCode = account.currency
			)
		}
	}

	fun setFromIncomeCategory(category: Category) {
		_state.update {
			it.copy(fromSelection = PartySelection.IncomeCategory(category))
		}
	}

	fun setToExpenseCategory(category: Category) {
		_state.update {
			it.copy(toSelection = PartySelection.ExpenseCategory(category))
		}
	}

	fun setToAccount(account: Account) {
		_state.update {
			it.copy(toSelection = PartySelection.Account(account))
		}
	}

	fun setTitle(title: String) {
		_state.update { it.copy(title = title) }
	}

	fun onAdjustClick() {
		// TODO: open tax/discount/tip picker
	}

	fun onKey(key: NumpadKey) {
		when (key) {
			is NumpadKey.Digit -> appendDigit(key.value)
			is NumpadKey.Decimal -> appendDecimal()
			is NumpadKey.Operator -> applyOperator(key.op)
			is NumpadKey.Backspace -> backspace()
			is NumpadKey.CurrencyToggle -> cycleCurrency()
			is NumpadKey.Confirm -> evaluateExpression()
		}
		updateDisplay()
	}

	/** Finalize the expression and update [TransactionEntryState.amountCents]. */
	fun evaluateExpression() {
		confirm()
		updateDisplay()
	}

	fun saveTransaction() {
		val state = _state.value
		val totalCents = leftOperand ?: 0L

		if (totalCents <= 0) return

		val (accountId, categoryId, linkedAccountId) = when {
			state.fromSelection is PartySelection.Account && state.toSelection is PartySelection.ExpenseCategory -> Triple(
				state.fromSelection.account.id,
				state.toSelection.category.id,
				null
			)
			state.fromSelection is PartySelection.IncomeCategory && state.toSelection is PartySelection.Account -> Triple(
				state.toSelection.account.id,
				state.fromSelection.category.id,
				null
			)
			state.fromSelection is PartySelection.Account && state.toSelection is PartySelection.Account -> Triple(
				state.fromSelection.account.id,
				null,
				state.toSelection.account.id
			)
			else -> return
		}

		screenModelScope.launch {
			val transaction = dev.apercorn.koin.core.domain.model.Transaction.OneOff(
				id = com.benasher44.uuid.uuid4().toString(),
				accountId = accountId,
				categoryId = categoryId,
				linkedAccountId = linkedAccountId,
				amount = totalCents,
				currency = state.currencyCode,
				type = state.transactionType,
				date = state.date,
				title = state.title.takeIf { it.isNotBlank() }
			)
			transactionRepository.save(transaction)
			resetForm()
		}
	}

	private fun appendDigit(digit: Int) {
		if (currentInput == "0") {
			currentInput = digit.toString()
		} else {
			currentInput += digit.toString()
		}
	}

	private fun appendDecimal() {
		if (!currentInput.contains('.')) {
			if (currentInput.isEmpty()) {
				currentInput = "0."
			} else {
				currentInput += "."
			}
		}
	}

	private fun applyOperator(op: Op) {
		if (currentInput.isNotEmpty()) {
			if (pendingOperator != null && leftOperand != null) {
				evaluate()
			}
			leftOperand = centsFromCurrentInput()
			pendingOperator = op
			currentInput = ""
		} else if (leftOperand != null) {
			pendingOperator = op
		}
	}

	private fun backspace() {
		if (currentInput.isNotEmpty()) {
			currentInput = currentInput.dropLast(1)
		} else {
			pendingOperator = null
		}
	}

	private fun evaluate() {
		val right = centsFromCurrentInput()
		val left = leftOperand ?: return
		val op = pendingOperator ?: return

		val result = when (op) {
			Op.ADD -> left + right
			Op.SUBTRACT -> left - right
			Op.MULTIPLY -> left * right
			Op.DIVIDE -> if (right != 0L) left / right else 0L
		}
		leftOperand = result
		pendingOperator = null
		currentInput = ""
	}

	private fun cycleCurrency() {
		val accounts = _state.value.accounts
		if (accounts.size <= 1) return
		val currentIdx = accounts.indexOfFirst { it.currency == _state.value.currencyCode }
		val nextIdx = (currentIdx + 1) % accounts.size
		val nextCurrency = accounts[nextIdx].currency
		_state.update { it.copy(currencyCode = nextCurrency) }
	}

	private fun confirm() {
		if (pendingOperator != null && leftOperand != null && currentInput.isNotEmpty()) {
			evaluate()
		} else if (leftOperand != null && pendingOperator != null) {
			pendingOperator = null
		}

		if (leftOperand == null && currentInput.isNotEmpty()) {
			leftOperand = centsFromCurrentInput()
		}
	}

	private fun updateDisplay() {
		val display = buildString {
			val left = leftOperand
			if (left != null) {
				val amount = displayFromCents(left)
				append(amount)
			}
			val op = pendingOperator
			if (op != null) {
				val opStr = when (op) {
					Op.ADD -> " + "
					Op.SUBTRACT -> " − "
					Op.MULTIPLY -> " × "
					Op.DIVIDE -> " ÷ "
				}
				append(opStr)
			}
			if (currentInput.isNotEmpty()) {
				if (pendingOperator == null && leftOperand != null) {
				}
				append(currentInput)
			}
		}
		val enabled = leftOperand != null || currentInput.isNotEmpty()
		val totalCents = leftOperand ?: 0L
		_state.update { it.copy(rawExpression = display.ifEmpty { "0" }, confirmEnabled = enabled, amountCents = totalCents) }
	}

	private fun resetForm() {
		currentInput = ""
		leftOperand = null
		pendingOperator = null
		_state.update { it.copy(rawExpression = "0", confirmEnabled = false) }
	}

	private fun centsFromCurrentInput(): Long {
		if (currentInput.isEmpty()) return 0L
		val info = CurrencyInfo.findOrDefault(_state.value.currencyCode)
		val hasDecimal = currentInput.contains('.')
		return when {
			!hasDecimal -> {
				val whole = currentInput.toLongOrNull() ?: 0L
				when (info.decimalDigits) {
					0 -> whole
					3 -> whole * 1000L
					else -> whole * 100L
				}
			}
			else -> {
				val parts = currentInput.split('.')
				val whole = parts[0].toLongOrNull() ?: 0L
				val fraction = parts.getOrElse(1) { "0" }.padEnd(info.decimalDigits, '0').take(info.decimalDigits)
				val divisor = when (info.decimalDigits) {
					3 -> 1000L
					else -> 100L
				}
				whole * divisor + fraction.toLongOrNull()!!
			}
		}
	}

	private fun displayFromCents(cents: Long): String {
		val currency = _state.value.currencyCode
		val info = CurrencyInfo.findOrDefault(currency)
		return if (info.decimalDigits == 0) {
			cents.toString()
		} else {
			val divisor = when (info.decimalDigits) {
				3 -> 1000L
				else -> 100L
			}
			val whole = cents / divisor
			val fraction = (cents % divisor).toString().padStart(info.decimalDigits, '0')
			"$whole.$fraction"
		}
	}
}