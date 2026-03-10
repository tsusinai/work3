package com.example.west2.tools

import java.text.SimpleDateFormat
import java.util.Locale

// 封装时间格式化工具（全局复用）
object TimeUtil {
    // 定义常用格式：年-月-日 时:分:秒
    private val timeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    // 时间戳 → 格式化字符串
    fun timestampToStr(timestamp: Long): String {
        return timeFormatter.format(timestamp)
    }

    // 快捷获取当前格式化时间
    fun getCurrentTimeStr(): String {
        return timestampToStr(System.currentTimeMillis())
    }
}