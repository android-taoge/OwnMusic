package com.study.localmusic.data.db

import androidx.room.*
import com.study.localmusic.model.Song
import com.study.localmusic.model.SongIndex

/**
 * @Author tangtao
 * @Description:
 * @Date: 2023/3/16 4:20 PM
 */

@Dao
interface MusicDao {

    @Query("SELECT * FROM song ")
    suspend fun querySong(): List<Song>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(songs: List<Song>)


}


@Dao
interface IndexDao {

    @Query("SELECT * FROM songindex WHERE indexId = :id")
    suspend fun queryIndex(id: Int = 0): SongIndex?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(index: SongIndex)


    @Update(SongIndex::class)
    suspend fun update(index: SongIndex)


}

