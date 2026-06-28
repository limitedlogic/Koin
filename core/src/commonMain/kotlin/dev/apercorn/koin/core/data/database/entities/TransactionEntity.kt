package dev.apercorn.koin.core.data.database.entities

import androidx.room.*

@Entity(
	tableName = "transactions",
	foreignKeys = [
		ForeignKey(
			entity = AccountEntity::class,
			parentColumns = ["id"],
			childColumns = ["accountId"],
			onDelete = ForeignKey.CASCADE
		),
		ForeignKey(
			entity = CategoryEntity::class,
			parentColumns = ["id"],
			childColumns = ["categoryId"],
			onDelete = ForeignKey.SET_NULL
		),
		ForeignKey(
			entity = AccountEntity::class,
			parentColumns = ["id"],
			childColumns = ["linkedAccountId"],
			onDelete = ForeignKey.SET_NULL
		)
	],
	indices = [
		Index(value = ["accountId"]),
		Index(value = ["categoryId"]),
		Index(value = ["linkedAccountId"]),
		Index(value = ["date"])
	]
)
data class TransactionEntity(
	@PrimaryKey val id: String,
	val accountId: String,
	val categoryId: String? = null,
	val counterpartyId: String? = null,
	val linkedAccountId: String? = null,
	val amount: Long, // stored in minor units, always positive magnitude
	val currency: String,
	val type: String, // INCOME, EXPENSE, TRANSFER, ADJUSTMENT
	val date: String, // ISO LocalDate string
	val title: String? = null,
	val note: String? = null,
	val tags: String? = null, // JSON array of tag IDs
	val isRecurring: Boolean = false,
	val recurringId: String? = null,
	val createdAt: Long,
	val updatedAt: Long
)