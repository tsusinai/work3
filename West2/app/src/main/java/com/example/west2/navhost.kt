package com.example.west2

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.currentBackStackEntryAsState
import com.example.west2.screens.Edit
import com.example.west2.screens.Home
import com.example.west2.viewmodel.EditViewModel


object NavRoutes {   //导航常数
    const val HOME = "home"
    const val EDIT = "edit/{journalId}"

    //页面传参
    fun editRoute(journalId: Int = 0) = "edit/$journalId"
}
data class NavItem(
    val route: String, // 对应哪个路由
    val label: String, // 文字标签（资源ID）
    val icon: ImageVector, // 图标资源
)

val navItems = listOf(
    NavItem(
        route = NavRoutes.HOME,
        label = "",
        icon = Icons.Default.Home
    ),
    NavItem(
        route = NavRoutes.HOME,
        label = "",
        icon = Icons.Default.Edit
    ),
    NavItem(
        route = NavRoutes.HOME,
        label = "",
        icon = Icons.Default.Build
    ),
    NavItem(
        route = NavRoutes.HOME,
        label = "",
        icon = Icons.Default.AccountCircle
    ),
    NavItem(
        route = NavRoutes.HOME,
        label = "",
        icon = Icons.Default.AccountBox
    ),
)

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable  //导航控制
fun MyNavHost(navController: NavHostController,modifier: Modifier) {
    Scaffold(
        topBar = {
            Box(modifier.fillMaxWidth().height(60.dp).offset(0.dp,2.dp).background(Color(0xFF333333))){}

            Box(modifier.fillMaxWidth().height(60.dp).background(Color(0xFFF4F3F3)) )
            {Text(
                "你好:",modifier = modifier.align(Alignment.BottomStart).padding(start = 12.dp, bottom = 5.dp),
                fontSize = 20.sp,
                fontWeight = Bold
                )
            } },

        bottomBar = {
            BottomNavigation(
                navController = navController,
                items = navItems,
            )
        }
    ) { innerPadding -> //innerPadding内部作用域于
        NavHost(
            navController = navController,
            startDestination = NavRoutes.HOME, //主页面
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoutes.HOME) {
                Home(navController)  //目标地址：主界面
            }

            composable(
                route = NavRoutes.EDIT,
                arguments = listOf(
                    navArgument("journalId") {
                        type = NavType.IntType // 参数类型：Int
                        defaultValue = 0       // 默认值：0（新建日记）
                    }
                )
            ) { backStackEntry ->
                val journalId = backStackEntry.arguments?.getInt("journalId") ?: 0

                val editViewModel: EditViewModel = viewModel()
                LaunchedEffect(journalId) {
                    // 同步参数到 ViewModel
                    editViewModel.id = journalId
                }

                // 传递 navController 到 Edit 页
                Edit(navController = navController)
            }
        }

    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigation(
    navController: NavController,
    items: List<NavItem>,
) {
    // 获取当前导航状态
    val navBackStackEntry by navController.currentBackStackEntryAsState() //监听导航控制器的回退栈状态，返回当前显示页面的导航条目
    val currentRoute = navBackStackEntry?.destination?.route //获取当前页面的路由（如 "home" 或 "pas"），用于判断哪个导航项应该被选中
    // 2. 监听 Navigation 路由变化，同步更新本地状态（仅做兜底，核心依赖手动设置）
    // 2. 核心判断：当前路由是否在items的路由列表中
    val isCurrentRouteInList = items.any { it.route == currentRoute }
    if (isCurrentRouteInList){
        NavigationBar(
            modifier = Modifier.height(80.dp).background(Color(0xFFF4F3F3)),
            containerColor = Color.White.copy(alpha = 0.2f),  //NavigationBar：Material3 提供的底部导航容器组件
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                // 用 Box 自定义导航项
                Box(
                    modifier = Modifier
                        .weight(0.1f)
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                        .clickable {
                            // 导航逻辑（与之前保持一致）
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState =  true
                                    }
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            }
                        },
                    //.padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector =item.icon,
                            null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }
}




