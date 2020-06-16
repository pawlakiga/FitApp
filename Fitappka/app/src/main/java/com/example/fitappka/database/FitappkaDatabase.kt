package com.example.fitappka.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Exercise::class, Training::class, TrainingExerciseCrossRef::class], version = 1, exportSchema = false)
abstract class FitappkaDatabase: RoomDatabase(){

    // Connecting DAO to interact with database (you can have multiple DAO's)
    abstract val fitappkaDatabaseDao: FitappkaDatabaseDao
    // Companion object allows clients to access database methods without instantiating object
    companion object {

        // Volatile makes variable always updated and the same for all accessing threads
        @Volatile
        private var INSTANCE: FitappkaDatabase? = null

        fun getInstance(context: Context): FitappkaDatabase {
            // For multiple threads access synchronization (only one thread can access this at once)
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    // Building new database with destructive migration as this project is simple
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            FitappkaDatabase::class.java,
                            "fitappka_database"
                        )
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                    INSTANCE = instance
                }

                return instance
            }

        }
    }
}
