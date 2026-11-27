package com.example.interview.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.interview.R
import com.example.interview.ui.theme.AppColors

/**
 * 打卡组件 - 1:1复刻鸿蒙 HcClockIn.ets
 * 使用 ic_common_unclock/ic_common_clocked 背景图片
 *
 * 业务逻辑(对应鸿蒙 requestClockIn):
 * - clockInCount > 0: 已连续打卡, 显示天数
 * - clockInCount == 0: 未打卡, 显示"打卡"文字
 */
@Composable
fun HMClockIn(
    clockInCount: Int = 0,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .width(74.dp)
            .height(28.dp)
            .paint(
                painter = painterResource(
                    if (clockInCount > 0) R.drawable.ic_common_clocked
                    else R.drawable.ic_common_unclock
                ),
                contentScale = ContentScale.Fit
            )
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (clockInCount > 0) {
            // 对应鸿蒙: Column().margin({ left: 30 })
            Column(
                modifier = Modifier.padding(start = 30.dp)
            ) {
                Text(
                    text = "已连续打卡",
                    fontSize = 8.sp,
                    lineHeight = 10.sp,
                    color = AppColors.Black
                )
                Text(
                    text = buildAnnotatedString { // 构建富文本字符串
                        withStyle( // 第一段样式
                            androidx.compose.ui.text.SpanStyle(
                                fontWeight = FontWeight.SemiBold, // 半粗体
                                fontSize = 12.sp // 12sp字号
                            )
                        ) {
                            append("$clockInCount")
                        }
                        withStyle(androidx.compose.ui.text.SpanStyle(fontSize = 10.sp)) {
                            append("天")
                        }
                    },
                    lineHeight = 14.sp,
                    color = AppColors.Black
                )
            }
        } else {
            // 对应鸿蒙: Text('打卡').margin({ left: 30, top: -4 })
            Text(
                text = "打卡",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.Black,
                modifier = Modifier
                    .padding(start = 30.dp)
                    .offset(y = (-4).dp)
            )
        }
    }
}