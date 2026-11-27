package com.example.interview.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.interview.data.model.QuestionItem
import com.example.interview.ui.theme.AppColors

@Composable
fun HMQuestionItemRow(item: QuestionItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 难度标签 + 题干
        Row(verticalAlignment = Alignment.CenterVertically) {
            HMTag(difficulty = item.difficulty)
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = item.stem,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
        // 底部数据
        Row {
            Text("点赞 ${item.likeCount}", fontSize = 13.sp, color = AppColors.Gray01)
            Text(
                "浏览 ${item.views}",
                fontSize = 13.sp,
                color = AppColors.Gray01,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            if (item.readFlag == 1) {
                Text("已看过", fontSize = 13.sp, color = AppColors.Gray01)
            }
        }
    }
}