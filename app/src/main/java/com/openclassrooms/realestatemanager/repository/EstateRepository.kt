package com.openclassrooms.realestatemanager.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.database.dao.EstateDao
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
import com.openclassrooms.realestatemanager.database.model.Estate
import com.openclassrooms.realestatemanager.database.model.EstateSearch

class EstateRepository(private val estateDao: EstateDao) {

    val allDetailedEstates: LiveData<List<DetailedEstate>> = estateDao.getDetailedEstates()

    suspend fun insert(estate: Estate) = estateDao.insert(estate)

    suspend fun update(estate: Estate) = estateDao.updateEstate(estate)

    fun getEstate(estateKey: Long): LiveData<DetailedEstate> = estateDao.getEstate(estateKey)

    fun filterEstateList(searchEstate: EstateSearch?): LiveData<List<DetailedEstate>> {
        StringBuilder().run {
            append(
                """
                    SELECT DISTINCT e.*
                    FROM estate_table AS e
                    LEFT JOIN type_table AS t ON t.type_id = e.type_id
                    LEFT JOIN (SELECT estate_id, COUNT(estate_id) AS picturesCount
                    FROM picture_table GROUP BY estate_id
                    ) AS p ON p.estate_id = e.start_time_milli
                """
            )
                .append("WHERE (t.name = '${searchEstate?.type}' OR '${searchEstate?.type}'= '') ")
                .append("AND (e.price BETWEEN '${searchEstate?.priceRange?.first}' AND '${searchEstate?.priceRange?.last}') ")
                .append(
                    "AND ((e.surface BETWEEN '${searchEstate?.surfaceRange?.first}' AND '${searchEstate?.surfaceRange?.last}') " +
                            "OR e.surface IS NULL) "
                ) // Surface not mandatory in Detail creation
                .append("AND (e.start_time_milli BETWEEN '${searchEstate?.createDateRange?.first}' AND '${searchEstate?.createDateRange?.last}') ")
                .append(
                    "AND (${searchEstate?.soldStatus} = false AND e.end_time_milli IS NULL) " +
                            "OR (${searchEstate?.soldStatus} = true " +
                            "AND e.end_time_milli BETWEEN '${searchEstate?.soldDateRange?.first}' " +
                            "AND '${searchEstate?.soldDateRange?.last}') "
                )
                .append("GROUP BY e.start_time_milli ")
                .append("""HAVING p.picturesCount >= ${searchEstate?.pictureMinNumber}""")

            Log.i("ListDetailViewModel", this.toString())
            return estateDao.filterEstateList(SimpleSQLiteQuery(this.toString()))
        }
    }


}


