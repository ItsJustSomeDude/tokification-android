package net.itsjustsomedude.tokens.db

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    fun deleteAllCoops() {
        CoroutineScope(Dispatchers.IO).launch {
            coopDao.deleteAllCoops()
        }
    }
}