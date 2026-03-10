package com.example.west2.data.repository

import android.content.Context
import com.example.west2.data.base.AppDatabase
import com.example.west2.data.dao.JournalDao
import com.example.west2.data.dao.JournalImageDao
import com.example.west2.data.model.Journal
import com.example.west2.data.model.JournalImage
import kotlinx.coroutines.flow.Flow

class JournalRepository(context: Context){
    private val journalDao: JournalDao
    private val imageDao : JournalImageDao

    init{
        val db= AppDatabase.getDatabase(context)
        journalDao = db.journalDao()
        imageDao = db.journalImageDao()
    }

    //日记相关功能
    fun getJournals(): Flow<List<Journal>> = journalDao.getAllJournals()

    suspend fun getJournalByID(id: Int): Journal? = journalDao.getJournalById(id)

    suspend fun insertJournal(journal: Journal): Long = journalDao.insertJournal(journal)

    suspend fun updateJournal(journal: Journal) = journalDao.updateJournal(journal)

    suspend fun deleteJournal(journal: Journal) = journalDao.deleteJournal(journal)

    fun searchJournals(string: String):Flow<List<Journal>> = journalDao.searchJournals(string)

    // 持续更新逻辑
    fun getJournalByIDFlow(journalId: Int): Flow<Journal?> = journalDao.getJournalByIDFlow(journalId)


    //图片相关功能
    fun getImagesById(journalId: Int): Flow<List<JournalImage>> {
        // 直接透传 Room 的可观察 Flow（关键：Room 会自动监听数据库变化）
        return imageDao.getImagesByJournalId(journalId)
    }

    suspend fun insertImage(journalImage: JournalImage):Long = imageDao.insertImage(journalImage)

    suspend fun insertImages(journalImage: List<JournalImage>): List<Long> =  imageDao.insertImages(journalImage)
    suspend fun updateImage(journalImage: JournalImage) = imageDao.updateImage(journalImage)

    suspend fun deleteImage(journalImage: JournalImage) = imageDao.deleteImage(journalImage)
}


