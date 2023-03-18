package com.study.localmusic.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Author tangtao
 * @Description:
 * @Date: 2023/3/16 4:08 PM
 */

@Entity()
data class Song(

    /**
     * 专辑ID
     */
    @PrimaryKey(autoGenerate = true)
    val albumId: Int = 0,

    /**
     * 歌手
     */
    val singer: String = "",
    /**
     * 歌曲名
     */
    val song: String = "",

    /**
     * 专辑名
     */
    val album: String = "",
    /**
     * 歌曲的地址
     */
    val path: String = "",
    /**
     * 歌曲长度
     */
    val duration: Int = 0,
    /**
     * 歌曲的大小
     */
    val size: Long = 0,

)
