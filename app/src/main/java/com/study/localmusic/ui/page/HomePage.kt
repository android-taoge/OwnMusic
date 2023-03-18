package com.study.localmusic.ui.page

import android.Manifest
import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.study.localmusic.R
import com.study.localmusic.model.Song
import com.study.localmusic.ui.widget.PlayControl
import com.study.localmusic.ui.widget.ShowReasonDialog
import com.study.localmusic.util.formatTime
import com.study.localmusic.util.getAlbumPicture
import com.study.localmusic.util.go2AppSettings
import kotlin.system.exitProcess

/**
 * @Author tangtao
 * @Description:
 * @Date: 2023/3/16 10:11 PM
 */

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun HomePage(
    musics: SnapshotStateList<Song>,
    selectIndex: Int,
    playDuration:Int,
    isPlay: Boolean,
    permissionGrantedAction: () -> Unit,
    serviceLoadData: () -> Unit,
    updateCheckAction: (Int) -> Unit,
    bottomPlayAction: () -> Unit,
    gotoSettingAction: () -> Unit
) {

    val showDialog = remember {
        mutableStateOf(false)
    }

    val readExternalState =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    RequestReadExternalPermission(
        readExternalState,
        showDialog,
        grantedAction = { permissionGrantedAction() },
        toSettingAction = { gotoSettingAction() })

    Column(Modifier.fillMaxSize()) {
        when {
            musics.isEmpty() -> EmptyPage {
                showDialog.value = true
            }
            else -> {
                serviceLoadData()
                Column(Modifier.weight(1f)) {
                    MusicList(musics, modifier = Modifier.weight(1f), selectIndex) { clickPos ->
                        updateCheckAction(clickPos)
                    }

                    val bitmap =
                        if (selectIndex != -1) {
                            getAlbumPicture(
                                LocalContext.current,
                                musics[selectIndex].path,
                                1
                            )
                        } else {
                            BitmapFactory.decodeResource(
                                LocalContext.current.resources,
                                R.drawable.icon_music
                            )
                        }

                    PlayControl(
                        musics[if (selectIndex == -1) 0 else selectIndex].song,
                        playDuration = playDuration,
                        bitmap,
                        isPlay,
                        playAction = {
                            bottomPlayAction()
                        })

                }
            }
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestReadExternalPermission(
    readExternalState: PermissionState,
    showDialog: MutableState<Boolean>,
    grantedAction: () -> Unit,
    toSettingAction: () -> Unit
) {

    when {
        readExternalState.hasPermission -> {
            grantedAction()
        }
        else -> {
            if (showDialog.value) {
                val shouldToSetting = !readExternalState.shouldShowRationale &&
                        readExternalState.permissionRequested
                val message =
                    if (!shouldToSetting) "扫描本地音乐需要读取权限，请同意该权限方可正常使用"
                    else "请到系统设置-应用-权限管理手动开启读取手机存储权限"
                ShowReasonDialog(
                    showDialog,
                    message = message,
                    confirmAction = {
                        if (!shouldToSetting) {
                            readExternalState.launchPermissionRequest()
                        } else {
                            toSettingAction()
                        }
                        showDialog.value = false
                    },
                    cancelAction = {
                        if (!shouldToSetting) {
                            showDialog.value = false
                        } else {
                            exitProcess(0)
                        }
                    })
            }

        }
    }

}

@Composable
fun ColumnScope.MusicList(
    musicState: List<Song>,
    modifier: Modifier,
    selectIndex: Int,
    clickAction: (Int) -> Unit
) {
    LazyColumn(modifier = modifier.weight(1f)) {
        itemsIndexed(musicState) { index, song ->
            SongItem(index, song, selectIndex) {
                clickAction(it)
            }
            if (index < musicState.size) {
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .height(.5.dp),
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun SongItem(index: Int, song: Song, selectIndex: Int, clickAction: (Int) -> Unit) {
    val backColor = if (index == selectIndex) Color(0xD2000000) else Color.White
    val textColor = if (index == selectIndex) Color.White else Color.Black
    Row(
        Modifier
            .clickable { clickAction(index) }
            .fillMaxWidth()
            .background(color = backColor)
            .height(60.dp)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "${index + 1}", fontSize = 14.sp, color = textColor)
        Column(
            Modifier
                .padding(start = 10.dp)
                .weight(1f)
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                overflow = TextOverflow.Ellipsis,
                text = song.song,
                fontSize = 16.sp,
                color = textColor
            )
            Row(
                Modifier
                    .padding(top = 5.dp)
                    .weight(1f)
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = song.singer,
                    fontSize = 14.sp,
                    color = textColor
                )

                Text(
                    text = "${formatTime(song.duration)}",
                    fontSize = 14.sp,
                    color = textColor
                )
            }
        }

    }
}

@Composable
fun EmptyPage(action: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = { action() }) {
            Text(text = "扫描本地歌曲", fontSize = 16.sp, color = Color.White)
        }
    }
}