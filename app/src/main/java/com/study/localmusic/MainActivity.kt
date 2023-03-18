package com.study.localmusic

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.graphics.BitmapFactory
import android.os.*
import android.widget.RemoteViews
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.study.localmusic.model.Song
import com.study.localmusic.model.SongIndex
import com.study.localmusic.service.MusicService
import com.study.localmusic.ui.page.HomePage
import com.study.localmusic.ui.theme.LocalMusicTheme
import com.study.localmusic.util.getAlbumPicture
import com.study.localmusic.util.go2AppSettings
import com.study.localmusic.viewmodel.MusicDataVm


class MainActivity : ComponentActivity() {

    private lateinit var notification: Notification
    private lateinit var remoteView: RemoteViews
    private val notificationId = 100
    private val musicVm by viewModels<MusicDataVm>()
    private var mService: MusicService? = null
    private val connection = ServiceConnect()
    private lateinit var notificationReceiver: NotificationReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initNotification()
        registerNotificationReceiver()

        Intent(this, MusicService::class.java).also {
            bindService(it, connection, BIND_AUTO_CREATE)
        }
        setContent {

            LocalMusicTheme {
                val musics by musicVm.musicFlow.collectAsState()
                val selectIndex by musicVm.indexFlow.collectAsState()
                val playDuration by musicVm.durationFlow.collectAsState()
                val isPlay by musicVm.playFlow.collectAsState()

                HomePage(musics = SnapshotStateList<Song>().apply {
                    addAll(musics)
                },
                    selectIndex = selectIndex,
                    playDuration = playDuration,
                    isPlay = isPlay,
                    permissionGrantedAction = {
                        musicVm.fetchMusicData()
                    },
                    serviceLoadData = {
                        mService?.loadSongs()
                    },
                    updateCheckAction = { clickPos ->
                        musicVm.dispatchPlayState(true)
                        updateCurrentPlay(clickPos, PLAY)
                        //updateDuration()
                    },
                    bottomPlayAction = {
                        val nowIndex = when (mService?.audioPlayer?.isPlaying) {
                            true -> {
                                mService?.pause()
                                mService?.playPosition ?: 0
                            }
                            else -> {
                                val currentDuration = mService?.audioPlayer?.currentPosition ?: 0
                                val hasChecked = selectIndex != -1
                                if (currentDuration == 0) {
                                    mService?.playByPos(if (hasChecked) selectIndex else 0)
                                } else {
                                    mService?.play()
                                }
                                selectIndex
                            }
                        }
                        showNotification(musics[nowIndex], PLAY)
                        musicVm.dispatchPlayState(!isPlay)
                    },
                    gotoSettingAction = {
                        go2AppSettings(this)
                    })
            }
        }
    }


    inner class ServiceConnect : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            mService = (binder as MusicService.MusicBinder).service()
            mService?.onIndexChange = {
                musicVm.updateSelectIndex(SongIndex(index = it))
                musicVm.querySelectIndex()
            }

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mService?.audioPlayer?.release()
            mService = null
        }
    }

    private fun updateDuration() {
//        if (mService?.audioPlayer?.isPlaying == true) {
//            val playDuration = mService?.audioPlayer?.currentPosition ?: 0
//            val message = Message.obtain().also {
//                it.what = playDuration
//            }
//            mHandler.sendMessageDelayed(message, 1000)
//        }

    }


    private val mHandler = Handler(
        Looper.getMainLooper()
    ) {
        musicVm.dispatchPlayDuration(it.what)
        updateDuration()
        return@Handler true
    }


    //初始化通知栏
    private fun initNotification() {
        val channelId = "channel_id"
        val notificationCompat = NotificationManagerCompat.from(this)
        createNotificationChannel(channelId, notificationCompat)
        initRemoteView()
        notification =
            NotificationCompat.Builder(this, channelId).setSmallIcon(R.mipmap.ic_launcher)
                .setCustomContentView(remoteView).setChannelId(channelId)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC).setOngoing(true)
                .setAutoCancel(false).build()


    }

    private fun createNotificationChannel(
        channelId: String, notificationCompat: NotificationManagerCompat
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, "music_channel", NotificationManager.IMPORTANCE_HIGH)
            notificationCompat.createNotificationChannel(notificationChannel)
        }
    }


    private fun initRemoteView() {
        remoteView = RemoteViews(packageName, R.layout.music_notification)
        createClickPendingIntent(PREVIOUS, R.id.iv_prev)
        createClickPendingIntent(PLAY, R.id.iv_play)
        createClickPendingIntent(NEXT, R.id.iv_next)
    }

    private fun createClickPendingIntent(action: String, viewId: Int) {
        val prevPendIntent =
            PendingIntent.getBroadcast(this, 0, Intent(action), PendingIntent.FLAG_IMMUTABLE)
        remoteView.setOnClickPendingIntent(viewId, prevPendIntent)
    }

    private fun showNotification(song: Song, action: String) {
        remoteView.setTextViewText(R.id.tv_title, song.song)
        remoteView.setTextViewText(R.id.tv_singer, song.singer)
        remoteView.setImageViewBitmap(
            R.id.iv_pic,
            getAlbumPicture(this, song.path, 2)
        )
        remoteView.setImageViewResource(
            R.id.iv_play,
            if (mService?.audioPlayer?.isPlaying == true) R.drawable.pause_black else R.drawable.play_black
        )

        NotificationManagerCompat.from(this).notify(notificationId, notification)
    }

    private fun registerNotificationReceiver() {
        notificationReceiver = NotificationReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(PREVIOUS)
        intentFilter.addAction(PLAY)
        intentFilter.addAction(NEXT)
        registerReceiver(notificationReceiver, intentFilter)
    }


    inner class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: PLAY
            val nowIndex = when (intent?.action) {
                PREVIOUS -> {
                    if (mService?.playPosition!! <= 0) 0 else mService?.playPosition!! - 1
                }

                NEXT -> {
                    val songSize = mService?.songs?.size ?: 0
                    if (mService?.playPosition!! >= songSize - 1) 0 else mService?.playPosition!! + 1
                }
                else -> {
                    if (mService?.audioPlayer?.isPlaying == true) {
                        mService?.pause()
                        musicVm.dispatchPlayState(false)
                    } else {
                        mService?.play()
                        musicVm.dispatchPlayState(true)
                    }
                    mService?.playPosition!!
                }
            }
            updateCurrentPlay(nowIndex, action)

        }
    }

    private fun updateCurrentPlay(nowIndex: Int, action: String) {
        musicVm.updateSelectIndex(SongIndex(index = nowIndex))
        mService?.playByPos(nowIndex)
        musicVm.querySelectIndex()
        showNotification(mService?.songs!![nowIndex], action)
    }


    override fun onDestroy() {
        unbindService(connection)
        unregisterReceiver(notificationReceiver)
        super.onDestroy()
    }


}

