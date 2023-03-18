package com.study.localmusic.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.Settings
import com.study.localmusic.R

/**
 * @Author tangtao
 * @Description:
 * @Date: 2023/3/16 10:06 PM
 */
fun formatTime(time: Int): String {
    return if (time / 1000 % 60 < 10) {
        (time / 1000 / 60).toString() + ":0" + time / 1000 % 60
    } else {
        (time / 1000 / 60).toString() + ":" + time / 1000 % 60
    }
}

fun go2AppSettings(context: Context) {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.parse("package:${context.packageName}")
    }.also {
        context.startActivity(it)
    }
}


fun getAlbumPicture(context: Context, path: String?, type: Int): Bitmap {
    //歌曲检索
    val mmr = MediaMetadataRetriever()


    val data = try {
        //设置数据源
        mmr.setDataSource(path)
        //获取图片数据
        mmr.embeddedPicture
    } catch (e: Exception) {
        null
    }

    var albumPicture: Bitmap
    if (data != null) {
        //获取bitmap对象
        albumPicture = BitmapFactory.decodeByteArray(data, 0, data.size)
        //获取宽高
        val width = albumPicture.width
        val height = albumPicture.height
        // 创建操作图片用的Matrix对象
        val matrix = Matrix()
        // 计算缩放比例
        val sx = 120f / width
        val sy = 120f / height
        // 设置缩放比例
        matrix.postScale(sx, sy)
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        albumPicture = Bitmap.createBitmap(albumPicture, 0, 0, width, height, matrix, false)
    } else {
        //从歌曲文件读取不出来专辑图片时用来代替的默认专辑图片
        albumPicture = if (type == 1) {
            //Activity中显示
            BitmapFactory.decodeResource(context.resources, R.drawable.icon_music)
        } else {
            //通知栏显示
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.icon_notification_default
            )
        }
        val width = albumPicture.width
        val height = albumPicture.height
        // 创建操作图片用的Matrix对象
        val matrix = Matrix()
        // 计算缩放比例
        val sx = 120f / width
        val sy = 120f / height
        // 设置缩放比例
        matrix.postScale(sx, sy)
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        albumPicture = Bitmap.createBitmap(albumPicture, 0, 0, width, height, matrix, false)
    }
    return albumPicture


}