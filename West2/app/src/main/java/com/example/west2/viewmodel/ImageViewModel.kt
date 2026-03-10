package com.example.west2.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.west2.data.model.ImageType
import com.example.west2.data.model.JournalImage
import com.example.west2.data.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImageViewModel (application: Application): AndroidViewModel(application){

    var id by mutableIntStateOf(0)

    fun updateId(newId:Int){
        id = newId
    }

    private val repository: JournalRepository = JournalRepository(application)

    private val _images = MutableStateFlow<List<JournalImage>>(emptyList())
    val images: StateFlow<List<JournalImage>> = _images.asStateFlow()

    // 初始化/刷新图片列表的方法
    fun loadImagesByJournalId(journalId: Int) {
        // 先清空旧数据（可选）
        _images.value = emptyList()

        viewModelScope.launch {
            // 关键：collect 持续监听 Room Flow 的数据变化
            repository.getImagesById(journalId).collect { newImages ->
                // 修正：直接赋值，而非 listOf(newImages)
                _images.value = newImages
            }
        }
    }

    fun updateImage(journalImage: JournalImage){
        viewModelScope.launch {
            repository.updateImage(journalImage)
        }
    }

    fun deleteImage(journalImage: JournalImage){
        viewModelScope.launch {
            repository.deleteImage(journalImage)
        }
    }

    fun insertImage(journalId: Int, imageType: ImageType, pathOrUrl: String) {
        viewModelScope.launch {
            val image = JournalImage(
                journalId = journalId,
                imageType = imageType,
                imagePathOrUrl = pathOrUrl
            )
            repository.insertImage(image)
        }
    }

    fun insertImages(
        journalId: Int,
        imageDataList: List<Pair<ImageType, String>>
    ) {
        // 前置校验：确保journalId有效（解决外键崩溃核心）
        if (journalId <= 0) {
            return
        }

        viewModelScope.launch {
            try {
                // 转换为JournalImage实体列表
                val imageList = imageDataList.map { (type, path) ->
                    JournalImage(
                        journalId = journalId, // 用有效日记ID
                        imageType = type,
                        imagePathOrUrl = path
                    )
                }
                // 调用批量插入，返回所有图片的ID列表（可选接收）
                val imageIds = repository.insertImages(imageList)
            } catch (e: Exception) {
            }
        }
    }
}