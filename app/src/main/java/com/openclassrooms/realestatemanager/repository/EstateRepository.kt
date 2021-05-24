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
        return if (searchEstate == null) allDetailedEstates
        else {
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
                append("WHERE (e.price BETWEEN '${searchEstate.priceRange.first}' AND '${searchEstate.priceRange.last}') ")
                if (!searchEstate.type.isNullOrBlank()) {
                    append("AND t.name = '${searchEstate.type}' ")
                }
                append(
                    "AND ((e.surface BETWEEN '${searchEstate.surfaceRange.first}' AND '${searchEstate.surfaceRange.last}') " +
                            "OR e.surface IS NULL) "
                )
                append("AND (e.start_time_milli BETWEEN '${searchEstate.createDateRange.first}' AND '${searchEstate.createDateRange.last}') ")
                if (!searchEstate.soldStatus) {
                    append("AND e.end_time_milli IS NULL ")
                } else {
                    append(
                        "AND e.end_time_milli BETWEEN '${searchEstate.soldDateRange.first}' " +
                                "AND '${searchEstate.soldDateRange.last}' "
                    )
                }
                // Manage POI research
                if (searchEstate.poiList?.isNotEmpty() == true) {
                    for (poi in searchEstate.poiList) {
                        append("AND e.poi_id LIKE '%|${poi}|%' ")
                    }
                }
                append("GROUP BY e.start_time_milli ")
                append("""HAVING p.picturesCount >= ${searchEstate.pictureMinNumber}""")

                Log.i("ListDetailViewModel", this.toString())
                return estateDao.filterEstateList(SimpleSQLiteQuery(this.toString()))
            }
        }
    }
}
