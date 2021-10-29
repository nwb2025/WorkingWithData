package com.example.androiddata.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Monster::class], version = 1, exportSchema = false)
abstract class MonsterDB: RoomDatabase() {

    abstract fun monsterDao(): MonsterDao

    companion object{
        // can be accessed by more than one thread at a time
        @Volatile
        private var INSTANCE: MonsterDB? = null

        fun getMonsterDB(context: Context): MonsterDB {
            if (INSTANCE == null){
                // can be called by one thread at a time
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        MonsterDB::class.java,
                        "monsters.db"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}