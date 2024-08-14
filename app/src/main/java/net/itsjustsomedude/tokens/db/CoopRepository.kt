package net.itsjustsomedude.tokens.db

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class CoopRepository(application: Application) {
    private val coopDao: CoopDao

    init {
        val database = AppDatabase.getInstance(application)
        coopDao = database.coopDao()
    }

    suspend fun getCoop(id: Int): LiveData<Coop>? {
        return withContext(Dispatchers.IO) {
            coopDao.getCoop(id)
        }
    }

    suspend fun listCoops(): LiveData<List<CoopSummary>> {
        return withContext(Dispatchers.IO) {
            coopDao.listCoops()
        }
    }

    fun insert(coop: Coop) {
        CoroutineScope(Dispatchers.IO).launch {
            coopDao.insert(coop)
        }
    }

    fun update(coop: Coop) {
        CoroutineScope(Dispatchers.IO).launch {
            coopDao.update(coop)
        }
    }

    fun delete(coop: Coop) {
        CoroutineScope(Dispatchers.IO).launch {
            coopDao.delete(coop)
        }
    }

    fun deleteAll() {
        CoroutineScope(Dispatchers.IO).launch {
            coopDao.deleteAllCoops()
        }
    }

    @Deprecated("Don't use blocking calls.")
    fun blockingGetCoop(coopId: Int): LiveData<Coop>? = runBlocking { getCoop(coopId) }

    @Deprecated("Don't use blocking calls.")
    fun blockingListCoops(): LiveData<List<CoopSummary>> = runBlocking { listCoops() }

    @Deprecated("Don't use blocking calls.")
    fun blockingInsert(coop: Coop) = runBlocking { insert(coop) }

    @Deprecated("Don't use blocking calls.")
    fun blockingUpdate(coop: Coop) = runBlocking { update(coop) }

    @Deprecated("Don't use blocking calls.")
    fun blockingDelete(coop: Coop) = runBlocking { delete(coop) }

    @Deprecated("Don't use blocking calls.")
    fun blockingDeleteAllCoops() = runBlocking { deleteAll() }
}