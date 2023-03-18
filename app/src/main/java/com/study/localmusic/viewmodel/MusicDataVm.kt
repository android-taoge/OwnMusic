package com.study.localmusic.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.study.localmusic.data.SourceManager
import com.study.localmusic.model.Song
import com.study.localmusic.model.SongIndex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @Author tangtao
 * @Description:
 * @Date: 2023/3/16 4:11 PM
 */
class MusicDataVm(context: Application) : AndroidViewModel(context) {

    private val sourceManager = SourceManager(context)

    val musicFlow = MutableStateFlow<List<Song>>(emptyList())
    val indexFlow = MutableStateFlow(-1)
    val durationFlow = MutableStateFlow(0)
    val playFlow = MutableStateFlow(false)

    init {
        querySelectIndex()
    }

    fun fetchMusicData() {
        viewModelScope.launch(Dispatchers.IO) {
            sourceManager.getSongs().also {
                musicFlow.value = it
            }
        }
    }


    fun updateSelectIndex(index: SongIndex) {
        viewModelScope.launch(Dispatchers.IO) {
            sourceManager.updateIndex(index)
        }
    }

    fun querySelectIndex() {
        viewModelScope.launch(Dispatchers.IO) {
            sourceManager.queryIndex().let {
                indexFlow.value = it?.index ?: -1
            }
        }
    }

    fun dispatchPlayDuration(duration: Int) {
        durationFlow.value = duration
    }

    fun dispatchPlayState(isPlay: Boolean) {
        playFlow.value = isPlay
    }
}