package com.openclassrooms.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.database.model.Employee

@Dao
interface EmployeeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(employee: Employee)

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param estate new value to write
     */
    @Update
    suspend fun updateEmployee(employee: Employee)

    /**
     * Selects and returns the row that matches the supplied estateId.
     *
     * @param employeeId to match
     */
    @Query("SELECT * FROM employee_table WHERE employee_id = :employeeId")
    fun getEmployee(employeeId: Long): LiveData<Employee>

    /**
     * Selects and returns all rows in the table,
     *
     * sorted by start time in descending order.
     */
    @Query("SELECT * FROM employee_table ORDER BY employee_id DESC")
    fun getAllEmployees(): LiveData<List<Employee>>



    @Query("DELETE FROM employee_table")
    fun deleteAll()



}