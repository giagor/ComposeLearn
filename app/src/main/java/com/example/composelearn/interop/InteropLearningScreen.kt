package com.example.composelearn.interop

import android.content.Intent
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

private const val TAG = "InteropLearningScreen"

@Composable
fun InteropLearningScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { InteropHeader() }
        item { AndroidViewLesson() }
        item { ComposeViewLesson() }
        item { ViewModelLesson() }
        item { NavigationLesson() }
        item { NavigationArgumentLesson() }
        item { ThemeLesson() }
        item { ArchitectureLesson() }
    }
}

@Composable
private fun InteropHeader() {
    Surface(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFF7ED),
                            Color(0xFFECFCCB)
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "互操作与工程化",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "这一部分先学 AndroidView：在 Compose 里包一个传统 View，并理解 factory / update 各管什么。",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun AndroidViewLesson() {
    var count by remember { mutableIntStateOf(0) }
    var themeLabel by remember { mutableStateOf("Compose Theme") }

    InteropCard(
        title = "AndroidView",
        summary = "目标是理解：Compose 里嵌一个传统 View 时，factory 负责创建，update 负责把最新状态同步进去。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Button(onClick = { count++ }) {
                Text("count + 1")
            }

            Button(
                onClick = {
                    themeLabel = if (themeLabel == "Compose Theme") {
                        "Legacy TextView Theme"
                    } else {
                        "Compose Theme"
                    }
                }
            ) {
                Text("切换文案")
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("当前 count = $count", fontWeight = FontWeight.Bold)
                    Text("当前 themeLabel = $themeLabel")
                    Text("看 Logcat 过滤 tag：$TAG")
                    Text("重点看 [AndroidView] factory / update")
                }
            }

            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    Log.d(TAG, "[AndroidView] factory create TextView")
                    TextView(context).apply {
                        textSize = 18f
                        setPadding(40, 30, 40, 30)
                    }
                },
                update = { textView ->
                    Log.d(TAG, "[AndroidView] update count=$count, themeLabel=$themeLabel")
                    textView.text = "Legacy View -> count = $count, label = $themeLabel"
                }
            )

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("观察点", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("1. `factory` 负责创建 View 实例，通常不是每次状态变化都重新创建。")
                    Text("2. `update` 会在状态变化后执行，用来把最新 Compose 状态同步给旧 View。")
                    Text("3. 这就是 AndroidView 的核心：保留原有 View，但让 Compose 继续驱动它的显示。")
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("最小例子", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = """
AndroidView(
    factory = { context -> TextView(context) },
    update = { textView ->
        textView.text = "count = $count"
    }
)
                        """.trimIndent()
                    )
                }
            }
        }
    }
}

@Composable
private fun ComposeViewLesson() {
    val context = LocalContext.current

    InteropCard(
        title = "ComposeView",
        summary = "目标是理解：更常见的 ComposeView 用法，不是在 Compose 页面里硬塞它，而是在传统 XML / Activity / Fragment 里挂一块 Compose UI。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Button(
                onClick = {
                    context.startActivity(
                        Intent(context, ComposeViewHostActivity::class.java)
                    )
                }
            ) {
                Text("打开 ComposeView 示例页")
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("更真实的场景：XML 里先放一个 ComposeView。", fontWeight = FontWeight.Bold)
                    Text("再在 Activity / Fragment 里对它调用 setContent { ... }。")
                    Text("点上面的按钮进入示例页。")
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("观察点", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("1. `ComposeView` 本质上是一个传统 View，但它内部承载的是 Compose 内容。")
                    Text("2. 在 View 体系里用 `setContent { ... }`，就能挂上一块 Compose UI。")
                    Text("3. 这和 `AndroidView` 是反方向：不是 Compose 包 View，而是 View 包 Compose。")
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("最小例子", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = """
setContentView(R.layout.activity_compose_view_host)

val composeView = findViewById<ComposeView>(R.id.composeViewHost)
composeView.setContent {
    Text("Hello from ComposeView")
}
                        """.trimIndent()
                    )
                }
            }
        }
    }
}

class CounterViewModel : ViewModel() {
    var count by mutableIntStateOf(0)
        private set

    var label by mutableStateOf("from ViewModel")
        private set

    fun addCount() {
        count++
    }

    fun toggleLabel() {
        label = if (label == "from ViewModel") {
            "updated by ViewModel"
        } else {
            "from ViewModel"
        }
    }
}

@Composable
private fun ViewModelLesson(
    counterViewModel: CounterViewModel = viewModel()
) {
    val count = counterViewModel.count
    val label = counterViewModel.label

    InteropCard(
        title = "ViewModel 配合",
        summary = "目标是理解：页面负责展示状态和分发事件，状态本身可以放进 ViewModel，而不是全塞在 Composable 里。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Button(onClick = counterViewModel::addCount) {
                Text("count + 1")
            }

            Button(onClick = counterViewModel::toggleLabel) {
                Text("切换文案")
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("当前 count = $count", fontWeight = FontWeight.Bold)
                    Text("当前 label = $label")
                    Text("这两个状态都来自 ViewModel。")
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("观察点", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("1. Composable 不直接持有业务状态，只读取 ViewModel 暴露出来的数据。")
                    Text("2. 按钮点击时，不是直接改本地 state，而是调用 ViewModel 方法。")
                    Text("3. 页面职责会更清楚：UI 负责展示，ViewModel 负责状态。")
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("最小例子", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = """
class CounterViewModel : ViewModel() {
    var count by mutableIntStateOf(0)
        private set

    fun addCount() {
        count++
    }
}

@Composable
fun CounterScreen(
    viewModel: CounterViewModel = viewModel()
) {
    Text("count = ${'$'}{viewModel.count}")
    Button(onClick = viewModel::addCount) {
        Text("count + 1")
    }
}
                        """.trimIndent()
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationLesson() {
    InteropCard(
        title = "导航",
        summary = "目标是理解：Compose 导航最小可用结构通常是 NavController + NavHost + composable(route)。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("下面是一个最小两页导航示例。", fontWeight = FontWeight.Bold)
                    Text("首页点击按钮跳到详情页，详情页点击按钮返回。")
                }
            }

            NavigationSample()

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("观察点", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("1. `rememberNavController()`：拿到导航控制器。")
                    Text("2. `NavHost(...)`：声明导航图和起始页面。")
                    Text("3. `composable(\"route\")`：声明每个路由对应的页面。")
                    Text("4. `navController.navigate(...)` 跳转，`popBackStack()` 返回。")
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("最小例子", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = """
val navController = rememberNavController()

NavHost(
    navController = navController,
    startDestination = "home"
) {
    composable("home") { HomeScreen(...) }
    composable("detail") { DetailScreen(...) }
}
                        """.trimIndent()
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationSample() {
    val navController = rememberNavController()

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(14.dp)
        ) {
            composable("home") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("HomeScreen", fontWeight = FontWeight.Bold)
                    Text("当前页面：home")
                    Button(onClick = { navController.navigate("detail") }) {
                        Text("跳到 detail")
                    }
                }
            }

            composable("detail") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("DetailScreen", fontWeight = FontWeight.Bold)
                    Text("当前页面：detail")
                    Button(onClick = { navController.popBackStack() }) {
                        Text("返回")
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationArgumentLesson() {
    InteropCard(
        title = "导航：带参数跳转",
        summary = "目标是理解：导航不只是切页面，也可以把简单参数一起带过去。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("下面示例会把 title 从 home 带到 detail。", fontWeight = FontWeight.Bold)
                    Text("重点看 route 拼接、navArgument 和 backStackEntry.arguments。")
                }
            }

            NavigationArgumentSample()

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("观察点", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("1. route 可以带占位符，比如 `detail/{title}`。")
                    Text("2. `navigate(\"detail/Compose\")` 是把真实参数拼进 route。")
                    Text("3. 详情页里再从 `backStackEntry.arguments` 读出来。")
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("最小例子", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = """
composable(
    route = "detail/{title}",
    arguments = listOf(
        navArgument("title") { type = NavType.StringType }
    )
) { backStackEntry ->
    val title = backStackEntry.arguments?.getString("title").orEmpty()
}
                        """.trimIndent()
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationArgumentSample() {
    val navController = rememberNavController()

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(14.dp)
        ) {
            composable("home") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("HomeScreen", fontWeight = FontWeight.Bold)
                    Button(onClick = { navController.navigate("detail/Compose") }) {
                        Text("跳到 detail/Compose")
                    }
                }
            }

            composable(
                route = "detail/{title}",
                arguments = listOf(
                    navArgument("title") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title").orEmpty()

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("DetailScreen", fontWeight = FontWeight.Bold)
                    Text("接收到的 title = $title")
                    Button(onClick = { navController.popBackStack() }) {
                        Text("返回")
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeLesson() {
    var useLocalTheme by remember { mutableStateOf(false) }

    InteropCard(
        title = "主题",
        summary = "目标是理解：主题不是某个页面随手写颜色，而是全局的设计令牌入口；页面里优先读 MaterialTheme。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Button(onClick = { useLocalTheme = !useLocalTheme }) {
                Text(if (useLocalTheme) "切回全局主题" else "切到局部覆盖主题")
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("当前模式 = ${if (useLocalTheme) "局部覆盖主题" else "全局主题"}", fontWeight = FontWeight.Bold)
                    Text("重点看同一个卡片，在不同主题下颜色和字体怎么跟着变。")
                }
            }

            ThemeTokenCard(title = "全局主题下的卡片")

            if (useLocalTheme) {
                MaterialTheme(
                    colorScheme = lightColorScheme(
                        primary = Color(0xFF0F766E),
                        onPrimary = Color.White,
                        surface = Color(0xFFF0FDFA),
                        onSurface = Color(0xFF134E4A),
                        surfaceVariant = Color(0xFFCCFBF1),
                        onSurfaceVariant = Color(0xFF115E59)
                    )
                ) {
                    ThemeTokenCard(title = "局部覆盖主题下的卡片")
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("观察点", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("1. `ComposeLearnTheme` 是全局主题入口，通常包在 App 顶层。")
                    Text("2. 页面里优先读 `MaterialTheme.colorScheme` 和 `MaterialTheme.typography`。")
                    Text("3. 如果某一小块 UI 需要不同风格，也可以局部再包一层 `MaterialTheme`。")
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("最小例子", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = """
ComposeLearnTheme {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme,
        typography = MaterialTheme.typography
    ) {
        Text(
            text = "Hello Theme",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
                        """.trimIndent()
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeTokenCard(title: String) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "这里没有手写具体颜色，而是直接读 MaterialTheme token。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class ProfileUiState(
    val name: String = "Compose Learner",
    val level: String = "Beginner",
    val bio: String = "状态来自 ViewModel，数据来自 Repository。"
)

private class FakeProfileRepository {
    fun loadProfile(): ProfileUiState {
        return ProfileUiState(
            name = "Compose Learner",
            level = "Intermediate",
            bio = "这是一个分层架构示例：UI 不直接拼数据来源。"
        )
    }
}

class ProfileViewModel : ViewModel() {
    private val repository = FakeProfileRepository()

    var uiState by mutableStateOf(ProfileUiState())
        private set

    fun loadProfile() {
        uiState = repository.loadProfile()
    }
}

@Composable
private fun ArchitectureLesson(
    profileViewModel: ProfileViewModel = viewModel()
) {
    val uiState = profileViewModel.uiState

    InteropCard(
        title = "分层架构",
        summary = "目标是理解：页面负责展示，ViewModel 负责组织状态，Repository 负责提供数据。"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Button(onClick = profileViewModel::loadProfile) {
                Text("加载资料")
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("UI 层只读 uiState。", fontWeight = FontWeight.Bold)
                    Text("按钮点击 -> ViewModel.loadProfile() -> Repository.loadProfile()")
                }
            }

            ProfileCard(uiState = uiState)

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("观察点", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("1. 页面不直接 new 数据，也不直接写取数逻辑。")
                    Text("2. ViewModel 把 Repository 返回的数据整理成 uiState。")
                    Text("3. UI 只关心当前 uiState 长什么样。")
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("最小例子", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = """
class ProfileViewModel : ViewModel() {
    private val repository = ProfileRepository()

    var uiState by mutableStateOf(ProfileUiState())
        private set

    fun loadProfile() {
        uiState = repository.loadProfile()
    }
}

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    ProfileCard(uiState = viewModel.uiState)
}
                        """.trimIndent()
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(uiState: ProfileUiState) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(uiState.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("level = ${uiState.level}")
            Text(uiState.bio, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun InteropCard(
    title: String,
    summary: String,
    content: @Composable () -> Unit
) {
    Log.d(TAG, "[Interop] InteropCard start, title=$title")
    SideEffect {
        Log.d(TAG, "[Interop] InteropCard recomposed, title=$title")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            HorizontalDivider()
            content()
        }
    }
}
