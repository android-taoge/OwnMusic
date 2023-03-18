package com.study.localmusic.data

import com.study.localmusic.model.Song

/**
 * @Author tangtao
 * @Description:
 * @Date: 2023/3/16 3:58 PM
 */
interface MusicSource {

    suspend fun loadSong(): List<Song>
}




