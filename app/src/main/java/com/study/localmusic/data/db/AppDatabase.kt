package com.study.localmusic.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.study.localmusic.model.Song
import com.study.localmusic.model.SongIndex

/**
 * @Author tangtao
 * @Description:
 * @Date: 2023/3/16 4:39 PM
 */
@Database(entities = [Song::class, SongIndex::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun musicDao(): MusicDao
   abstract fun indexDao(): IndexDao

    companion object {
        @Volatile
        var db: AppDatabase? = null
        fun createDb(context: Context): AppDatabase {
            return db ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "music")
                    .build()
                    .also {
                        db = it
                    }
            }
        }
    }
}