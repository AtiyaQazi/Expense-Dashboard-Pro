package com.example.expensedashboardpro

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "TaskDatabase.db", null, 1) {

    companion object {
        private const val TABLE_TASKS = "Tasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_TASKS ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    // Add a new task (CREATE)
    fun addTask(taskName: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, taskName)
        val success = db.insert(TABLE_TASKS, null, values)
        db.close()
        return success
    }

    // Retrieve all tasks (READ)
    fun getAllTasks(): ArrayList<Task> {
        val taskList = ArrayList<Task>()
        val selectQuery = "SELECT * FROM $TABLE_TASKS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                taskList.add(Task(id, name))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return taskList
    }

    // Delete a task (DELETE)
    fun deleteTask(id: Int): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_TASKS, "$COLUMN_ID=$id", null)
        db.close()
        return success
    }
}