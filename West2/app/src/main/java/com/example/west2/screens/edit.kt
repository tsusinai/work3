package com.example.west2.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.west2.data.model.Journal
import com.example.west2.tools.OpenImage
import com.example.west2.tools.TimeUtil.getCurrentTimeStr
import com.example.west2.viewmodel.EditViewModel
import com.example.west2.viewmodel.ImageViewModel
import com.example.west2.viewmodel.JournalViewModel


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun Edit(navController: NavController){

    val journalViewModel: JournalViewModel = viewModel()
    val editViewModel: EditViewModel = viewModel()
    val imageViewModel: ImageViewModel = viewModel()
    val time = getCurrentTimeStr()

    LaunchedEffect(editViewModel.id) {
        if (editViewModel.id>0) {
            val journal = editViewModel.getJournal(editViewModel.id)
            journal?.let {
                editViewModel.updateTitle(it.title)
                editViewModel.updateBody(it.content)
                editViewModel.updateEditTime(time)
                editViewModel.createTime(it.createTime)

                imageViewModel.updateId(it.id)
            }
        } else {
            editViewModel.updateTitle("")
            editViewModel.updateBody("")
            editViewModel.updateEditTime(time)
            editViewModel.createTime(time)

            imageViewModel.updateId(0)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { TheTitle(editViewModel) }
            item { Body(editViewModel) }
            item{Spacer(modifier = Modifier.height(400.dp))}
            item{OpenImage(imageViewModel, editViewModel)}
        }


        Box(modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(50.dp)
        ){
            SaveButton(navController, editViewModel, journalViewModel)}
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun SaveButton(
    navController: NavController,
    editViewModel: EditViewModel,
    journalViewModel: JournalViewModel
) {
    Box(modifier = Modifier
        .size(46.dp)
        .clip(CircleShape)
        .border(width = 1.dp, color = Color.Black ,CircleShape)
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    if (editViewModel.id <= 0)
                        journalViewModel.insertJournal(
                            editViewModel.title,
                            editViewModel.body,
                            editViewModel.editTime,
                            editViewModel.createTime,
                            editViewModel.city
                        )
                    else {
                        val updatedJournal = Journal(
                            id = editViewModel.id,
                            title = editViewModel.title,
                            content = editViewModel.body,
                            createTime = editViewModel.createTime,
                            updateTime = editViewModel.editTime,
                            city = editViewModel.city
                        )
                        journalViewModel.updateJournal(updatedJournal)
                    }

                    navController.popBackStack()
                }
            )
        }
    )
    {
        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.align (Alignment.Center))
    }
}

@Composable
fun TheTitle(
    viewModel: EditViewModel,
) {
    Box(modifier = Modifier) {
        Column {
            BasicTextField(
                value = viewModel.title,
                onValueChange = { viewModel.updateTitle(it) },
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 23.sp,
                    fontWeight = Bold
                ),
                decorationBox = { innerTextField ->
                    // 占位符：仅当标题为空时显示
                    if (viewModel.title.isEmpty()) {
                        Text(
                            text = "请输入标题",
                            color = Color.Gray,
                            fontSize = 23.sp,
                            fontWeight = Bold
                        )
                    }
                    innerTextField()
                }
            )
            Text("${viewModel.editTime} ${viewModel.body.count()}字")
        }


    }
}


@Composable
fun Body(
    viewModel: EditViewModel,
){
    Box(modifier=Modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            BasicTextField(
                value = viewModel.body,
                onValueChange = {viewModel.updateBody(it)},
                textStyle = TextStyle(
                    fontSize = 20.sp,),
                decorationBox = { innerTextField ->
                    // 占位符：仅当标题为空时显示
                    if (viewModel.body.isEmpty()) {
                        Text(
                            text = "请输入文本",
                            color = Color.Gray,
                            fontSize = 20.sp
                        )
                    }
                    innerTextField()
                }

            )
        }
    }
}



//val journal by viewModel.getJournalFlow(id)
//    .collectAsState(initial = null)