package com.example.interview.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.interview.data.model.QuestionType
import com.example.interview.data.model.SortType
import com.example.interview.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(
    filterIndex: Int,
    types: List<QuestionType>,
    onDismiss: () -> Unit,
    filterSort: SortType,
    onSortChange: (SortType) -> Unit,
    onIndexChange: (Int) -> Unit,
    onConfirm: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppColors.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 顶部: 重置 | 筛选题目 | 完成
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "重置",
                    fontSize = 16.sp,
                    color = AppColors.Gray03,
                    modifier = Modifier.clickable {
                        onIndexChange(0)
                        onSortChange(SortType.Default)
                    }
                )
                Text(
                    "筛选题目", Modifier.weight(1f), fontSize = 18.sp,
                    fontWeight = FontWeight.Medium, textAlign = TextAlign.Center
                )
                Text(
                    "完成",
                    fontSize = 16.sp,
                    color = AppColors.MainColor,
                    modifier = Modifier.clickable { onConfirm() })
            }

            Spacer(Modifier.height(16.dp))

            // 题目排序
            Text(
                "题目排序", fontSize = 14.sp, fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 20.dp)
            )

            // 排序按钮 - 每个都是: 灰色圆角背景 + 文字
            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterButton(text = "默认", selected = filterSort == SortType.Default, onClick = {
                    onSortChange(
                        SortType.Default
                    )
                })
                FilterButton(
                    text = "浏览量",
                    isSort = true,
                    selected = filterSort == SortType.ViewLow || filterSort == SortType.ViewHigh,
                    sort = filterSort,
                    onClick = {
                        if (filterSort == SortType.ViewLow || filterSort == SortType.ViewHigh) {
                            onSortChange(if (filterSort == SortType.ViewLow) SortType.ViewHigh else SortType.ViewLow)
                        } else {
                            onSortChange(SortType.ViewLow)
                        }
                    })

                FilterButton(
                    text = "难度",
                    isSort = true,
                    selected = filterSort == SortType.DifficultyLow || filterSort == SortType.DifficultyHigh,
                    sort = filterSort,
                    onClick = {
                        if (filterSort == SortType.DifficultyLow || filterSort == SortType.DifficultyHigh) {
                            onSortChange(if (filterSort == SortType.DifficultyLow) SortType.DifficultyHigh else SortType.DifficultyLow)
                        } else {
                            onSortChange(SortType.DifficultyLow)
                        }
                    }
                )
                FilterButton(
                    text = "推荐",
                    selected = filterSort == SortType.Commend,
                    onClick = { onSortChange(SortType.Commend) }
                )
            }

            Spacer(Modifier.height(20.dp))

            // 选择分类
            Text(
                "选择分类", fontSize = 14.sp, fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 20.dp)
            )

            FlowRow {
                types.forEachIndexed { index, type ->
                    FilterButton(
                        text = type.name,
                        isNew = type.displayNewestFlag == 1,
                        selected = filterIndex == index,
                        onClick = { onIndexChange(index) }
                    )
                }
            }

            // 分类按钮 - 静态
            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(AppColors.GrayBg)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Android", fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(AppColors.GrayBg)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("鸿蒙", fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(AppColors.GrayBg)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("iOS", fontSize = 12.sp)
                }
            }
        }
    }
}