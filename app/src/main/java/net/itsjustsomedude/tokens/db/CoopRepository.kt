package net.itsjustsomedude.tokens.db

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoopRepository(application: Application) {
    private val coopDao: CoopDao
    private val allCoops: LiveData<List<Coop>>

    init {
        val database = CoopDatabase.getInstance(application)
        coopDao = database.coopDao()
        allCoops = coopDao.getAllCoops()
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