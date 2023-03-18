package com.study.localmusic.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

/**
 * @Author tangtao
 * @Description:
 * @Date: 2023/3/16 10:13 PM
 */
@Composable
fun ShowReasonDialog(
    showDialog: MutableState<Boolean>,
    title: String = "温馨提示",
    message: String,
    confirmAction: () -> Unit,
    cancelAction: () -> Unit
) {

    AlertDialog(
        onDismissRequest = {
            showDialog.value = false
        },
        title = {
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.Black
            )
        },
        text = { Text(text = message, fontSize = 14.sp, color = Color.Black) },
        buttons = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Button(
                    modifier = Modifier
                        .padding(start = 5.dp, bottom = 5.dp)
                        .weight(1f)
                        .height(50.dp), onClick = {
                        cancelAction()
                        showDialog.value = false

                    }, shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "取消",
                    )
                }
                Spacer(modifier = Modifier.width(15.dp))
                Button(
                    modifier = Modifier
                        .padding(end = 5.dp, bottom = 5.dp)
                        .weight(1f)
                        .height(50.dp), onClick = {
                        confirmAction()
                        //showDialog.value = false
                    }, shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "确定"
                    )
                }
            }
        },
        modifier = Modifier.width(300.dp),
        shape = RoundedCornerShape(10.dp),
        backgroundColor = Color.White,
        contentColor = Color.White,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    )
}