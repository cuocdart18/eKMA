package com.example.kmatool.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.kmatool.models.score.MiniStudent
import com.example.kmatool.utils.DATABASE_MINISTUDENT_NAME

@Database(
    entities = [MiniStudent::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class MiniStudentDatabase : RoomDatabase() {
    abstract fun miniStudentDao(): MiniStudentDao

    companion object {

        @Volatile
        private var instance: MiniStudentDatabase? = null

        fun getInstance(context: Context): MiniStudentDatabase {
            if (instance == null) {
                synchronized(this) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MiniStudentDatabase::class.java,
                        DATABASE_MINISTUDENT_NAME
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