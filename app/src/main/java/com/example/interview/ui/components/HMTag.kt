package com.example.interview.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.interview.ui.theme.AppColors


/**
 * 标签组件
 * 对应鸿蒙项目中的 HcTag.ets
 *
 * @param difficulty 难度等级 1-5
 * @param text 自定义文字
 * @param color 自定义颜色
 */
@Composable
fun HMTag(
    difficulty: Int = 0,
    text: String = "",
    color: Color = AppColors.Green
) {
    val tagInfo = when (difficulty) {
        1, 2 -> "简单" to AppColors.Green
        3, 4 -> "一般" to AppColors.Blue
        5 -> "困难" to AppColors.MainColor
        else -> text to color
    }

    Text(
        text = tagInfo.first,
        fontSize = 10.sp,
        color = tagInfo.second,
        modifier = Modifier
            .defaultMinSize(minWidth = 34.dp)
            .height(24.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(AppColors.GrayBg)
            .wrapContentWidth(Alignment.CenterHorizontally)
            .wrapContentHeight(Alignment.CenterVertically)
            .padding(horizontal = 6.dp)
    )
}