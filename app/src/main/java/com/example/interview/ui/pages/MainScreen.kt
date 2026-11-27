package com.example.interview.ui.pages

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.interview.ui.navigation.Routes

data class TabItem(
    val name: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)


@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val tabs = listOf(
        TabItem("首页", Icons.Filled.Home, Icons.Outlined.Home, Routes.HOME),
        TabItem("项目", Icons.Filled.Work, Icons.Outlined.Work, Routes.PROJECT),
        TabItem("面经", Icons.Filled.Work, Icons.Outlined.Work, Routes.INTERVIEW),
        TabItem("我的", Icons.Filled.Person, Icons.Outlined.Person, Routes.MINE),
    )

    // 获取用户点击了哪个Tab
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // 判断是否在主页Tab上
    val isMainTab = tabs.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (isMainTab) {
                NavigationBar {
                    tabs.forEach { tab ->
                        // hierarchy 当前页面的层级链（自己 + 所有祖先页面）
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (currentDestination?.hierarchy?.any { it.route == tab.route } == true) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.name
                                )
                            },
                            label = {
                                Text(tab.name)
                            },
                            selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                            onClick = {
                                navController.navigate(tab.route) {
                                    // 1. 离开时保存页面状态
                                    // 把栈中所有页面弹出，直到 NavGraph 的起始页面
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // 2. 不要重复创建页面
                                    launchSingleTop = true
                                    // 3. 回来时恢复之前的状态
                                    restoreState = true
                                }
                            }

                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable(Routes.HOME) { HomePage(navController) }
            composable(Routes.PROJECT) { ProjectPage() }
            composable(Routes.INTERVIEW) { InterviewPage() }
            composable(Routes.MINE) { MinePage() }
            composable(Routes.SEARCH) { SearchPage(navController) }
        }
    }


}