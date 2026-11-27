package com.example.interview.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.interview.data.model.QuestionItem
import com.example.interview.data.model.QuestionType
import com.example.interview.data.network.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // 分类状态
    private val _questionTypes = MutableStateFlow<List<QuestionType>>(emptyList())
    val questionTypes = _questionTypes.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    // 列表状态 新增
    private val _questionList = MutableStateFlow<List<QuestionItem>>(emptyList())
    val questionList = _questionList.asStateFlow()

    private val _activeIndex = MutableStateFlow(0)
    val activeIndex = _activeIndex.asStateFlow()

    // 分页加载 新增
    private val _isFinished = MutableStateFlow(false)
    val isFinished = _isFinished.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore = _isLoadingMore.asStateFlow()

    private var page = 1 // 当前页码，普通变量，不需要通知ui

    // 切花分类
    fun selectType(index: Int) {
        _activeIndex.value = index
        refreshList()
    }

    // 刷新列表 重置到第一页
    fun refreshList() {
        page = 1
        _isFinished.value = false // 切换分类/刷新时重置
        loadQuestionList()
    }


    // 获取题目分类
    fun loadQuestionTypes() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _questionTypes.value = HttpClient.request { HttpClient.api.getQuestionTypes() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            _loading.value = false
        }
    }

    /**
     * 加载更多
     */
    fun loadMore() {
        if (_isFinished.value || _isLoadingMore.value) return // 防重复
        _isLoadingMore.value = true
        loadQuestionList()
    }

    /**
     * 加载题目列表
     */
    fun loadQuestionList() {
        viewModelScope.launch {
            try {
                val typeId = _questionTypes.value.getOrNull(_activeIndex.value)?.id ?: return@launch
                val result = HttpClient.request {
                    HttpClient.api.getQuestionList(type = typeId, page = page, pageSize = 10)
                }
                if (page == 1) {
                    _questionList.value = result.rows // 第一页 替换
                } else {
                    _questionList.value += result.rows // 后续页 追加
                }
                if (page >= result.pageTotal) {
                    _isFinished.value = true // 已加载最后一页
                } else {
                    page++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            _isLoadingMore.value = false
        }
    }


}