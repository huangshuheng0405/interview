package com.example.interview.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.interview.R
import com.example.interview.data.model.SortType
import com.example.interview.ui.components.FilterSheet
import com.example.interview.ui.components.HMClockIn
import com.example.interview.ui.components.HMQuestionItemRow
import com.example.interview.ui.components.HMSearchBox
import com.example.interview.ui.components.HMSkeletonLoader
import com.example.interview.ui.navigation.Routes
import com.example.interview.ui.theme.AppColors
import com.example.interview.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun HomePage(navController: NavController) {
    val viewModel: HomeViewModel = viewModel()
    val scope = rememberCoroutineScope()

    // 首次加载分类数据
    LaunchedEffect(Unit) {
        viewModel.loadQuestionTypes()
    }

    val types by viewModel.questionTypes.collectAsStateWithLifecycle()
    val activeIndex by viewModel.activeIndex.collectAsStateWithLifecycle()
    val list by viewModel.questionList.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    LaunchedEffect(activeIndex, types.size) {
        if (types.isNotEmpty()) {
            viewModel.refreshList()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 头部导航栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
                .background(Color.White)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧扫码图标
            Image(
                painter = painterResource(R.drawable.ic_home_scan),
                contentDescription = "扫码",
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(16.dp))
            // 搜索框
            HMSearchBox(
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(Routes.SEARCH) })
            Spacer(modifier = Modifier.height(16.dp))
            // 打卡
            HMClockIn()
        }

        // 轮播图
        HMBanner(
            images = listOf(
                R.drawable.banner_ai,
                R.drawable.banner_pj,
                R.drawable.banner_qa,
            )
        )

        // 分类 tab + 题目列表
        if (loading && types.isEmpty()) {
            // 骨架屏
            HMSkeletonLoader()
        } else {
            HMHomeCategorySection(
                viewModel = viewModel,
                navController = navController
            )
        }


    }
}

/**
 * 轮播图组件 - 对应鸿蒙 Swiper(banner_ai, banner_pj, banner_qa)
 * 支持无限轮播 + 自动轮播 + 指示器
 *
 * 无限轮播原理: pageCount设为Int.MAX_VALUE, 起始页在中间位置,
 * 通过 page % realCount 映射到真实数据, 实现左右无限滑动
 *
 * @param images 图片资源ID列表
 * @param autoScrollInterval 自动轮播间隔(毫秒), 默认3000ms
 */
@Composable
fun HMBanner(
    images: List<Int>,
    autoScrollInterval: Long = 3000L
) {
    val realCount = images.size
    if (realCount == 0) return

    // 无限轮播: 虚拟页数为Int.MAX_VALUE, 通过取模映射到真实数据
    val virtualCount = Int.MAX_VALUE
    val startIndex = virtualCount / 2  // 从中间开始, 确保左右都能滑动

    // 创建并记住 HorizontalPager 的状态对象，用来控制和监听翻页
    val pagerState = rememberPagerState(
        initialPage = startIndex,      // ① 初始显示第几页
        pageCount = { virtualCount }   // ② 一共有多少页
    )

    // 当前页映射到真实索引
    val realIndex = pagerState.currentPage % realCount

    // 自动轮播 - 只需+1, 因为虚拟页无穷大
    LaunchedEffect(pagerState) {
        while (true) {
            delay(autoScrollInterval)
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            val index = page % realCount  // 虚拟页 → 真实索引
            Image(
                painter = painterResource(images[index]),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .aspectRatio(2.65f),
                contentScale = ContentScale.Crop
            )
        }

        // 指示器叠加在图片右下角
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(end = 28.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(realCount) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (realIndex == index) Color.Red
                            else AppColors.White.copy(alpha = 0.5f)
                        )
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
fun HMHomeCategorySection(
    viewModel: HomeViewModel,
    navController: NavController
) {

    val types by viewModel.questionTypes.collectAsStateWithLifecycle()
    val activeIndex by viewModel.activeIndex.collectAsStateWithLifecycle()
    val list by viewModel.questionList.collectAsStateWithLifecycle()

    var showFilterSheet by remember { mutableStateOf(false) }

    var filterIndex by remember { mutableIntStateOf(0) }
    var filterSort by remember { mutableStateOf(SortType.Default) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // 分类的tab行
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            SecondaryScrollableTabRow(
                activeIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 48.dp),  // 右侧留空间给筛选按钮
                edgePadding = 16.dp,
                divider = { HorizontalDivider(thickness = 0.5.dp, color = AppColors.GrayBorder) },
                indicator = { }  // 禁用默认指示器, 下划线移到Tab内容内部
            ) {
                types.forEachIndexed { index, type ->
                    Tab(
                        modifier = Modifier.height(48.dp),
                        selected = activeIndex == index,
                        onClick = { viewModel.selectType(index) }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = type.name,
                                    fontSize = 15.sp,
                                    color = if (activeIndex == index) AppColors.Black else AppColors.Gray01
                                )

                                // 选中下划线
                                Box(
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .then(
                                            if (activeIndex == index) Modifier
                                                .width(20.dp)
                                                .height(2.dp)
                                                .background(AppColors.Black)
                                            else Modifier.height(2.dp)
                                        )
                                )
                            }

                            // new 标记
                            if (type.displayNewestFlag == 1) {
                                Image(
                                    painter = painterResource(R.drawable.ic_home_new),
                                    contentDescription = "new",
                                    modifier = Modifier
                                        .padding(start = 5.dp)
                                        .width(32.dp)
                                        .height(14.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                }
            }


            // 筛选按钮 - 右侧渐变遮罩 + ic_home_filter
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(48.dp)
                    .clickable { showFilterSheet = true },
                contentAlignment = Alignment.Center
            ) {
                // 渐变背景遮罩 - 让Tab文字过渡消失
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, Color.White),
                                startX = 0f
                            )
                        )
                )
                Image(
                    painter = painterResource(R.drawable.ic_home_filter),
                    contentDescription = "筛选",
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit
                )

                if (showFilterSheet) {
                    FilterSheet(
                        types = types,
                        filterIndex = filterIndex,
                        onIndexChange = { filterIndex = it },
                        filterSort = filterSort,
                        onSortChange = { filterSort = it },
                        onDismiss = { showFilterSheet = false },
                        onConfirm = {
                            showFilterSheet = false
                        })
                }
            }
        }


        var isRefreshing by remember { mutableStateOf(false) }
        val listState = rememberLazyListState()
        val isFinished by viewModel.isFinished.collectAsStateWithLifecycle()
        val isLoadingMore by viewModel.isLoadingMore.collectAsStateWithLifecycle()

        PullToRefreshBox(
            isRefreshing = isLoadingMore,
            onRefresh = {
                isRefreshing = true
                viewModel.refreshList()
                isRefreshing = false
            },
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.GrayBg)
        ) {
            LazyColumn(
                state = listState
            ) {
                items(list) { item ->
                    HMQuestionItemRow(item)
                    HorizontalDivider(
                        color = AppColors.GrayBorder,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }


                // 底部加载状态
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isFinished) {
                            Text("没有更多了~", fontSize = 14.sp, color = AppColors.Gray03)
                        } else if (isLoadingMore) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("正在拼命加载中~", fontSize = 14.sp, color = AppColors.Gray03)
                            }
                        }
                    }
                }

            }

            // 监听滚动到底部 自动触发加载更多
            LaunchedEffect(listState) {
                snapshotFlow {
                    val lastVisibleIndex =
                        listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                    lastVisibleIndex >= listState.layoutInfo.totalItemsCount - 2
                }.collect { isAtEnd ->
                    if (isAtEnd) {
                        viewModel.loadMore()
                    }
                }
            }

        }


    }
}