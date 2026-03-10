package com.example.west2.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.west2.data.model.Journal
import com.example.west2.data.repository.JournalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class JournalViewModel (application: Application): AndroidViewModel(application){

    private val repository: JournalRepository = JournalRepository(application)

    val getJournals: Flow<List<Journal>> = repository.getJournals()

    var searchWord: String by mutableStateOf("")

    fun setKeyword(keyword: String) {
        println("setKeyword 被调用，关键词：$keyword") // 加日志
        println("searchWord 最新值：$searchWord") // 确认状态真的更新了
         searchWord = keyword
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchJournal: Flow<List<Journal>> = snapshotFlow { searchWord }
        .filter { it.isNotBlank() } // 过滤空/纯空格
        .distinctUntilChanged() // 避免重复查询
        .debounce(300)
        .flatMapLatest { keyword ->
            println("ViewModel: 触发搜索 → $keyword") // 验证关键词无延迟
            repository.searchJournals(keyword)
        }

    fun insertJournal(title: String, content: String,updateTime:String,createTime:String,city:String) {
        viewModelScope.launch {
            repository.insertJournal(Journal(title = title, content = content , updateTime = updateTime, createTime = createTime , city = city))
        }
    }

    fun deleteJournal(journal: Journal){
        viewModelScope.launch {
            repository.deleteJournal(journal)
        }
    }

    fun updateJournal(journal: Journal) {
        viewModelScope.launch {
            repository.updateJournal(journal)
        }
    }
}