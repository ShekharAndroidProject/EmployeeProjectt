package com.example.employeeproject.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.employeeproject.model.Employee;

@Database(entities = {Employee.class},version = 1,exportSchema = false)
public abstract class EmployeeDatabase extends RoomDatabase {
    public abstract EmployeeDao getEmployeeDao();

}
