package net.itsjustsomedude.tokens

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.sentry.Sentry
import io.sentry.SentryLevel
import kotlinx.serialization.json.Json
import net.itsjustsomedude.tokens.db.AppDatabase
import net.itsjustsomedude.tokens.db.CoopRepository
import net.itsjustsomedude.tokens.db.EventRepository
import net.itsjustsomedude.tokens.db.ExpandedCoopRepository
import net.itsjustsomedude.tokens.models.CoopNameEditViewModel
import net.itsjustsomedude.tokens.models.CoopViewModel
import net.itsjustsomedude.tokens.models.EventEditViewModel
import net.itsjustsomedude.tokens.models.MainScreenViewModel
import net.itsjustsomedude.tokens.models.NotificationDebuggerViewModel
import net.itsjustsomedude.tokens.models.SettingsViewModel
import net.itsjustsomedude.tokens.network.UpdateChecker
import net.itsjustsomedude.tokens.store.PreferencesRepository
import org.koin.android.ext.android.inject
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
			modules(appModule, networkModule)
		}

		/* if (!BuildConfig.DEBUG) */
		startSentry()
	}

	private fun startSentry() {
		val prefsRepo by inject<PreferencesRepository>()

		Sentry.init { options ->
			options.dsn = BuildConfig.SENTRY_DSN
			options.isDebug = BuildConfig.DEBUG
			options.setDiagnosticLevel(SentryLevel.INFO)

			options.setBeforeSend { event, _ ->
				val shouldSend = prefsRepo.sentryEnabled.getValueSync()

				if (shouldSend)
					event
				else
					null
			}
		}
	}
}

val networkModule = module {
	single {
		HttpClient(OkHttp) {
			install(ContentNegotiation) {
				json(Json { ignoreUnknownKeys = true })
			}
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
	single { ExpandedCoopRepository(get(), get()) }

	single { UpdateChecker(get(), get()) }

	viewModel { MainScreenViewModel(get(), get(), get(), get(), get(), get()) }
	viewModel { parameters -> CoopViewModel(parameters.get(), get(), get(), get(), get()) }
	viewModel { NotificationDebuggerViewModel(get()) }
	viewModel { parameters -> CoopNameEditViewModel(parameters[0], parameters[1], get()) }
	viewModel { parameters -> EventEditViewModel(parameters[0], parameters[1], get(), get()) }

	viewModel { SettingsViewModel(get(), get()) }
}
