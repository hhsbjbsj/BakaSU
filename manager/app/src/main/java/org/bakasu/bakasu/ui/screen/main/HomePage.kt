package org.bakasu.bakasu.ui.screen.main

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.system.Os
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.expandVertically
import androidx.compose.animation.core.fadeIn
import androidx.compose.animation.core.fadeOut
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.shrinkVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.MenuBook
import androidx.compose.material.icons.twotone.Android
import androidx.compose.material.icons.twotone.Block
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.DeveloperBoard
import androidx.compose.material.icons.twotone.Error
import androidx.compose.material.icons.twotone.Extension
import androidx.compose.material.icons.twotone.FilterList
import androidx.compose.material.icons.twotone.Group
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.Memory
import androidx.compose.material.icons.twotone.PowerSettingsNew
import androidx.compose.material.icons.twotone.Security
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material.icons.twotone.Smartphone
import androidx.compose.material.icons.twotone.Tag
import androidx.compose.material.icons.twotone.TaskAlt
import androidx.compose.material.icons.twotone.Tune
import androidx.compose.material.icons.twotone.VolunteerActivism
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bakasu.bakasu.BuildConfig
import org.bakasu.bakasu.Natives.KernelPatchImplementation
import org.bakasu.bakasu.R
import org.bakasu.bakasu.domain.model.HomeSystemInfo
import org.bakasu.bakasu.domain.model.KernelStatus
import org.bakasu.bakasu.domain.model.ManagerUpdateChannel
import org.bakasu.bakasu.domain.model.ManagerUpdateInfo
import org.bakasu.bakasu.domain.usecase.EnqueueManagerUpdateUseCase
import org.bakasu.bakasu.magica.MagicaService
import org.bakasu.bakasu.ui.component.KsuIsValid
import org.bakasu.bakasu.ui.component.SwipeableSnackbarHost
import org.bakasu.bakasu.ui.component.WarningCard
import org.bakasu.bakasu.ui.component.rememberConfirmDialog
import org.bakasu.bakasu.ui.component.rememberLoadingDialog
import org.bakasu.bakasu.ui.component.settings.SegmentedColumn
import org.bakasu.bakasu.ui.component.settings.SettingsBaseWidget
import org.bakasu.bakasu.ui.navigation.LocalNavigator
import org.bakasu.bakasu.ui.navigation.Route
import org.bakasu.bakasu.ui.screen.LabelText
import org.bakasu.bakasu.ui.theme.CardConfig
import org.bakasu.bakasu.ui.theme.ThemeConfig
import org.bakasu.bakasu.ui.theme.blurEffect
import org.bakasu.bakasu.ui.theme.blurSource
import org.bakasu.bakasu.ui.util.LocalPermissionRequestInterface
import org.bakasu.bakasu.ui.util.LocalSnackbarHost
import org.bakasu.bakasu.ui.util.adaptiveScaffoldWindowInsets
import org.bakasu.bakasu.ui.util.downloader.downloadManagerUpdate
import org.bakasu.bakasu.ui.viewmodel.HomeUiAction
import org.bakasu.bakasu.ui.viewmodel.HomeUiEvent
import org.bakasu.bakasu.ui.viewmodel.HomeUiState
import org.bakasu.bakasu.ui.viewmodel.HomeViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * @author ShirkNeko
 * @date 2025/9/29.
 */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomePage(
    bottomPadding: Dp,
) {
    val context = LocalContext.current
    val viewModel = koinViewModel<HomeViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.dispatch(HomeUiAction.AwaitInitialData)
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeUiEvent.Error -> if (event.message.isNotBlank()) {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)
    val scrollState = rememberScrollState()
    val navigator = LocalNavigator.current
    val loadingDialog = rememberLoadingDialog()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopBar(
                uiState = uiState,
                onReboot = { viewModel.dispatch(HomeUiAction.Reboot(it)) },
                scrollBehavior = scrollBehavior,
            )
        },
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        contentWindowInsets = adaptiveScaffoldWindowInsets(includeBottom = false),
        snackbarHost = {
            SwipeableSnackbarHost(
                modifier = Modifier.padding(bottom = bottomPadding),
                hostState = LocalSnackbarHost.current,
            )
        },
    ) { innerPadding ->
        if (!uiState.isInitialDataLoaded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .blurSource()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .verticalScroll(scrollState)
                    .padding(
                        top = innerPadding.calculateTopPadding() + 2.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                // 1. Tsundere Mascot Speech Card
                var speechDismissed by rememberSaveable { mutableStateOf(false) }
                var quoteIndex by rememberSaveable { mutableIntStateOf(0) }
                val tsundereQuotes = remember {
                    listOf(
                        "哼！就算成功载入了 LKM，也别想让我夸你！(｀へ´)",
                        "笨蛋主人！SuSFS 深度隐藏已经把痕迹抹除干净啦~ (≧◡≦)",
                        "内核空间已经被 Baka 完全接管了，不许乱翻别的模块！",
                        "⑨ 才是最强的！谁敢反作弊检测就通通冻结掉！❄️",
                        "SELinux 隐蔽穿透中，别在外部留下破绽哦！(*/ω＼*)",
                    )
                }

                if (!speechDismissed) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clickable {
                                quoteIndex = (quoteIndex + 1) % tsundereQuotes.size
                            },
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = "🌸", fontSize = 16.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = tsundereQuotes[quoteIndex],
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.weight(1f),
                            )
                            IconButton(
                                onClick = { speechDismissed = true },
                                modifier = Modifier.size(24.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.TwoTone.Close,
                                    contentDescription = "Dismiss",
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                )
                            }
                        }
                    }
                }

                // 2. Concise LKM Status Hero Card
                if (uiState.isCoreDataLoaded) {
                    StatusCard(
                        uiState = uiState,
                        onClickInstall = {
                            if (uiState.systemStatus.isLateLoadMode) return@StatusCard
                            navigator.push(Route.Install(preselectedKernelUri = null))
                        },
                        onClickJailbreak = {
                            loadingDialog.showLoading()
                            context.startService(Intent(context, MagicaService::class.java))
                            scope.launch(Dispatchers.IO) {
                                delay(30_000.milliseconds)
                                withContext(Dispatchers.Main) {
                                    loadingDialog.hide()
                                    Toast.makeText(
                                        context,
                                        R.string.jailbreak_timeout,
                                        Toast.LENGTH_LONG,
                                    ).show()
                                }
                            }
                        },
                        onReboot = { viewModel.dispatch(HomeUiAction.Reboot(it)) },
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. SuSFS Deep Hidden Defense Card
                    if (uiState.systemInfo.susfsEnabled && uiState.systemInfo.susfsVersion.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF10B981).copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text("🛡️", fontSize = 18.sp)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "SuSFS 深度隐藏防御",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = "kstat 挂载欺骗、openat 路径抹除已启用",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF10B981).copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.25f)),
                                ) {
                                    Text(
                                        text = "已就绪",
                                        color = Color(0xFF10B981),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    )
                                }
                            }
                        }
                    }

                    // 4. Critical Warnings (if applicable)
                    if (uiState.systemStatus.ksuVersion != null && !uiState.systemStatus.isRootAvailable) {
                        WarningCard(
                            message = stringResource(id = R.string.grant_root_failed),
                            icon = {
                                Icon(
                                    imageVector = Icons.TwoTone.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(18.dp),
                                )
                            },
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // 5. Manager Update Cards
                ManagerUpdateCard(uiState.stableManagerUpdate)
                ManagerUpdateCard(uiState.betaManagerUpdate)
                if (uiState.isBetaManagerUpdateCheckFailed) {
                    WarningCard(
                        message = stringResource(R.string.beta_update_check_failed),
                        icon = {
                            Icon(
                                imageVector = Icons.TwoTone.Error,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // 6. Rich System & Version Info List
                if (uiState.isExtendedDataLoaded) {
                    InfoCard(
                        systemStatus = uiState.systemStatus,
                        systemInfo = uiState.systemInfo,
                        isSimpleMode = uiState.isSimpleMode,
                        showHomeCardIcons = uiState.showHomeCardIcons,
                    )
                }

                // Redundant management cards (DonateCard, LearnMoreCard) removed for clean concise M3E Home

                Spacer(Modifier.height(bottomPadding))
            }
        }
    }
}

@Composable
private fun ManagerUpdateCard(update: ManagerUpdateInfo?) {
    val visibilityState = remember { MutableTransitionState(false) }
    var displayedUpdate by remember { mutableStateOf<ManagerUpdateInfo?>(null) }

    LaunchedEffect(update) {
        if (update != null) displayedUpdate = update
        visibilityState.targetState = update != null
    }

    AnimatedVisibility(
        visibleState = visibilityState,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        displayedUpdate?.let { updateInfo ->
            ManagerUpdateCardContent(updateInfo)
        }
    }
}

@Composable
private fun ManagerUpdateCardContent(updateInfo: ManagerUpdateInfo) {
    val context = LocalContext.current
    val permissionRequestInterface = LocalPermissionRequestInterface.current
    val enqueueManagerUpdate = koinInject<EnqueueManagerUpdateUseCase>()
    val channelTitle = stringResource(
        if (updateInfo.channel == ManagerUpdateChannel.STABLE) {
            R.string.manager_update_stable
        } else {
            R.string.manager_update_beta
        },
    )
    val message = if (updateInfo.channel == ManagerUpdateChannel.STABLE) {
        stringResource(R.string.new_version_available, updateInfo.versionCode)
    } else {
        stringResource(R.string.beta_version_available, updateInfo.versionCode)
    }
    val updateText = stringResource(R.string.module_update)
    val details = stringResource(
        R.string.manager_update_details,
        updateInfo.versionName,
        updateInfo.versionCode,
        updateInfo.abi,
    )
    val dialogContent = if (updateInfo.changelog.isBlank()) {
        details
    } else {
        "$details\n\n${updateInfo.changelog}"
    }
    val updateDialog = rememberConfirmDialog(
        onConfirm = {
            downloadManagerUpdate(
                context,
                permissionRequestInterface,
                updateInfo,
                enqueueManagerUpdate,
            )
        },
    )

    WarningCard(
        message = "$channelTitle - $message",
        icon = {
            Icon(
                imageVector = Icons.TwoTone.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(18.dp),
            )
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        onClick = {
            updateDialog.showConfirm(
                title = message,
                content = dialogContent,
                markdown = updateInfo.changelog.isNotBlank(),
                confirm = updateText,
            )
        },
    )

    Spacer(modifier = Modifier.height(10.dp))
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RebootDropdownItems(
    items: Map<Int, String>,
    onReboot: (String) -> Unit,
) {
    items.onEachIndexed { index, (id, reason) ->
        DropdownMenuItem(
            shape = MenuDefaults.itemShape(index, items.size).shape,
            text = { Text(stringResource(id)) },
            onClick = { onReboot(reason) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TopBar(
    uiState: HomeUiState,
    onReboot: (String) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val themeConfig: ThemeConfig = koinInject()
    val cardConfig: CardConfig = koinInject()
    val navigator = LocalNavigator.current

    LargeFlexibleTopAppBar(
        modifier = Modifier.blurEffect(),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "⑨",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                    )
                }
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.app_name),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                        )
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                        ) {
                            Text(
                                text = "M3E ⑨",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }
                    Text(
                        text = "(≧◡≦) LKM Hook Active & Working",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor =
                if (themeConfig.isEnableBlur) {
                    Color.Transparent
                } else {
                    MaterialTheme.colorScheme.surfaceContainer.copy(cardConfig.cardAlpha)
                },
            scrolledContainerColor =
                if (themeConfig.isEnableBlur) {
                    Color.Transparent
                } else {
                    MaterialTheme.colorScheme.surfaceContainer.copy(cardConfig.cardAlpha)
                },
        ),
        actions = {
            if (uiState.isCoreDataLoaded) {
                // SuSFS 配置按钮
                if (uiState.systemInfo.susfsVersionSupported) {
                    IconButton(onClick = {
                        navigator.push(Route.SuSFSConfig)
                    }) {
                        Icon(
                            imageVector = Icons.TwoTone.Tune,
                            contentDescription = stringResource(R.string.susfs_config_setting_title),
                        )
                    }
                }

                // 重启按钮
                var showDropdown by remember { mutableStateOf(false) }
                KsuIsValid(uiState.systemStatus) {
                    if (uiState.systemStatus.isRootAvailable) {
                        IconButton(onClick = {
                            showDropdown = true
                        }) {
                            Icon(
                                imageVector = Icons.TwoTone.PowerSettingsNew,
                                contentDescription = stringResource(id = R.string.reboot),
                            )

                            DropdownMenuPopup(expanded = showDropdown, onDismissRequest = {
                                showDropdown = false
                            }) {
                                DropdownMenuGroup(
                                    shapes = MenuDefaults.groupShapes(),
                                ) {
                                    val pm =
                                        LocalContext.current.getSystemService(Context.POWER_SERVICE) as PowerManager?
                                    var methods = mapOf(
                                        R.string.reboot to "",
                                        R.string.reboot_soft to "soft_reboot",
                                        R.string.reboot_recovery to "recovery",
                                        R.string.reboot_bootloader to "bootloader",
                                        R.string.reboot_download to "download",
                                        R.string.reboot_edl to "edl",
                                    )

                                    @Suppress("DEPRECATION")
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && pm?.isRebootingUserspaceSupported == true) {
                                        methods = methods + (R.string.reboot_userspace to "userspace")
                                    }

                                    RebootDropdownItems(methods, onReboot)
                                }
                            }
                        }
                    }
                }
            }
        },
        windowInsets = TopAppBarDefaults.windowInsets.add(WindowInsets(left = 12.dp)),
        scrollBehavior = scrollBehavior,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun StatusCard(
    uiState: HomeUiState,
    onClickInstall: () -> Unit = {},
    onClickJailbreak: () -> Unit = {},
    onReboot: (String) -> Unit = {},
) {
    val systemStatus = uiState.systemStatus
    val onClick = {
        if (systemStatus.isRootAvailable || systemStatus.kernelVersion.isGKI()) {
            onClickInstall()
        }
    }

    when {
        systemStatus.ksuVersion != null -> {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() },
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                ) {
                    // 1. Status Header Row with pulsing dot & SuSFS pill badge
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val pulseAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse,
                        ),
                        label = "pulseAlpha",
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha)),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "LKM 驱动状态 · WORKING",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp,
                        )
                        Spacer(Modifier.weight(1f))
                        if (uiState.systemInfo.susfsEnabled && uiState.systemInfo.susfsVersion.isNotEmpty()) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                ) {
                                    Text(text = "🛡️", fontSize = 11.sp)
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "SuSFS ${uiState.systemInfo.susfsVersion}",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // 2. Main title & description
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (systemStatus.isSafeMode) {
                                stringResource(id = R.string.safe_mode)
                            } else {
                                "已激活 · 完全接管"
                            },
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(text = "✨", fontSize = 20.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "内核级挂载模块 (LKM) 运行正常，系统调用拦截已生效，SELinux 保持隐蔽穿透。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp,
                    )

                    Spacer(Modifier.height(16.dp))

                    // 3. Concise 2x2 Technical Pill Badges
                    val lkmLabel = when {
                        systemStatus.lkmMode == true -> "● LKM (In-Tree)"
                        else -> "● Built-in"
                    }
                    val kernelShort = uiState.systemInfo.kernelRelease.ifBlank { "Linux 5.x" }
                    val selinuxShort = uiState.systemInfo.selinuxStatus.ifBlank { "Enforcing" }
                    val isSelinuxOk = !systemStatus.isSELinuxPermissive && selinuxShort.contains("Enforc", ignoreCase = true)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        MetricChip(
                            modifier = Modifier.weight(1f),
                            label = "挂载机制 / 模式",
                            value = lkmLabel,
                            highlightColor = Color(0xFF10B981),
                        )
                        MetricChip(
                            modifier = Modifier.weight(1f),
                            label = "内核版本",
                            value = kernelShort.take(18),
                            highlightColor = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        MetricChip(
                            modifier = Modifier.weight(1f),
                            label = "SELinux 策略",
                            value = if (isSelinuxOk) "Enforcing (Cloaked)" else selinuxShort,
                            highlightColor = if (isSelinuxOk) Color(0xFF10B981) else MaterialTheme.colorScheme.error,
                        )
                        MetricChip(
                            modifier = Modifier.weight(1f),
                            label = "活跃授权 / 模块",
                            value = "${uiState.systemInfo.superuserCount} 个应用 · ${uiState.systemInfo.moduleCount} 个模块",
                            highlightColor = MaterialTheme.colorScheme.primary,
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(Modifier.height(12.dp))

                    // 4. Expressive Action Buttons Inside Card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Button(
                            onClick = { onClick() },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                        ) {
                            Icon(
                                imageVector = Icons.TwoTone.TaskAlt,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "环境体检",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        var showRebootMenu by remember { mutableStateOf(false) }
                        Box {
                            FilledTonalButton(
                                onClick = { showRebootMenu = true },
                                modifier = Modifier.height(40.dp),
                                shape = CircleShape,
                                contentPadding = PaddingValues(horizontal = 14.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.TwoTone.PowerSettingsNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = stringResource(R.string.reboot),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                            DropdownMenuPopup(
                                expanded = showRebootMenu,
                                onDismissRequest = { showRebootMenu = false },
                            ) {
                                DropdownMenuGroup(
                                    shapes = MenuDefaults.groupShapes(),
                                ) {
                                    val pm = LocalContext.current.getSystemService(Context.POWER_SERVICE) as PowerManager?
                                    val methods = mutableMapOf(
                                        R.string.reboot to "",
                                        R.string.reboot_soft to "soft_reboot",
                                        R.string.reboot_recovery to "recovery",
                                        R.string.reboot_bootloader to "bootloader",
                                        R.string.reboot_download to "download",
                                        R.string.reboot_edl to "edl",
                                    )
                                    @Suppress("DEPRECATION")
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && pm?.isRebootingUserspaceSupported == true) {
                                        methods[R.string.reboot_userspace] = "userspace"
                                    }
                                    RebootDropdownItems(methods, onReboot)
                                }
                            }
                        }
                    }
                }
            }
        }

        systemStatus.kernelVersion.isGKI() -> {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f),
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() },
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "内核尚未接管 · NOT INSTALLED",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.error,
                            letterSpacing = 1.sp,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.home_not_installed),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(text = "⚠️", fontSize = 20.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.home_click_to_install),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onClickInstall,
                            modifier = Modifier.height(40.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError,
                            ),
                        ) {
                            Text("点击安装 BakaSU", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        if (systemStatus.isSELinuxPermissive) {
                            FilledTonalButton(
                                onClick = onClickJailbreak,
                                modifier = Modifier.height(40.dp),
                                shape = CircleShape,
                            ) {
                                Text(stringResource(R.string.home_jailbreak), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        else -> {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.TwoTone.Block, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.home_unsupported),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.home_unsupported_reason),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricChip(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    highlightColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = highlightColor,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun InfoCard(
    systemStatus: KernelStatus,
    systemInfo: HomeSystemInfo,
    isSimpleMode: Boolean,
    showHomeCardIcons: Boolean,
) {
    val managersList = systemInfo.managersList
    val versionTitle = if (systemInfo.managerVersion.first.isNotEmpty()) {
        "⚙️ 系统与内核深度信息 · BakaSU v${systemInfo.managerVersion.first}"
    } else {
        stringResource(R.string.home_version_info)
    }

    SegmentedColumn(
        title = versionTitle,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 4.dp),
    ) {
        item {
            SettingsBaseWidget(
                icon = Icons.TwoTone.Smartphone,
                iconPlaceholder = false,
                title = stringResource(R.string.home_device_model),
                description = systemInfo.deviceModel.ifEmpty { "Android Device" },
            )
        }

        item {
            SettingsBaseWidget(
                icon = Icons.TwoTone.DeveloperBoard,
                iconPlaceholder = false,
                title = stringResource(R.string.home_kernel),
                description = systemInfo.kernelRelease,
            )
        }

        item(
            visible = !isSimpleMode,
        ) {
            SettingsBaseWidget(
                icon = Icons.TwoTone.Android,
                iconPlaceholder = false,
                title = stringResource(R.string.home_android_version),
                description = systemInfo.androidVersion,
            )
        }

        item(
            visible = systemStatus.isManager,
        ) {
            SettingsBaseWidget(
                icon = Icons.TwoTone.Memory,
                iconPlaceholder = false,
                title = stringResource(R.string.home_kernel_version),
                description = systemStatus.ksuFullVersion.orEmpty(),
            )
        }

        item {
            SettingsBaseWidget(
                icon = Icons.TwoTone.Tag,
                iconPlaceholder = false,
                title = stringResource(R.string.home_manager_version),
                description = "${systemInfo.managerVersion.first} (${systemInfo.managerVersion.second}/${systemInfo.managerVersion.third})",
            )
        }

        item(
            visible = !isSimpleMode && systemInfo.susfsEnabled && systemInfo.susfsVersion.isNotEmpty(),
        ) {
            SettingsBaseWidget(
                icon = Icons.TwoTone.Settings,
                iconPlaceholder = false,
                title = stringResource(R.string.home_susfs_version),
                description = "SuSFS ${systemInfo.susfsVersion} (Cloaked)",
            )
        }
    }

    SegmentedColumn(
        title = "🛡️ 安全策略与执行环境",
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 4.dp),
    ) {
        item {
            SettingsBaseWidget(
                icon = Icons.TwoTone.Security,
                iconPlaceholder = false,
                title = stringResource(R.string.home_selinux_status),
                description = systemInfo.selinuxStatus,
            )
        }

        item {
            val seccompDisplay = when (systemInfo.seccompStatus) {
                -1 -> stringResource(R.string.seccomp_status_not_supported)
                0 -> stringResource(R.string.seccomp_status_disabled)
                1 -> stringResource(R.string.seccomp_status_strict)
                2 -> stringResource(R.string.seccomp_status_filter)
                else -> stringResource(R.string.seccomp_status_unknown)
            }

            SettingsBaseWidget(
                icon = Icons.TwoTone.FilterList,
                iconPlaceholder = false,
                title = stringResource(R.string.home_seccomp_status),
                description = seccompDisplay,
            )
        }

        item(
            visible = !isSimpleMode && managersList != null,
        ) {
            val signatureMap =
                managersList?.managers.orEmpty().groupBy { it.signatureIndex }
            val managersText = buildString {
                signatureMap.toSortedMap().forEach { (signatureIndex, managers) ->
                    append(managers.joinToString(", ") { "UID: ${it.uid}" })
                    append(" ")
                    append(
                        when (signatureIndex) {
                            0 -> "(${stringResource(R.string.app_name)})"

                            255 -> "(${stringResource(R.string.dynamic_managerature)})"

                            else -> if (signatureIndex >= 1) {
                                "(${
                                    stringResource(
                                        R.string.signature_index,
                                        signatureIndex,
                                    )
                                })"
                            } else {
                                "(${stringResource(R.string.unknown_signature)})"
                            }
                        },
                    )
                    append(" | ")
                }
            }.trimEnd(' ', '|')

            SettingsBaseWidget(
                icon = Icons.TwoTone.Group,
                iconPlaceholder = false,
                title = stringResource(R.string.multi_manager_list),
                description = managersText.ifEmpty { stringResource(R.string.no_active_manager) },
            )
        }

        item(
            visible = !isSimpleMode && systemStatus.isFullFeatured,
        ) {
            SettingsBaseWidget(
                icon = Icons.TwoTone.Tune,
                iconPlaceholder = false,
                title = stringResource(R.string.home_hook_type),
                description = systemStatus.hookType,
            )
        }

        item(
            visible = !isSimpleMode && systemInfo.zygiskImplement.isNotEmpty() && systemInfo.zygiskImplement != "None",
        ) {
            SettingsBaseWidget(
                icon = Icons.TwoTone.Extension,
                iconPlaceholder = false,
                title = stringResource(R.string.home_zygisk_implement),
                description = systemInfo.zygiskImplement,
            )
        }

        item(
            visible = !isSimpleMode && systemInfo.metaModuleImplement.isNotEmpty() && systemInfo.metaModuleImplement != "None",
        ) {
            SettingsBaseWidget(
                icon = Icons.TwoTone.Extension,
                iconPlaceholder = false,
                title = stringResource(R.string.home_meta_module_implement),
                description = systemInfo.metaModuleImplement,
            )
        }
    }
}
