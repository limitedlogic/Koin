package dev.apercorn.koin.ui.screens.transactions.entry

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.apercorn.koin.core.data.repository.AccountRepository
import dev.apercorn.koin.core.data.repository.CategoryRepository
import dev.apercorn.koin.core.data.repository.TransactionRepository
import dev.apercorn.koin.core.domain.model.Account
import dev.apercorn.koin.core.domain.model.Category
import dev.apercorn.koin.core.domain.model.TransactionType
import dev.apercorn.koin.core.util.CurrencyInfo
import dev.apercorn.koin.ui.screens.transactions.components.NumpadKey
import dev.apercorn.koin.ui.screens.transactions.components.Op
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*

data class TransactionEntryState(
	val date: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
	val accounts: List<Account> = emptyList(),
	val categories: List<Category> = emptyList(),
	val selectedAccount: Account? = null,
	val selectedCategory: Category? = null,
	val transactionType: TransactionType = TransactionType.EXPENSE,
	val rawExpression: String = "",
	val currencyCode: String = "USD",
	val confirmEnabled: Boolean = false,
	val isLoading: Boolean = true
)

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
				_state.update {
					it.copy(
						accounts = accounts,
						categories = categories,
						selectedAccount = accounts.firstOrNull(),
						selectedCategory = categories.firstOrNull { cat -> cat.type.name == "EXPENSE" },
						currencyCode = accounts.firstOrNull()?.currency ?: "USD",
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

	fun setAccount(account: Account) {
		_state.update {
			it.copy(
				selectedAccount = account,
				currencyCode = account.currency
			)
		}
	}

	fun setCategory(category: Category) {
		_state.update { it.copy(selectedCategory = category) }
	}

	fun setTransactionType(type: TransactionType) {
		_state.update { it.copy(transactionType = type) }
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
			is NumpadKey.Confirm -> confirm()
		}
		updateDisplay()
	}

	fun onConfirm() {
		confirm()
		updateDisplay()
		saveTransaction()
	}

	private fun appendDigit(digit: Int) {
		// prevent leading zeros
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
			// if we already have a pending operator, evaluate first
			if (pendingOperator != null && leftOperand != null) {
				evaluate()
			}
			leftOperand = centsFromCurrentInput()
			pendingOperator = op
			currentInput = ""
		} else if (leftOperand != null) {
			// change operator without new input
			pendingOperator = op
		}
	}

	private fun backspace() {
		if (currentInput.isNotEmpty()) {
			currentInput = currentInput.dropLast(1)
		} else {
			// no input — cancel pending operator
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
		// if operator pending, complete evaluation
		if (pendingOperator != null && leftOperand != null && currentInput.isNotEmpty()) {
			evaluate()
		} else if (leftOperand != null && pendingOperator != null) {
			// operator pending but no right operand — just cancel the op
			pendingOperator = null
		}

		// if no operator and no left operand, take current input as the value
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
					// user started typing a new right operand after operator was already cleared
				}
				append(currentInput)
			}
		}
		val enabled = leftOperand != null || currentInput.isNotEmpty()
		_state.update { it.copy(rawExpression = display.ifEmpty { "0" }, confirmEnabled = enabled) }
	}

	private fun saveTransaction() {
		val state = _state.value
		val totalCents = leftOperand ?: 0L
		val account = state.selectedAccount ?: return

		if (totalCents <= 0) return

		screenModelScope.launch {
			val transaction = dev.apercorn.koin.core.domain.model.Transaction.OneOff(
				id = com.benasher44.uuid.uuid4().toString(),
				accountId = account.id,
				categoryId = state.selectedCategory?.id,
				amount = when (state.transactionType) {
					TransactionType.INCOME -> totalCents
					TransactionType.EXPENSE -> -totalCents
					TransactionType.TRANSFER -> -totalCents
				},
				currency = state.currencyCode,
				type = state.transactionType,
				date = state.date
			)
			transactionRepository.save(transaction)
			resetForm()
		}
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
				// whole units → cents
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