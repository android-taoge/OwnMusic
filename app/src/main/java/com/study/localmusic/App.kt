package com.study.localmusic

import android.app.Application
import com.study.localmusic.data.db.AppDatabase

/**
 * @Author tangtao
 * @Description:
 * @Date: 2023/3/16 4:59 PM
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AppDatabase.createDb(applicationContext)

    }
}