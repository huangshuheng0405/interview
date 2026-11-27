package com.example.interview.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


/**
 * 灵活的Int序列化器 - 兼容API返回的Int或String类型
 * 比如： 同一个 difficulty 字段，列表 API 返回数字，详情 API 返回字符串。Kotlinx Serialization 默认严格类型匹配——如果 data class 声明的是 Int，收到 "3"（字符串）就会直接抛异常崩溃。
 * 反序列化流程：
 *   1. 先尝试 decodeInt()        → 正常情况（列表接口返回 Int）
 *   2. 失败 → 再尝试 decodeString().toInt()  → 详情接口返回 String "3"
 *   3. 都失败 → 返回 0（兜底，不崩溃）
 */
object FlexibleIntSerializer : KSerializer<Int> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("FlexibleInt", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Int) = encoder.encodeInt(value)
    override fun deserialize(decoder: Decoder): Int {
        return try {
            decoder.decodeInt()
        } catch (e: Exception) {
            try {
                decoder.decodeString().toInt()
            } catch (e2: Exception) {
                0
            }
        }
    }
}


@Serializable
data class QuestionType(
    val id: Int,
    val name: String,
    val displayNewestFlag: Int
)

@Serializable
data class QuestionItem(
    val id: String,
    /** 题干 */
    val stem: String,
    /** 难度 */
    val difficulty: Int = 0,
    /** 点赞数 */
    val likeCount: Int = 0,
    /** 浏览数 */
    val views: Int = 0,
    /** 是否已看 0-未看 1-已看 */
    val readFlag: Int = 0,
    /** 是否点赞 0-否 1-是 */
    val likeFlag: Int = 0,
    /** 是否收藏 0-否 1-是 */
    val collectFlag: Int = 0
)


@Serializable
data class PageData<T>(
    val total: Int,
    val pageTotal: Int,
    val rows: List<T>
)

enum class SortType(val value: Int) {
    Default(0),
    DifficultyLow(10),
    DifficultyHigh(11),
    ViewLow(20),
    ViewHigh(21),
    Commend(30)
}

@Serializable
data class ApiResponse<T>(
    val code: Int,
    val data: T? = null,
    val message: String? = null
)