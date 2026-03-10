package com.example.west2.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.west2.data.model.JournalImage
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalImageDao {
    // 根据日记ID获取所有图片（按顺序）
    @Query("SELECT * FROM journal_images WHERE journalId = :journalId")
    fun getImagesByJournalId(journalId: Int): Flow<List<JournalImage>>
    // 插入图片
    @Insert
    suspend fun insertImage(image: JournalImage): Long

    @Insert
    suspend fun insertImages(images: List<JournalImage>): List<Long>

    // 更新图片
    @Update
    suspend fun updateImage(image: JournalImage)

    // 删除图片
    @Delete
    suspend fun deleteImage(image: JournalImage)

    // 根据日记ID删除所有图片
    @Query("DELETE FROM journal_images WHERE journalId = :journalId")
    suspend fun deleteImagesByJournalId(journalId: Int)

}