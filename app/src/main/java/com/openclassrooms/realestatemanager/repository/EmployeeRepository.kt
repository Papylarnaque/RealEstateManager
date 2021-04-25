package com.openclassrooms.realestatemanager.repository

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.database.dao.EmployeeDao
import com.openclassrooms.realestatemanager.database.model.Employee

class EmployeeRepository(private val employeeDao: EmployeeDao) {

    val allEmployees: LiveData<List<Employee>> = employeeDao.getAllEmployees()

    suspend fun insert(employee: Employee) {
        employeeDao.insert(employee)
    }

    suspend fun update(employee: Employee) {
        employeeDao.updateEmployee(employee)
    }

}