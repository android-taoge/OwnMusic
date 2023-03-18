package com.study.localmusic.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.study.localmusic.LOADSONG
import com.study.localmusic.data.SourceManager
import com.study.localmusic.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * @Author tangtao
 * @Description:
 * @Date: 2023/3/16 11:06 PM
 */
class MusicService : Service(), MediaPlayer.OnCompletionListener {

    lateinit var audioPlayer: MediaPlayer
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var sourceManager: SourceManager
    var songs: List<Song> = emptyList()
        private set(value) {
            field = value
        }
    var playPosition: Int = 0
        private set(value) {
            field = value
        }

    inner class MusicBinder : Binder() {
        fun service(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        coroutineScope = CoroutineScope(SupervisorJob())
        sourceManager = SourceManager(this)
        audioPlayer = MediaPlayer().apply {
            setOnCompletionListener(this@MusicService)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let {
            if (it == LOADSONG) {
                loadSongs()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun loadSongs() {
        coroutineScope.launch {
            sourceManager.getSongs().also {
                songs = it
            }
        }
    }


    override fun onBind(intent: Intent?): IBinder {
        return MusicBinder()
    }


    fun playByPos(position: Int) {
        if (songs.isEmpty()) return
        if (playPosition == 0 || playPosition != position) {
            playPosition = position

            runCatching {
                audioPlayer.reset()
                audioPlayer.setDataSource(songs[position].path)
                audioPlayer.prepare()
                audioPlayer.start()
            }
        }

    }


    fun play() {
        if (!audioPlayer.isPlaying) {
            audioPlayer.start()
        }
    }

    fun pause() {
        if (audioPlayer.isPlaying) {
            audioPlayer.pause()
        }
    }

    private fun nextPlay() {
        if (playPosition >= songs.size - 1) {
            0
        } else {
            playPosition + 1
        }.also {
            playByPos(it)
            updatePlayIndex(it)
        }

    }

    override fun onCompletion(mp: MediaPlayer?) {
        nextPlay()
    }

    private fun updatePlayIndex(index: Int) {
        onIndexChange?.invoke(index)
    }


    var onIndexChange: ((index: Int) -> Unit)? = null


}