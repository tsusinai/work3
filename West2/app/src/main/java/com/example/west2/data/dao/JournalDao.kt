package com.example.west2.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.west2.data.model.Journal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface JournalDao {
    // 获取所有日记（按更新时间倒序）
    @Query("SELECT * FROM journals ORDER BY updateTime DESC")
    fun getAllJournals(): Flow<List<Journal>>

    // 根据ID获取日记
    @Query("SELECT * FROM journals WHERE id = :journalId")
    suspend fun getJournalById(journalId: Int): Journal?

    // 插入日记Repository
    @Insert
    suspend fun insertJournal(journal: Journal): Long

    // 更新日记
    @Update
    suspend fun updateJournal(journal: Journal)

    // 删除日记
    @Delete
    suspend fun deleteJournal(journal: Journal)


    // 新增：模糊搜索日记（按标题+内容，更新时间倒序）
    @Query("""
        SELECT * FROM journals 
        WHERE (:keyword != '' 
        AND (title LIKE '%' || :keyword || '%' 
        OR content LIKE '%' || :keyword || '%'))
        ORDER BY updateTime DESC
    """)
    fun searchJournals(keyword: String): Flow<List<Journal>>

    //持续更新，无需手动保存
    @Query("SELECT * FROM journals WHERE id = :journalId LIMIT 1")
    fun getJournalByIDFlow(journalId: Int): Flow<Journal?>
}