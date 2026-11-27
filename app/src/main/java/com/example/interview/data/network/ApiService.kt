package com.example.interview.data.network

import com.example.interview.data.model.ApiResponse
import com.example.interview.data.model.PageData
import com.example.interview.data.model.QuestionItem
import com.example.interview.data.model.QuestionType
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    /** 获取题目类型列表 */
    @GET("question/type")
    suspend fun getQuestionTypes(): ApiResponse<List<QuestionType>>

    /** 获取题目列表 */
    @GET("question/list")
    suspend fun getQuestionList(
        @Query("type") type: Int,
        @Query("questionBankType") questionBankType: Int = 10,
        @Query("keyword") keyword: String? = null,
        @Query("sort") sort: Int? = null,
        @Query("page") page: Int? = null,
        @Query("pageSize") pageSize: Int? = null
    ): ApiResponse<PageData<QuestionItem>>
}