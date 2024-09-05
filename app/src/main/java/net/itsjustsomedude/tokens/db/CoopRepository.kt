package net.itsjustsomedude.tokens.db

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class CoopRepository(application: Application) {
    private val coopDao: CoopDao
    private val application: Application

    init {
        val database = AppDatabase.getInstance(application)
        coopDao = database.coopDao()

        this.application = application
    }

    suspend fun getCoop(id: Long): LiveData<Coop?> {
        println("RepoGet")
        return withContext(Dispatchers.IO) {
            val a = coopDao.getCoop(id)
            println("RepoGetDone")
            a
        }
    }

    fun getCoopSync(id: Long): LiveData<Coop?> {
        return coopDao.getCoop(id)
    }

    fun getCoopByNameSync(coopName: String, kevId: String): LiveData<Coop?> {
        return coopDao.getCoopByName(coopName, kevId)
    }

    suspend fun getCoopByName(coopName: String, kevId: String): LiveData<Coop?> {
        return withContext(Dispatchers.IO) {
            coopDao.getCoopByName(coopName, kevId)
        }
    }

    suspend fun listCoops(): LiveData<List<Coop>> {
        return withContext(Dispatchers.IO) {
            coopDao.listCoops()
        }
    }

    fun insert(coop: Coop) {
        CoroutineScope(Dispatchers.IO).launch {
            coopDao.insert(coop)
            Log.i("CoopRepo", "Created.")
        }
    }

    fun update(coop: Coop) {
        println("RepoUpdate")
        CoroutineScope(Dispatchers.IO).launch {
            coopDao.update(coop)
            println("RepoUpdateDone")
        }
    }

    fun delete(coop: Coop) {
        CoroutineScope(Dispatchers.IO).launch {
            coopDao.delete(coop)
        }
    }

    @Deprecated("Don't use blocking calls.")
    fun blockingGetCoop(coopId: Long): LiveData<Coop?> = runBlocking { getCoop(coopId) }

    @Deprecated("Don't use blocking calls.")
    fun blockingListCoops(): LiveData<List<Coop>> = runBlocking { listCoops() }

    @Deprecated("Don't use blocking calls.")
    fun blockingInsert(coop: Coop) = runBlocking { insert(coop) }

    @Deprecated("Don't use blocking calls.")
    fun blockingUpdate(coop: Coop) = runBlocking { update(coop) }

    @Deprecated("Don't use blocking calls.")
    fun blockingDelete(coop: Coop) = runBlocking { delete(coop) }
}