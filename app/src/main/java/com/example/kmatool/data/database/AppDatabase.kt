package com.example.kmatool.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.kmatool.common.Converters
import com.example.kmatool.data.database.daos.MiniStudentDao
import com.example.kmatool.data.database.daos.NoteDao
import com.example.kmatool.data.database.daos.PeriodDao
import com.example.kmatool.data.models.Period
import com.example.kmatool.data.models.MiniStudent
import com.example.kmatool.data.models.Note
import com.example.kmatool.common.DATABASE_NAME

@Database(
    entities = [MiniStudent::class, Period::class, Note::class],
    version = 2
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun miniStudentDao(): MiniStudentDao
    abstract fun periodDao(): PeriodDao
    abstract fun noteDao(): NoteDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                synchronized(this) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        DATABASE_NAME
                    ).allowMainThreadQueries()
                        .build()
                }
            }
            // Return database.
            return instance!!
        }

        fun destroyInstance() {
            instance = null
        }
    }
}