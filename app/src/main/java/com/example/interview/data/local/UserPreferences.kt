package com.example.interview.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// 顶层 DataStore 单例 — 确保全局只有一个实例（多个实例会崩溃）
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "interview_settings")

class UserPreferences(context: Context) {

    private val dataStore = context.dataStore

    // ============ 搜索历史 ============

    // 每个搜索词对应一个独立的 key，方便逐条删除
    // 例如: search_Kotlin协程、search_Handler原理
    private fun searchKey(keyword: String) = stringPreferencesKey("search_$keyword")

    /** 添加搜索历史 */
    suspend fun addSearchHistory(keyword: String) {
        dataStore.edit { it[searchKey(keyword)] = keyword }
    }

    /** 删除单条搜索历史 */
    suspend fun deleteSearchHistory(keyword: String) {
        dataStore.edit { it.remove(searchKey(keyword)) }
    }

    /** 清空所有搜索历史 */
    suspend fun clearSearchHistory() {
        dataStore.edit { prefs ->
            // 找出所有以 "search_" 开头的 key，全部删除
            val keysToRemove = prefs.asMap().keys.filter { it.name.startsWith("search_") }
            keysToRemove.forEach { prefs.remove(it) }
        }
    }

    /** 获取所有搜索历史 */
    suspend fun getAllSearchHistory(): List<String> {
        return dataStore.data.map { prefs ->
            prefs.asMap()
                .filter { it.key.name.startsWith("search_") }  // 只取搜索历史的 key
                .values
                .map { it.toString() }
        }.first()  // first() 只取第一个值（一次性读取，不是持续监听）
    }
}