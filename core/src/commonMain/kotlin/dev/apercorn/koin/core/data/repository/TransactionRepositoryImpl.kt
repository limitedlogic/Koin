package dev.apercorn.koin.core.data.repository

import dev.apercorn.koin.core.data.database.daos.CategorySpending
import dev.apercorn.koin.core.data.database.daos.TransactionDao
import dev.apercorn.koin.core.data.database.entities.TransactionEntity
import dev.apercorn.koin.core.domain.model.Transaction
import dev.apercorn.koin.core.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock.System
import kotlinx.datetime.LocalDate

class TransactionRepositoryImpl(
	private val transactionDao: TransactionDao
) : TransactionRepository {

	override fun getAllTransactions(): Flow<List<Transaction>> {
		return transactionDao.getAllTransactions().map { entities ->
			entities.map { it.toDomain() }
		}
	}

	override fun getTransactionsByAccount(accountId: String): Flow<List<Transaction>> {
		return transactionDao.getTransactionsByAccount(accountId).map { entities ->
			entities.map { it.toDomain() }
		}
	}

	override fun getTransactionsFiltered(
		accountId: String?,
		categoryId: String?,
		startDate: String?,
		endDate: String?,
		type: String?
	): Flow<List<Transaction>> {
		return transactionDao.getTransactionsFiltered(accountId, categoryId, startDate, endDate, type)
			.map { entities -> entities.map { it.toDomain() } }
	}

	override suspend fun getTransactionById(id: String): Transaction? {
		return transactionDao.getTransactionById(id)?.toDomain()
	}

	override suspend fun save(transaction: Transaction) {
		transactionDao.insert(transaction.toEntity())
	}

	override suspend fun delete(id: String) {
		transactionDao.deleteById(id)
	}

	override fun getBalanceForAccount(accountId: String): Flow<Long> {
		return transactionDao.getBalanceForAccount(accountId)
	}

	override fun getTotalBalance(): Flow<Long> {
		return transactionDao.getTotalBalance()
	}

	override fun getSpendingByCategory(
		startDate: String,
		endDate: String
	): Flow<List<CategorySpending>> {
		return transactionDao.getSpendingByCategory(startDate, endDate)
	}

	override fun getTotalExpensesInPeriod(startDate: String, endDate: String): Flow<Long> {
		return transactionDao.getTotalExpensesInPeriod(startDate, endDate)
	}

	override fun getTotalIncomeInPeriod(startDate: String, endDate: String): Flow<Long> {
		return transactionDao.getTotalIncomeInPeriod(startDate, endDate)
	}

	private fun TransactionEntity.toDomain(): Transaction {
		return if (recurringId != null) {
			Transaction.Recurring(
				id = id,
				accountId = accountId,
				categoryId = categoryId,
				counterpartyId = counterpartyId,
				linkedAccountId = linkedAccountId,
				amount = amount,
				currency = currency,
				type = TransactionType.fromString(type),
				date = LocalDate.parse(date),
				title = title,
				note = note,
				tags = tags,
				recurringId = recurringId
			)
		} else {
			Transaction.OneOff(
				id = id,
				accountId = accountId,
				categoryId = categoryId,
				counterpartyId = counterpartyId,
				linkedAccountId = linkedAccountId,
				amount = amount,
				currency = currency,
				type = TransactionType.fromString(type),
				date = LocalDate.parse(date),
				title = title,
				note = note,
				tags = tags
			)
		}
	}

	private fun Transaction.toEntity(): TransactionEntity {
		val now = System.now().toEpochMilliseconds()
		val (recurringId, isRecurring) = when (this) {
			is Transaction.Recurring -> this.recurringId to true
			is Transaction.OneOff -> null to false
		}
		return TransactionEntity(
			id = id,
			accountId = accountId,
			categoryId = categoryId,
			counterpartyId = counterpartyId,
			linkedAccountId = linkedAccountId,
			amount = amount,
			currency = currency,
			type = TransactionType.toString(type),
			date = date.toString(),
			title = title,
			note = note,
			tags = tags,
			isRecurring = isRecurring,
			recurringId = recurringId,
			createdAt = now,
			updatedAt = now
		)
	}
}