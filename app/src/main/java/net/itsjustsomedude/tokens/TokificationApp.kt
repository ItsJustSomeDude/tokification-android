package net.itsjustsomedude.tokens

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import net.itsjustsomedude.tokens.db.AppDatabase
import net.itsjustsomedude.tokens.db.CoopRepository
import net.itsjustsomedude.tokens.db.EventRepository
import net.itsjustsomedude.tokens.models.CoopViewModel
import net.itsjustsomedude.tokens.models.CreateEventViewModel
import net.itsjustsomedude.tokens.models.MainScreenViewModel
import net.itsjustsomedude.tokens.models.NotificationDebuggerViewModel
import net.itsjustsomedude.tokens.store.StoreRepo
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore(name = "settings")

class TokificationApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@TokificationApp)
            modules(appModule)
        }
    }
}

val appModule = module {
    single { AppDatabase.createInstance(get()) }

    single { get<AppDatabase>().eventDao() }
    single { get<AppDatabase>().coopDao() }

    single { NotificationHelper(get(), get(), get()) }
    single { ClipboardHelper(get()) }

    single { EventRepository(get()) }
    single { CoopRepository(get()) }
    single { StoreRepo(get()) }

    viewModel { MainScreenViewModel(get(), get(), get()) }
    viewModel { parameters -> CoopViewModel(parameters.get(), get(), get(), get(), get()) }
    viewModel { CreateEventViewModel(get(), get()) }
    viewModel { NotificationDebuggerViewModel(get()) }
}