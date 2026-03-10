package com.example.west2.screens

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.west2.NavRoutes
import com.example.west2.data.model.Journal
import com.example.west2.tools.WeatherDisplay
import com.example.west2.tools.checkLocationPermission
import com.example.west2.viewmodel.JournalViewModel
import java.util.jar.Manifest


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun Home(navController: NavController){
    val journalViewModel: JournalViewModel = viewModel()
//    val imageViewModel: ImageViewModel = viewModel ()

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(top = 10.dp, start = 6.dp , end = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        item{TopInf()}
        item{JournalList(journalViewModel,navController = navController)}
        item{ AddButton(onClick = { navController.navigate(NavRoutes.editRoute(0))}
        ) }
    }
}

@Composable
fun JournalList(
    journalViewModel: JournalViewModel,
    navController: NavController
){

    val searchWord = journalViewModel.searchWord
    val searchJournals by journalViewModel.searchJournal.collectAsState(initial = emptyList())
    val journals by journalViewModel.getJournals.collectAsState(initial = emptyList())

    MySearchBar(journalViewModel.searchWord
    ) { searchWord -> journalViewModel.setKeyword(searchWord) }

    LaunchedEffect(searchWord) {
        println("UI 层感知到 searchWord 变化：${searchWord}")
    }

    Spacer(modifier = Modifier.size(10.dp))
    Text(text = "我的笔记：", fontSize = 20.sp, fontFamily = FontFamily.Monospace , fontWeight = Bold,modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.size(10.dp))


    if(journalViewModel.searchWord.isNotEmpty()){
        if (searchJournals.isEmpty()) {
            Text(text = "无结果", fontSize = 18.sp, fontFamily = FontFamily.Monospace , fontWeight = Bold, modifier = Modifier.padding(horizontal = 10.dp))
        }
        else{
        Column{
            searchJournals.forEach { searchJournals ->
                JournalCard(
                    journal = searchJournals,
                    onEditClick = {
                        navController.navigate(NavRoutes.editRoute(searchJournals.id))
                        println("卡片被编辑了")
                    },
                    onDeleteClick = { journalViewModel.deleteJournal(searchJournals) },
                )
            }
            }
        }
    }

    else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (journals.isEmpty()) {
                Text(text = "暂无日记", fontFamily = FontFamily.Monospace , fontWeight = Bold)
            } else {
                journals.forEach { journal ->
                    JournalCard(
                        journal = journal,
                        onEditClick = {
                            navController.navigate(NavRoutes.editRoute(journal.id))
                            println("卡片被编辑了")
                        },
                        onDeleteClick = { journalViewModel.deleteJournal(journal) },
                    )
                }
            }
        }
    }
}

@Composable
fun MySearchBar(
    searchWord: String,
    onValueChange: (String) -> Unit,
){
    Text(text = "搜索框：", fontSize = 20.sp, fontFamily = FontFamily.Monospace , fontWeight = Bold,modifier = Modifier.fillMaxWidth()
    )

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(65.dp)
        .padding(vertical = 10.dp)
        .background(Color(0xFFF4F3F3),RoundedCornerShape(20.dp))
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            BasicTextField(
                value = searchWord,
                onValueChange = { onValueChange(it) },
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 18.sp
                ),
                decorationBox = { innerTextField ->
                    // 占位符：仅当标题为空时显示
                    if (searchWord.isEmpty()) {
                        Text(
                            text = "请输入搜索文本：",
                            color = Color.Gray,
                            fontSize = 18.sp
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
fun JournalCard(
    journal: Journal,
    onEditClick:() -> Unit,
    onDeleteClick:() -> Unit,
){
    var showMenu by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp)
        .height(80.dp)
        .background(Color(0xFFF4F3F3),RoundedCornerShape(20.dp))
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    onEditClick()
                }, //单击
                onLongPress = { longPressOffset ->
                    showMenu = true
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress) //震动
                }, //长按
                onDoubleTap = {
                }, //双击
            )
        }
){
        Column(Modifier
            .fillMaxSize()
            .padding(16.dp)
        ){
            Text(
                text = journal.title.ifEmpty { "暂无标题" },
                fontSize = 18.sp,
                fontWeight = Bold,
            )
            Text(text = journal.content.ifEmpty { "暂无内容" },
                style = MaterialTheme.typography.titleLarge ,maxLines = 2,
                fontSize = 16.sp,
            )
        }
    }

    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false },
        shape = RoundedCornerShape(20.dp),
        offset = DpOffset(250.dp, (-50).dp)
    ) {
        DropdownMenuItem(
            text = { Text("编辑") },
            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
            onClick = {
                showMenu = false
                onEditClick()
            }
        )
        DropdownMenuItem(
            text = { Text("删除") },
            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
            onClick = {
                showMenu = false
                onDeleteClick()
            }
        )
    }
    Text(
        text = "创建日期：${journal.createTime}   最近修改日期：${journal.updateTime}",
        style = MaterialTheme.typography.titleLarge, maxLines = 1,
        fontSize = 12.sp,
        modifier = Modifier.fillMaxWidth().padding(start = 10.dp).offset(0.dp, (-10).dp)
    )
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun TopInf(){
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        hasPermission = checkLocationPermission(context)
        if (!hasPermission) {
            // 2. 动态申请权限（需在Activity中执行）
            val activity = context as? androidx.activity.ComponentActivity
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(ACCESS_FINE_LOCATION),
                    1001 // 请求码
                )
            }
        }
    }
    WeatherDisplay()
}

@Composable
fun AddButton(onClick:() ->Unit){
    Box(modifier = Modifier
        .size(46.dp)
        .clip(CircleShape)
        .border(width = 1.dp, color = Color.Black ,CircleShape)
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = { onClick() })
        },
    ){
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.align (Alignment.Center))
    }
}

