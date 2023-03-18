package com.study.localmusic.data

import android.content.Context
import android.provider.MediaStore.Audio.Media
import com.study.localmusic.model.Song

/**
 * @Author tangtao
 * @Description:
 * @Date: 2023/3/16 4:10 PM
 */
class LocalMusicSource(private val context: Context) : MusicSource {


    override suspend fun loadSong(): List<Song> {
        val cursor = context.contentResolver.query(
            Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            Media.IS_MUSIC
        ) ?: return emptyList()
        val data = ArrayList<Song>()
        while (cursor.moveToNext()) {
            var name = cursor.getString(cursor.getColumnIndexOrThrow(Media.TITLE))
            var singer = cursor.getString(cursor.getColumnIndexOrThrow(Media.ARTIST))
            val album = cursor.getString(cursor.getColumnIndexOrThrow(Media.ALBUM))
            val albumId = cursor.getInt(cursor.getColumnIndexOrThrow(Media.ALBUM_ID))
            val duration = cursor.getInt(cursor.getColumnIndexOrThrow(Media.DURATION))
            val path = cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA))
            val size = cursor.getLong(cursor.getColumnIndexOrThrow(Media.SIZE))

            // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
            if (size > 1000 * 800) {
                if (name.contains("-")) {
                    val str: List<String> = name.split("-")
                    singer = str[0]
                    name = str[1]

                }
                with(
                    Song(
                        song = name,
                        singer = singer,
                        album = album,
                        albumId = albumId,
                        duration = duration,
                        path = path,
                        size = size
                    )
                ) {
                    data.add(this)
                }
            }


        }
        cursor.close()
        //Log.e("cursorData", "==$data")
        return data
    }
}