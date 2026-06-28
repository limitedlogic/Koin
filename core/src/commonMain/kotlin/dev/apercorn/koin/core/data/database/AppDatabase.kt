package dev.apercorn.koin.core.data.database

import androidx.room.*
import dev.apercorn.koin.core.data.database.converters.Converters
import dev.apercorn.koin.core.data.database.daos.*
import dev.apercorn.koin.core.data.database.entities.*
import dev.apercorn.koin.core.domain.SeedCategories

@Database(
	entities = [
		AccountEntity::class,
		TransactionEntity::class,
		CategoryEntity::class,
		BudgetEntity::class,
		GoalEntity::class,
		RecurringEntity::class,
		CounterpartyEntity::class
	],
	version = 3,
	exportSchema = false
)
@TypeConverters(Converters::class)
@ConstructedBy(AppDatabaseCtor::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun accountDao(): AccountDao
	abstract fun transactionDao(): TransactionDao
	abstract fun categoryDao(): CategoryDao
	abstract fun budgetDao(): BudgetDao
	abstract fun recurringDao(): RecurringDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseCtor : RoomDatabaseConstructor<AppDatabase> {
	override fun initialize(): AppDatabase
}

suspend fun seedDefaultCategories(database: AppDatabase) {
	val dao = database.categoryDao()
	if (dao.count() == 0) {
		dao.upsertAll(SeedCategories.defaults)
	}
}
