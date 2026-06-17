package dev.apercorn.koin.di

import dev.apercorn.koin.ui.screens.accounts.AccountsViewModel
import dev.apercorn.koin.ui.screens.overview.OverviewViewModel
import dev.apercorn.koin.ui.screens.transactions.TransactionViewModel
import dev.apercorn.koin.ui.screens.transactions.entry.TransactionEntryViewModel
import org.koin.dsl.module

val screenModelModule = module {
	factory<OverviewViewModel> { OverviewViewModel(get(), get(), get()) }
	factory<TransactionViewModel> { TransactionViewModel(get(), get(), get()) }
	factory<TransactionEntryViewModel> { TransactionEntryViewModel(get(), get(), get()) }
	factory<AccountsViewModel> { AccountsViewModel(get(), get()) }
}
