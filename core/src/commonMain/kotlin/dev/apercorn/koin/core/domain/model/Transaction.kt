package dev.apercorn.koin.core.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
 * Transaction type determines the flow of money:
 * - INCOME: money flows from an income category TO a bank account (accountId = destination)
 * - EXPENSE: money flows from a bank account TO an expense category (accountId = source)
 * - TRANSFER: money flows from one bank account TO another (accountId = source, linkedAccountId = destination)
 * - ADJUSTMENT: balance correction on a single account (accountId = target, no category)
 *
 * Amounts are always stored as positive magnitudes (cents). The type field determines
 * the sign in balance calculations.
 */
@Serializable
sealed interface Transaction {
	/** Unique identifier for the transaction */
	val id: String

	/** ID of the account this transaction belongs to */
	val accountId: String

	/** ID of the category */
	val categoryId: String?

	/** ID of the counterparty */
	val counterpartyId: String?

	/** ID of the linked account (for TRANSFER destination) */
	val linkedAccountId: String?

	/** Transaction amount in cents/smallest currency unit (always positive magnitude) */
	val amount: Long

	/** ISO currency code for the transaction */
	val currency: String

	/** Type of transaction (INCOME, EXPENSE, TRANSFER, ADJUSTMENT) */
	val type: TransactionType

	/** Date when the transaction occurred */
	val date: LocalDate

	/** User-facing title for the transaction */
	val title: String?

	/** Optional note or description for the transaction */
	val note: String?

	/** JSON array of tag IDs */
	val tags: String?

	@Serializable
	data class OneOff(
		override val id: String,
		override val accountId: String,
		override val categoryId: String? = null,
		override val counterpartyId: String? = null,
		override val linkedAccountId: String? = null,
		override val amount: Long,
		override val currency: String,
		override val type: TransactionType,
		override val date: LocalDate,
		override val title: String? = null,
		override val note: String? = null,
		override val tags: String? = null
	) : Transaction

	@Serializable
	data class Recurring(
		override val id: String,
		override val accountId: String,
		override val categoryId: String? = null,
		override val counterpartyId: String? = null,
		override val linkedAccountId: String? = null,
		override val amount: Long,
		override val currency: String,
		override val type: TransactionType,
		override val date: LocalDate,
		override val title: String? = null,
		override val note: String? = null,
		override val tags: String? = null,
		/** ID of the recurring transaction that generated this transaction */
		val recurringId: String
	) : Transaction
}

enum class TransactionType {
	INCOME,
	EXPENSE,
	TRANSFER,
	ADJUSTMENT;

	companion object {
		fun fromString(value: String): TransactionType = when (value.uppercase()) {
			"INCOME" -> INCOME
			"EXPENSE" -> EXPENSE
			"TRANSFER" -> TRANSFER
			"ADJUSTMENT" -> ADJUSTMENT
			else -> EXPENSE
		}

		fun toString(type: TransactionType): String = type.name
	}
}