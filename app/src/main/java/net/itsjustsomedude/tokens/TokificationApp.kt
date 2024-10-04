package net.itsjustsomedude.tokens

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import net.itsjustsomedude.tokens.db.AppDatabase
import net.itsjustsomedude.tokens.db.CoopRepository
import net.itsjustsomedude.tokens.db.EventRepository
import net.itsjustsomedude.tokens.models.CoopNameEditViewModel
import net.itsjustsomedude.tokens.models.CoopViewModel
import net.itsjustsomedude.tokens.models.EventEditViewModel
import net.itsjustsomedude.tokens.models.MainScreenViewModel
import net.itsjustsomedude.tokens.models.NotificationDebuggerViewModel
import net.itsjustsomedude.tokens.models.SettingsViewModel
import net.itsjustsomedude.tokens.store.PreferencesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

const val DATASTORE_NAME = "new_settings"

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
    single {
        PreferenceDataStoreFactory.create(
            produceFile = { get<Context>().preferencesDataStoreFile(DATASTORE_NAME) }
        )
    }

    single { AppDatabase.createInstance(get()) }

    single { get<AppDatabase>().eventDao() }
    single { get<AppDatabase>().coopDao() }

    single { NotificationHelper(get(), get(), get()) }
    single { ClipboardHelper(get()) }

    single { EventRepository(get()) }
    single { CoopRepository(get()) }
    single { PreferencesRepository(get()) }

    viewModel { MainScreenViewModel(get(), get(), get(), get()) }
    viewModel { parameters -> CoopViewModel(parameters.get(), get(), get(), get(), get()) }
    viewModel { NotificationDebuggerViewModel(get()) }
    viewModel { parameters -> CoopNameEditViewModel(parameters[0], parameters[1], get()) }
    viewModel { parameters -> EventEditViewModel(parameters[0], parameters[1], get(), get()) }

    viewModel { SettingsViewModel(get()) }
}