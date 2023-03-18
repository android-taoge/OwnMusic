package com.study.localmusic.data

import android.content.Context
import com.study.localmusic.data.db.AppDatabase
import com.study.localmusic.model.Song
import com.study.localmusic.model.SongIndex

/**
 * @Author tangtao
 * @Description:
 * @Date: 2023/3/16 4:14 PM
 */
class SourceManager(context: Context) {

    private val localSource = LocalMusicSource(context)
    private val dbSource = DbMusicSource(AppDatabase.db!!)

    suspend fun getSongs(): List<Song> {

        return when {
            dbSource.loadSong().isEmpty() -> localSource.loadSong().also {
                dbSource.saveSong(it)
                dbSource.saveIndex(SongIndex(index = -1))
            }
            else -> dbSource.loadSong()
        }
    }


    suspend fun queryIndex(): SongIndex? {
        return dbSource.queryIndex()
    }

    suspend fun updateIndex( index: SongIndex) {
        dbSource.updateIndex(index)
    }
}