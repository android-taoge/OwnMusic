package com.study.localmusic.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Author tangtao
 * @Description:
 * @Date: 2023/3/16 4:08 PM
 */

@Entity("songindex")
data class SongIndex(
    @PrimaryKey
    val indexId: Int = 0,
    val index: Int = 0
)
