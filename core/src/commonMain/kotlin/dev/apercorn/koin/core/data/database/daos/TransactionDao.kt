package dev.apercorn.koin.core.data.database.daos

import androidx.room.*
import dev.apercorn.koin.core.data.database.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
	@Query("SELECT * FROM transactions ORDER BY date DESC, createdAt DESC")
	fun getAllTransactions(): Flow<List<TransactionEntity>>

	@Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY date DESC, createdAt DESC")
	fun getTransactionsByAccount(accountId: String): Flow<List<TransactionEntity>>

	@Query("SELECT * FROM transactions WHERE id = :id")
	suspend fun getTransactionById(id: String): TransactionEntity?

	@Query(
		"""
        SELECT * FROM transactions 
        WHERE (:accountId IS NULL OR accountId = :accountId)
        AND (:categoryId IS NULL OR categoryId = :categoryId)
        AND (:startDate IS NULL OR date >= :startDate)
        AND (:endDate IS NULL OR date <= :endDate)
        AND (:type IS NULL OR type = :type)
        ORDER BY date DESC, createdAt DESC
        """
	)
	fun getTransactionsFiltered(
		accountId: String? = null,
		categoryId: String? = null,
		startDate: String? = null,
		endDate: String? = null,
		type: String? = null
	): Flow<List<TransactionEntity>>

	@Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE accountId = :accountId AND type = 'INCOME'")
	fun getTotalIncomeForAccount(accountId: String): Flow<Long>

	@Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE accountId = :accountId AND type = 'EXPENSE'")
	fun getTotalExpensesForAccount(accountId: String): Flow<Long>

	@Query(
		"""
        SELECT COALESCE(SUM(
            CASE WHEN type = 'INCOME' THEN amount 
            WHEN type = 'EXPENSE' THEN -amount 
            WHEN type = 'TRANSFER' AND accountId = :accountId THEN -amount
            WHEN type = 'ADJUSTMENT' THEN amount
            ELSE 0 END
        ), 0) FROM transactions WHERE accountId = :accountId
        """
	)
	fun getBalanceForAccount(accountId: String): Flow<Long>

	@Query(
		"""
        SELECT COALESCE(SUM(
            CASE WHEN type = 'INCOME' THEN amount 
            WHEN type = 'ADJUSTMENT' THEN amount
            ELSE -amount END
        ), 0) FROM transactions
        """
	)
	fun getTotalBalance(): Flow<Long>

	@Query(
		"""
        SELECT categoryId, COALESCE(SUM(amount), 0) as total 
        FROM transactions 
        WHERE type = 'EXPENSE' AND date >= :startDate AND date <= :endDate
        GROUP BY categoryId
        """
	)
	fun getSpendingByCategory(startDate: String, endDate: String): Flow<List<CategorySpending>>

	@Query(
		"""
        SELECT COALESCE(SUM(amount), 0) FROM transactions 
        WHERE type = 'EXPENSE' AND date >= :startDate AND date <= :endDate
        """
	)
	fun getTotalExpensesInPeriod(startDate: String, endDate: String): Flow<Long>

	@Query(
		"""
        SELECT COALESCE(SUM(amount), 0) FROM transactions 
        WHERE type = 'INCOME' AND date >= :startDate AND date <= :endDate
        """
	)
	fun getTotalIncomeInPeriod(startDate: String, endDate: String): Flow<Long>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(transaction: TransactionEntity)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(transactions: List<TransactionEntity>)

	@Update
	suspend fun update(transaction: TransactionEntity)

	@Delete
	suspend fun delete(transaction: TransactionEntity)

	@Query("DELETE FROM transactions WHERE id = :id")
	suspend fun deleteById(id: String)

	@Query("SELECT COUNT(*) FROM transactions")
	suspend fun count(): Int
}

data class CategorySpending(
	val categoryId: String?,
	val total: Long
)