package com.example.west2.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.west2.data.model.Journal
import com.example.west2.data.repository.JournalRepository
import kotlinx.coroutines.flow.Flow

class EditViewModel(application: Application): AndroidViewModel(application){
    private val repository: JournalRepository = JournalRepository(application)

    var id by mutableIntStateOf(0)

    var title by mutableStateOf("")
        private set

    var body by mutableStateOf("")
        private set

    var editTime: String by mutableStateOf("") //时间轴
        private set

    var createTime:String by mutableStateOf("")
        private set

    var city:String by mutableStateOf("")
        private set


    suspend fun getJournal(id: Int): Journal? {
        return repository.getJournalByID(id)
    }

    //持续更新逻辑
    fun getJournalFlow(id: Int): Flow<Journal?> {
        // 仓库层返回 Flow（Room 的 @Query 方法支持返回 Flow）
        return repository.getJournalByIDFlow(id)
    }

    fun updateTitle(newTitle: String) {
        title = newTitle
    }

    // 更新正文（供 UI 层调用）
    fun updateBody(newBody: String) {
        body = newBody
    }

    fun updateEditTime(newEditTime: String){
        editTime =  newEditTime
    }

    fun createTime(createTime:String){
        this.createTime = createTime
    }

    fun updateCity(newCity: String?){
        if (newCity != null) {
            city = newCity
        }
    }
}