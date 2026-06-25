package dev.apercorn.koin.ui.screens.transactions

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.apercorn.koin.core.data.repository.*
import dev.apercorn.koin.core.domain.model.Transaction
import dev.apercorn.koin.core.domain.model.TransactionType
import dev.apercorn.koin.core.util.DateUtils.startOfMonth
import dev.apercorn.koin.core.util.DateUtils.startOfQuarter
import dev.apercorn.koin.core.util.DateUtils.startOfWeek
import dev.apercorn.koin.core.util.DateUtils.today
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*

enum class DateRangeMode { WEEK, MONTH, QUARTER, YEAR, ALL_TIME, CUSTOM }

data class TransactionScreenState(
	val transactions: List<Transaction> = emptyList(),
	val filterType: TransactionType? = null,
	val filterAccountId: String? = null,
	val searchQuery: String = "",
	val isLoading: Boolean = true,
	val totalBalance: Long = 0L,
	val currency: String = "USD",
	val rangeStart: LocalDate = today(),
	val rangeEnd: LocalDate = today(),
	val rangeMode: DateRangeMode = DateRangeMode.MONTH,
	val canGoBack: Boolean = true,
	val canGoForward: Boolean = false
) {
	val startMonthLabel: String get() = rangeStart.month.name.uppercase().take(3)
	val startDateLabel: String get() = rangeStart.dayOfMonth.toString()
	val endMonthLabel: String get() = rangeEnd.month.name.uppercase().take(3)
	val endDateLabel: String get() = rangeEnd.dayOfMonth.toString()
}

class TransactionViewModel(
	private val transactionRepository: TransactionRepository,
	private val accountRepository: AccountRepository,
	private val categoryRepository: CategoryRepository
) : ScreenModel {

	private val _state = MutableStateFlow(TransactionScreenState())
	val state: StateFlow<TransactionScreenState> = _state.asStateFlow()

	init {
		loadTransactions()
		loadTotalBalance()
	}

	private fun loadTransactions() {
		screenModelScope.launch {
			transactionRepository.getAllTransactions().collect { transactions ->
				_state.update { it.copy(transactions = transactions, isLoading = false) }
			}
		}
	}

	private fun loadTotalBalance() {
		screenModelScope.launch {
			transactionRepository.getTotalBalance().collect { total ->
				_state.update { it.copy(totalBalance = total) }
			}
		}
	}

	fun onPrev() {
		_state.update { s ->
			val interval = s.rangeStart.until(s.rangeEnd, DateTimeUnit.DAY)
			val newEnd = s.rangeStart.minus(1, DateTimeUnit.DAY)
			val newStart = newEnd.minus(interval, DateTimeUnit.DAY)
			s.copy(rangeStart = newStart, rangeEnd = newEnd, canGoForward = true)
		}
	}

	fun onNext() {
		_state.update { s ->
			val today = today()
			val interval = s.rangeStart.until(s.rangeEnd, DateTimeUnit.DAY)
			val newStart = s.rangeEnd.plus(1, DateTimeUnit.DAY)
			val newEnd = newStart.plus(interval, DateTimeUnit.DAY)
			s.copy(
				rangeStart = newStart,
				rangeEnd = newEnd,
				canGoForward = newEnd < today
			)
		}
	}

	fun onStartDatePicked(date: LocalDate) {
		_state.update { s ->
			val end = if (date > s.rangeEnd) date.plus(1, DateTimeUnit.DAY) else s.rangeEnd
			s.copy(rangeStart = date, rangeEnd = end, rangeMode = DateRangeMode.CUSTOM)
		}
	}

	fun onEndDatePicked(date: LocalDate) {
		_state.update { s ->
			val start = if (date < s.rangeStart) date.minus(1, DateTimeUnit.DAY) else s.rangeStart
			s.copy(rangeStart = start, rangeEnd = date, rangeMode = DateRangeMode.CUSTOM)
		}
	}

	fun onRangeModeChanged(mode: DateRangeMode) {
		_state.update { s ->
			val today = today()
			when (mode) {
				DateRangeMode.WEEK -> {
					val start = startOfWeek(today)
					s.copy(rangeStart = start, rangeEnd = today, rangeMode = mode, canGoForward = false)
				}
				DateRangeMode.MONTH -> {
					val start = startOfMonth(today)
					s.copy(rangeStart = start, rangeEnd = today, rangeMode = mode, canGoForward = false)
				}
				DateRangeMode.QUARTER -> {
					val start = startOfQuarter(today)
					s.copy(rangeStart = start, rangeEnd = today, rangeMode = mode, canGoForward = false)
				}
				DateRangeMode.YEAR -> {
					val start = LocalDate(today.year, 1, 1)
					s.copy(rangeStart = start, rangeEnd = today, rangeMode = mode, canGoForward = false)
				}
				DateRangeMode.ALL_TIME -> {
					s.copy(rangeMode = mode, canGoForward = false, canGoBack = false)
				}
				DateRangeMode.CUSTOM -> s.copy(rangeMode = mode)
			}
		}
	}

	fun onReset() {
		_state.update { s ->
			val today = today()
			when (s.rangeMode) {
				DateRangeMode.WEEK -> {
					val start = startOfWeek(today)
					s.copy(rangeStart = start, rangeEnd = today, canGoForward = false)
				}
				DateRangeMode.MONTH -> {
					val start = startOfMonth(today)
					s.copy(rangeStart = start, rangeEnd = today, canGoForward = false)
				}
				DateRangeMode.QUARTER -> {
					val start = startOfQuarter(today)
					s.copy(rangeStart = start, rangeEnd = today, canGoForward = false)
				}
				DateRangeMode.YEAR -> {
					val start = LocalDate(today.year, 1, 1)
					s.copy(rangeStart = start, rangeEnd = today, canGoForward = false)
				}
				DateRangeMode.ALL_TIME -> s
				DateRangeMode.CUSTOM -> s
			}
		}
	}

	fun setFilterType(type: TransactionType?) {
		_state.update { it.copy(filterType = type) }
	}

	fun setFilterAccount(accountId: String?) {
		_state.update { it.copy(filterAccountId = accountId) }
	}

	fun setSearchQuery(query: String) {
		_state.update { it.copy(searchQuery = query) }
	}

	fun deleteTransaction(id: String) {
		screenModelScope.launch {
			transactionRepository.delete(id)
		}
	}

}