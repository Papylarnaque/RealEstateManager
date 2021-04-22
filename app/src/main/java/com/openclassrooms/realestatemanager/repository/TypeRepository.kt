package com.openclassrooms.realestatemanager.repository

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.database.dao.TypeDao
import com.openclassrooms.realestatemanager.database.model.Type

class TypeRepository(private val typeDao: TypeDao) {

    val allTypes: LiveData<List<Type>> = typeDao.getAllTypes()

    suspend fun insert(type: Type) {
        typeDao.insert(type)
    }

    suspend fun update(type: Type) {
        typeDao.updateType(type)
    }

    fun getAllTypesStatic(): List<Type> {
        return typeDao.getAllTypesStatic()
    }

}