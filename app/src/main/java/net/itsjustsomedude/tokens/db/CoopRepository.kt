package net.itsjustsomedude.tokens.db

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoopRepository(private val coopDao: CoopDao) {

    suspend fun getCoop(id: Long): LiveData<Coop?> {
        return withContext(Dispatchers.IO) {
            coopDao.getCoop(id)
        }
    }

    suspend fun getCoopDirect(id: Long): Coop? {
        return withContext(Dispatchers.IO) {
            coopDao.getCoopDirect(id)
        }
    }

    suspend fun getCoopByName(coopName: String, kevId: String): LiveData<Coop?> {
        return withContext(Dispatchers.IO) {
            coopDao.getCoopByName(coopName, kevId)
        }
    }

    suspend fun getCoopByNameDirect(coopName: String, kevId: String): Coop? {
        return withContext(Dispatchers.IO) {
            coopDao.getCoopByNameDirect(coopName, kevId)
        }
    }

    suspend fun listCoops(): LiveData<List<Coop>> {
        return withContext(Dispatchers.IO) {
            coopDao.listCoops()
        }
    }

    suspend fun insert(coop: Coop): Long {
        return withContext(Dispatchers.IO) {
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

    fun deleteById(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            coopDao.deleteById(id)
        }
    }
}