package com.study.localmusic.data

import com.study.localmusic.data.db.AppDatabase
import com.study.localmusic.model.Song
import com.study.localmusic.model.SongIndex

/**
 * @Author tangtao
 * @Description:
 * @Date: 2023/3/16 4:10 PM
 */
class DbMusicSource(private val db: AppDatabase) : MusicSource {


    override suspend fun loadSong(): List<Song> {

        return db.musicDao().querySong()

    }

    suspend fun saveSong(songs: List<Song>) {
        db.musicDao().insert(songs)
    }

    suspend fun queryIndex(): SongIndex? {
        return db.indexDao().queryIndex()
    }

    suspend fun saveIndex(index: SongIndex) {
        db.indexDao().save(index)
    }

    suspend fun updateIndex(index: SongIndex) {
        db.indexDao().update(index)
    }

}