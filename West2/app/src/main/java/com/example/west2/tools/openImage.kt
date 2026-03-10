package com.example.west2.tools

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.west2.data.model.ImageType
import com.example.west2.viewmodel.ImageViewModel
import java.io.InputStream
import androidx.core.net.toUri
import com.example.west2.viewmodel.EditViewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.compareTo

fun copyImageToPrivateDir(context: Context, uri: Uri): String? {
    return try {
        // 1. 创建私有目录（app内部存储，无需权限）
        val imageDir = File(context.filesDir, "journal_images")
        if (!imageDir.exists()) imageDir.mkdirs()

        // 2. 生成唯一文件名（避免重复）
        val fileName = "img_${System.currentTimeMillis()}.jpg"
        val targetFile = File(imageDir, fileName)

        // 3. 复制图片到私有目录
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(targetFile)
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        // 4. 返回私有目录路径
        targetFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun OpenImage(
    imageViewModel: ImageViewModel,
    editViewModel: EditViewModel
) {
    LaunchedEffect(editViewModel.id) {
        if (editViewModel.id > 0) {
            imageViewModel.loadImagesByJournalId(editViewModel.id)
        }
    }

    val context = LocalContext.current
    var toastMsg by remember { mutableStateOf("") }
    // 存储选择的图片 Uri
//    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) } // 存储多张图片Uri
//    var selectedBitmaps by remember { mutableStateOf<List<Bitmap?>>(emptyList()) } // 存储预览Bitmap

    val allImages by imageViewModel.images.collectAsStateWithLifecycle(initialValue = emptyList())


    val multipleGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(), // 多图选择契约
        onResult = { uris: List<Uri> ->
            if (uris.isEmpty()) {
                toastMsg = "未选择任何图片"
//                selectedImages = emptyList()
//                selectedBitmaps = emptyList()
                return@rememberLauncherForActivityResult
            }

            // 校验日记ID有效性（核心：避免外键崩溃）
            val journalId = editViewModel.id // 确保该ID>0（日记已保存）
            if (journalId <= 0) {
                toastMsg = "请先保存日记，再添加图片"
//                selectedImages = emptyList()
//                selectedBitmaps = emptyList()
                return@rememberLauncherForActivityResult
            }

            // 存储选中的Uri
//            selectedImages = uris

            // 批量处理图片：解析Bitmap + 转换为持久化路径
            val imageDataList = mutableListOf<Pair<ImageType, String>>() // 批量插入的参数
            val bitmaps = mutableListOf<Bitmap?>()

            uris.forEach { uri ->
                try {
                    // 解析Bitmap用于预览
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    bitmaps.add(bitmap)
                    inputStream?.close()

                    // 转换Uri为绝对路径（避免临时Uri失效）
                    val imagePath = copyImageToPrivateDir(context, uri) ?: run {
                        // 降级方案：仍用Uri（仅临时有效）
                        uri.toString()
                    }
                    imageDataList.add(ImageType.LOCAL to imagePath)

                } catch (e: Exception) {
                    bitmaps.add(null)
                    toastMsg = "部分图片解析失败：${e.message ?: "未知错误"}"
                }
            }

            // 存储预览Bitmap
//            selectedBitmaps = bitmaps

            // 核心：调用ViewModel的批量插入方法
            imageViewModel.insertImages(
                journalId = journalId,
                imageDataList = imageDataList
            )

            toastMsg = "成功选择${imageDataList.size}张图片并保存"
        }
    )

    // 1. 权限请求 Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // 权限通过，打开相册选择图片
                multipleGalleryLauncher.launch("image/*") // "image/*" 表示选择所有类型图片
            } else {
                toastMsg = "需要读取相册权限才能选择图片，请在设置中开启"
            }
        }
    )

    // 3. 展示 Toast 提示
    LaunchedEffect(toastMsg) {
        if (toastMsg.isNotBlank()) {
            Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
            toastMsg = ""
        }
    }

// 4. 界面布局（新增数据库图片展示）

    Text(text = "图集：点击增加图片", fontSize = 20.sp, fontFamily = FontFamily.Monospace , fontWeight = Bold,
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            READ_MEDIA_IMAGES
                        } else {
                            READ_EXTERNAL_STORAGE
                        }
                        permissionLauncher.launch(permission)
                    }
                )
            }
    )
            // 展示数据库中已保存的图片（从 Uri 恢复展示）
            if (allImages.isNotEmpty()) {
                Text(
                    text = "已保存的图片",
                    modifier = Modifier.padding(bottom = 10.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(allImages) { image ->
                        // 将数据库中的 Uri 字符串转回 Uri 并解析为 Bitmap
                        val savedBitmap = remember(image.imagePathOrUrl) {
                            mutableStateOf<Bitmap?>(null)
                        }

                        LaunchedEffect(image.imagePathOrUrl) {
                            savedBitmap.value = try {
                                if (image.imagePathOrUrl.startsWith("/")) {
                                    // 私有目录路径：直接解码文件
                                    BitmapFactory.decodeFile(image.imagePathOrUrl)
                                } else {
                                    // 降级：Uri字符串
                                    val savedUri = image.imagePathOrUrl.toUri()
                                    val inputStream = context.contentResolver.openInputStream(savedUri)
                                    val bitmap = BitmapFactory.decodeStream(inputStream)
                                    inputStream?.close()
                                    bitmap
                                }
                            } catch (e: Exception) {
                                e.printStackTrace() // 打印异常，定位具体问题
                                null
                            }
                        }

                        savedBitmap.value?.let { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "已保存图片",
                                modifier = Modifier
                                    .padding(end = 10.dp)
                                    .size(150.dp)
                                    .clickable {
                                        imageViewModel.deleteImage(image)
                                        toastMsg = "图片已删除"
                                    }
                            )
                        }
                    }
                }
            }
        }




