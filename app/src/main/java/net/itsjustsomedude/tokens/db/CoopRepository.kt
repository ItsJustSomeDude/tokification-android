package net.itsjustsomedude.tokens.db

import androidx.lifecycle.LiveData

class CoopRepository(private val coopDao: CoopDao) {

    fun listCoopsLiveData(): LiveData<List<Coop>> =
        coopDao.listCoopsLiveData()

    fun getCoopLiveData(id: Long): LiveData<Coop?> =
        coopDao.getCoopLiveData(id)

    fun getCoopLiveData(coopName: String, kevId: String): LiveData<Coop?> =
        coopDao.getCoopLiveDataByName(coopName, kevId)

    suspend fun getCoop(id: Long): Coop? =
        coopDao.getCoop(id)

    suspend fun getCoop(coopName: String, kevId: String): Coop? =
        coopDao.getCoopByName(coopName, kevId)

    suspend fun insert(coop: Coop): Long =
        coopDao.insert(coop)

    suspend fun update(coop: Coop) =
        coopDao.update(coop)

    suspend fun delete(coop: Coop) =
        coopDao.delete(coop)
}