package com.example.interview.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.interview.R
import com.example.interview.data.model.SortType
import com.example.interview.ui.theme.AppColors

@Composable
fun FilterButton(
    text: String,
    isNew: Boolean = false,       // NEW 标记
    selected: Boolean = false,    // 是否选中
    sort: SortType = SortType.Default, // 当前排序方式
    isSort: Boolean = false,      // 是否显示排序箭头
    onClick: () -> Unit = {}      // 点击回调
) {
    // 箭头高亮逻辑：
    // sort.value % 2 == 1 → 上箭头高亮（升序，从低到高）
    // sort.value % 2 == 0 → 下箭头高亮（降序，从高到低）
    val upActive = selected && isSort && sort.value % 2 == 1
    val downActive = selected && isSort && sort.value % 2 == 0

    // 外层 Box = margin（对应鸿蒙 margin: top 12, right isNew?26:10）
    Box(
        modifier = Modifier.padding(top = 12.dp, end = if (isNew) 26.dp else 10.dp)
    ) {
        // 按钮本体
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = 40.dp)
                .height(30.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(AppColors.GrayBg)
                .clickable { onClick() }
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text, fontSize = 12.sp,
                    color = if (selected) AppColors.MainColor else AppColors.Black
                )

                // 排序上下箭头
                if (isSort) {
                    Column(
                        modifier = Modifier.padding(start = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // 上箭头（ArrowDropDown 旋转 180°）
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "升序",
                            modifier = Modifier.size(12.dp, 6.dp)
                                .graphicsLayer { rotationZ = 180f },
                            tint = if (upActive) AppColors.MainColor else AppColors.Black
                        )
                        // 下箭头
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "降序",
                            modifier = Modifier.size(12.dp, 6.dp),
                            tint = if (downActive) AppColors.MainColor else AppColors.Black
                        )
                    }
                }
            }

        }

        // NEW 标记 - 放在外层 Box，避免被内层 clip 裁剪
        if (isNew) {
            Image(
                painter = painterResource(R.drawable.ic_home_new),
                contentDescription = "new",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 10.dp, y = (-7).dp)
                    .width(32.dp)
                    .height(14.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}