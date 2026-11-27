package com.example.interview.ui.pages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.interview.data.local.UserPreferences
import com.example.interview.data.model.QuestionItem
import com.example.interview.data.network.HttpClient
import com.example.interview.ui.components.HMQuestionItemRow
import com.example.interview.ui.theme.AppColors
import kotlinx.coroutines.launch

@Composable
fun SearchPage(navController: NavHostController) {


    var isSearch by remember { mutableStateOf(false) }  // false=显示历史, true=显示结果

    var keyword by remember { mutableStateOf("") }        // 输入框文字
    var searchResults by remember { mutableStateOf<List<QuestionItem>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }   // 加载中

    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    // 搜索关键字
    var searchKeywords by remember { mutableStateOf<List<String>>(emptyList()) }  // 搜索历史列表
    val preferences = remember { UserPreferences(context.applicationContext) }    // DataStore 实例

    var isDeleting by remember { mutableStateOf(false) } // 删除模式

    // 页面加载时读取搜索历史
    LaunchedEffect(Unit) {
        searchKeywords = preferences.getAllSearchHistory()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // ========== 1. 搜索栏 ==========
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()      // 延伸到状态栏下方
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 搜索输入框
            OutlinedTextField(
                value = keyword,
                onValueChange = {
                    keyword = it
                    if (it.isEmpty()) isSearch = false // 清空输入 回到历史页
                },
                placeholder = { Text("请输入试题关键字", fontSize = 14.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true,            // 单行输入
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                // 键盘右下角显示搜索按钮
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                // 点击键盘搜索按钮触发
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (keyword.isNotBlank()) {
                            // 收起键盘
                            keyboardController?.hide()
                            // 保存搜索历史
                            scope.launch {
                                preferences.addSearchHistory(keyword)
                                searchKeywords = preferences.getAllSearchHistory()
                            }
                            val kw = keyword
                            scope.launch {
                                searching = true
                                try {
                                    val result = HttpClient.request {
                                        HttpClient.api.getQuestionList(
                                            type = 0,
                                            keyword = kw,
                                            pageSize = 10
                                        )
                                    }
                                    searchResults = result.rows
                                } catch (e: Exception) {
                                    searchResults = emptyList()
                                    Toast.makeText(context, "搜索失败", Toast.LENGTH_SHORT).show()
                                }
                                searching = false
                                isSearch = true // 切换到结果页
                            }
                        }
                    }
                )
            )
            Spacer(modifier = Modifier.width(16.dp))
            // 取消按钮 - 返回上一页
            Text(
                text = "取消",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.Black,
                modifier = Modifier.clickable { navController.popBackStack() }
            )
        }

        HorizontalDivider(thickness = 0.5.dp, color = AppColors.GrayBorder)

        // ========== 2. 搜索历史 / 搜索结果 ==========
        if (isSearch) {
            if (searching) {
                // 加载中
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (searchResults.isEmpty()) {
                // 无结果
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("没有找到相关试题", color = AppColors.Gray01)
                }
            } else {
                // --- 搜索结果列表 ---
                LazyColumn {
                    items(searchResults) { item ->
                        HMQuestionItemRow(item)
                    }
                }
            }
        } else {
            // --- 搜索历史 ---
            Column(modifier = Modifier.padding(16.dp)) {
                // 标题行: 搜索记录 + 删除图标
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("搜索记录", fontSize = 15.sp, color = AppColors.Gray01)
                    if (isDeleting) {
                        Row {
                            Text(
                                "全部删除", fontSize = 14.sp, color = AppColors.Gray01,
                                modifier = Modifier.clickable {
                                    scope.launch {
                                        preferences.clearSearchHistory()
                                        searchKeywords = emptyList()
                                        isDeleting = false
                                    }
                                }
                            )
                            Text(" | ", fontSize = 14.sp, color = AppColors.Gray01)
                            Text(
                                "完成", fontSize = 14.sp, color = AppColors.Gray01,
                                modifier = Modifier.clickable { isDeleting = false }
                            )
                        }
                    } else {
                        Icon(
                            Icons.Default.Delete, contentDescription = "删除",
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { isDeleting = true },
                            tint = AppColors.Gray01
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 关键字标签 - FlowRow 自动换行
                FlowRow(modifier = Modifier.fillMaxWidth()) {
                    searchKeywords.forEach { kw ->
                        // 每个标签: 灰色圆角背景 + 文字
                        Row(
                            modifier = Modifier
                                .padding(end = 16.dp, bottom = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF3F4F5))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .clickable {
                                    // 点击历史标签 -> 填入输入框 -> 触发搜索
                                    keyword = kw
                                    scope.launch {
                                        preferences.addSearchHistory(kw) // 重新置顶
                                        searchKeywords = preferences.getAllSearchHistory()
                                        searching = true
                                        try {
                                            val result = HttpClient.request {
                                                HttpClient.api.getQuestionList(
                                                    type = 0,
                                                    keyword = keyword,
                                                    pageSize = 10
                                                )
                                            }
                                            searchResults = result.rows
                                        } catch (e: Exception) {
                                            searchResults = emptyList()
                                        }
                                        searching = false
                                        isSearch = true
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(kw, fontSize = 14.sp, color = Color(0xFF6F6F6F))
                        }
                    }
                }
            }
        }
    }
}