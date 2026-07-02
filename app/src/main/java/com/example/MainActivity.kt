package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.ImageView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Game
import com.example.ui.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    private val viewModel: BoostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AppNavigation(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: BoostViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(300))
        },
        label = "ScreenTransition"
    ) { screen ->
        when (screen) {
            AppScreen.Splash -> SplashScreen(viewModel)
            AppScreen.Signup -> SignupScreen(viewModel)
            AppScreen.Dashboard -> DashboardScreen(viewModel)
        }
    }
}

// ================= SPLASH SCREEN =================
@Composable
fun SplashScreen(viewModel: BoostViewModel) {
    val progress by viewModel.splashProgress.collectAsStateWithLifecycle()
    val status by viewModel.splashStatus.collectAsStateWithLifecycle()

    // Pulse animation for the logo
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(CyberSurfaceVariant, CyberBackground),
                    radius = 1200f
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Center Content (hanOs)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        Brush.linearGradient(listOf(CyberPrimary, CyberSecondary)),
                        CircleShape
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                    .shadow(16.dp, CircleShape, clip = false),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "hanOs Core",
                    tint = Color.White,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "hanOs",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp
                ),
                color = CyberPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "NEXT-GEN CONSOLE ENVIRONMENT",
                style = MaterialTheme.typography.labelSmall,
                color = CyberSecondary.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Bottom loading bar showing status updates
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = status,
                style = MaterialTheme.typography.labelSmall,
                color = CyberOnBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // High-fidelity progress indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(CyberSurfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(CyberPrimary, CyberSecondary)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Footer powered by hanZOZ
            Text(
                text = "powered by hanZOZ",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = CyberOnBackground.copy(alpha = 0.4f)
            )
        }
    }
}

// ================= SIGNUP SCREEN =================
@Composable
fun SignupScreen(viewModel: BoostViewModel) {
    var textInput by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.DeveloperMode,
                    contentDescription = "Setup",
                    tint = CyberPrimary,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "CREATE GAMER PROFILE",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Initialize the hanBoost optimization kernel on your device. Enter your alias to start.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = CyberOnBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    label = { Text("Gamertag / Nickname", color = CyberOnBackground.copy(alpha = 0.6f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberOnBackground.copy(alpha = 0.2f),
                        focusedLabelColor = CyberPrimary,
                        unfocusedLabelColor = CyberOnBackground.copy(alpha = 0.6f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_nickname_input")
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            viewModel.performSignup(textInput.trim())
                        }
                    },
                    enabled = textInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberPrimary,
                        disabledContainerColor = CyberOnBackground.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("signup_submit_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "ENGAGE BOOSTER",
                        style = MaterialTheme.typography.titleMedium,
                        color = CyberBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ================= DASHBOARD SCREEN =================
@Composable
fun DashboardScreen(viewModel: BoostViewModel) {
    val nickname by viewModel.nickname.collectAsStateWithLifecycle()
    val plan by viewModel.subscriptionPlan.collectAsStateWithLifecycle()
    val resolution by viewModel.selectedResolution.collectAsStateWithLifecycle()
    val fpsLimit by viewModel.selectedFpsLimit.collectAsStateWithLifecycle()
    val stats by viewModel.liveStats.collectAsStateWithLifecycle()
    val gamesList by viewModel.games.collectAsStateWithLifecycle()
    val boostState by viewModel.boostState.collectAsStateWithLifecycle()

    val pendingPlan by viewModel.pendingPlan.collectAsStateWithLifecycle()
    val pendingTimeLeft by viewModel.pendingTimeLeft.collectAsStateWithLifecycle()
    val pendingName by viewModel.pendingName.collectAsStateWithLifecycle()
    val pendingNumber by viewModel.pendingNumber.collectAsStateWithLifecycle()

    var showAddGameDialog by remember { mutableStateOf(false) }
    var showUpgradeDialog by remember { mutableStateOf(false) }
    var upgradeTargetPlan by remember { mutableStateOf("Pro") }

    var showActivationFormDialog by remember { mutableStateOf(false) }
    var activationFormTargetPlan by remember { mutableStateOf("") }
    var activationFormTargetPrice by remember { mutableStateOf("") }

    // State to handle custom boosting popup overlays
    var activeBoostingGame by remember { mutableStateOf<Game?>(null) }
    var boostFinishedOverlay by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberBackground)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // TOP USER BAR
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SYSTEM ONLINE",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberTertiary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Gamer: $nickname",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }

                    // Active badge representing subscription
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (plan) {
                                    "Ultimate" -> CyberAccentPink.copy(alpha = 0.2f)
                                    "Pro" -> CyberPrimary.copy(alpha = 0.2f)
                                    else -> CyberSurfaceVariant
                                }
                            )
                            .border(
                                1.dp,
                                when (plan) {
                                    "Ultimate" -> CyberAccentPink
                                    "Pro" -> CyberPrimary
                                    else -> CyberOnBackground.copy(alpha = 0.3f)
                                },
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { showUpgradeDialog = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$plan Tier".uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = when (plan) {
                                "Ultimate" -> CyberAccentPink
                                "Pro" -> CyberPrimary
                                else -> CyberOnBackground
                            }
                        )
                    }
                }
            }

            // HUD DIALS (Telemetry Monitor Grid)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = BorderStroke(1.dp, CyberSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "REAL-TIME ENGINE TELEMETRY",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                            color = CyberOnBackground.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TelemetryDial(
                                label = "CPU",
                                value = "${stats.cpuUsage}%",
                                percentage = stats.cpuUsage / 100f,
                                color = if (stats.cpuUsage > 80) CyberAccentPink else CyberPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            TelemetryDial(
                                label = "RAM FREE",
                                value = "${stats.ramFreePercent}%",
                                percentage = stats.ramFreePercent / 100f,
                                color = CyberTertiary,
                                modifier = Modifier.weight(1f)
                            )
                            TelemetryDial(
                                label = "LIMIT",
                                value = "${stats.fps}FPS",
                                percentage = stats.fps / 144f,
                                color = CyberSecondary,
                                modifier = Modifier.weight(1f)
                            )
                            TelemetryDial(
                                label = "TEMP",
                                value = "${stats.temperature}°C",
                                percentage = (stats.temperature.toFloat() / 50f).coerceIn(0f, 1f),
                                color = if (stats.temperature > 38) CyberAccentPink else CyberPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            if (pendingPlan.isNotEmpty()) {
                item {
                    PendingActivationCard(
                        pendingPlan = pendingPlan,
                        timeLeft = pendingTimeLeft,
                        name = pendingName,
                        number = pendingNumber,
                        onInstantBypass = {
                            viewModel.updatePlan(pendingPlan)
                            viewModel.cancelPendingActivation()
                        },
                        onCancel = {
                            viewModel.cancelPendingActivation()
                        }
                    )
                }
            }

            // TUNING PIPELINE (Resolution & FPS Booster settings)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = BorderStroke(1.dp, CyberSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Tuning",
                                tint = CyberPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GRAPHICS ENGINE PIPELINE",
                                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                                color = CyberOnBackground.copy(alpha = 0.7f)
                            )
                        }

                        // Resolution Setting (Min 1448 for Pro tier, 4K for Ultimate, 720P for Free)
                        Text(
                            text = "Resolution Boost Limit",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Minimum 1448p recommended for high-fidelity rendering. Higher settings require premium access.",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberOnBackground.copy(alpha = 0.5f),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("720P", "1488P", "4K").forEach { res ->
                                val unlocked = when (res) {
                                    "720P" -> true
                                    "1488P" -> plan == "Pro" || plan == "Ultimate"
                                    "4K" -> plan == "Ultimate"
                                    else -> false
                                }

                                val selected = res == resolution

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            when {
                                                selected -> CyberPrimary.copy(alpha = 0.15f)
                                                else -> CyberSurfaceVariant
                                            }
                                        )
                                        .border(
                                            1.dp,
                                            when {
                                                selected -> CyberPrimary
                                                !unlocked -> CyberOnBackground.copy(alpha = 0.1f)
                                                else -> CyberOnBackground.copy(alpha = 0.2f)
                                            },
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            if (unlocked) {
                                                viewModel.updateResolution(res)
                                            } else {
                                                upgradeTargetPlan = if (res == "4K") "Ultimate" else "Pro"
                                                showUpgradeDialog = true
                                            }
                                        }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (!unlocked) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Locked",
                                                tint = CyberOnBackground.copy(alpha = 0.4f),
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .padding(end = 4.dp)
                                            )
                                        }
                                        Text(
                                            text = res,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = when {
                                                selected -> CyberPrimary
                                                !unlocked -> CyberOnBackground.copy(alpha = 0.4f)
                                                else -> Color.White
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Target FPS settings
                        Text(
                            text = "Target Framerate Lock",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(60, 90, 120, 144).forEach { targetFps ->
                                val selected = targetFps == fpsLimit
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (selected) CyberSecondary.copy(alpha = 0.15f) else CyberSurfaceVariant)
                                        .border(
                                            1.dp,
                                            if (selected) CyberSecondary else CyberOnBackground.copy(alpha = 0.15f),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { viewModel.updateFpsLimit(targetFps) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${targetFps}Hz",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (selected) CyberSecondary else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ACTIVE PLANS INFO
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = BorderStroke(1.dp, CyberSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Plans",
                                tint = CyberAccentPink,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AVAILABLE KERNEL PLANS",
                                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                                color = CyberOnBackground.copy(alpha = 0.7f)
                            )
                        }

                        // Grid containing subscription cards
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Free Plan
                            PlanRow(
                                title = "Free Tier",
                                price = "$0",
                                benefits = "Resolution max 720P",
                                isActive = plan == "Free",
                                onSelect = {
                                    activationFormTargetPlan = "Free"
                                    activationFormTargetPrice = "$0"
                                    showActivationFormDialog = true
                                }
                            )

                            // Pro Plan
                            PlanRow(
                                title = "Pro Premium",
                                price = "$2/mo",
                                benefits = "Unlocks Resolution 1488P (minimum high fidelity)",
                                isActive = plan == "Pro",
                                onSelect = {
                                    activationFormTargetPlan = "Pro"
                                    activationFormTargetPrice = "$2/mo"
                                    showActivationFormDialog = true
                                }
                            )

                            // Ultimate Plan
                            PlanRow(
                                title = "Ultimate 4K",
                                price = "$5/mo",
                                benefits = "Unlocks 4K Resolution & Priority Cloud Support",
                                isActive = plan == "Ultimate",
                                onSelect = {
                                    activationFormTargetPlan = "Ultimate"
                                    activationFormTargetPrice = "$5/mo"
                                    showActivationFormDialog = true
                                }
                            )
                        }
                    }
                }
            }

            // ADDED GAMES PANEL
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CUSTOM GAMES CATALOG",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Button(
                        onClick = { showAddGameDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Game",
                            tint = CyberBackground,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ADD GAME",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CyberBackground
                        )
                    }
                }
            }

            if (gamesList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No games added yet. Tap 'ADD GAME' above to configure.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = CyberOnBackground.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(gamesList) { game ->
                    GameListItem(
                        game = game,
                        onBoost = {
                            activeBoostingGame = game
                            viewModel.startBoostSequence(game) {
                                boostFinishedOverlay = true
                            }
                        },
                        onDelete = { viewModel.deleteGame(game) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }

        // BOOSTING OVERLAY VIEW (Activated when activeBoostingGame is set)
        activeBoostingGame?.let { game ->
            val activeBoostState by viewModel.boostState.collectAsStateWithLifecycle()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CyberBackground.copy(alpha = 0.98f))
                    .clickable(enabled = false) {} // block clickthrough
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // Header
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(CyberSurface, CircleShape)
                                .border(2.dp, CyberPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            AppIconImage(
                                packageName = game.packageName,
                                defaultEmoji = game.iconEmoji,
                                modifier = Modifier.size(52.dp),
                                emojiSize = 38.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "BOOSTING ENGINE ACTIVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberAccentPink,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )

                        Text(
                            text = game.title,
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                            color = Color.White
                        )
                    }

                    // Progress indicators
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Current step execution log
                        val currentStep = activeBoostState.steps.getOrNull(activeBoostState.currentStepIndex)
                            ?: "Tuning system variables..."

                        Text(
                            text = currentStep,
                            style = MaterialTheme.typography.bodyLarge,
                            color = CyberTertiary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress percentage
                        val progressPercent = (activeBoostState.progress * 100).toInt()
                        Text(
                            text = "$progressPercent%",
                            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 42.sp),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Loading gauge
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyberSurfaceVariant)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(activeBoostState.progress)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(CyberPrimary, CyberSecondary)
                                        )
                                    )
                            )
                        }
                    }

                    // Bottom status logs / Action buttons
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        if (boostFinishedOverlay) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, CyberTertiary.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = CyberSurface)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Success",
                                        tint = CyberTertiary,
                                        modifier = Modifier.size(32.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "OPTIMIZATION COMPLETE",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Resolution", style = MaterialTheme.typography.labelSmall, color = CyberOnBackground.copy(alpha = 0.6f))
                                            Text(activeBoostState.finalResolution, style = MaterialTheme.typography.bodyLarge, color = CyberPrimary, fontWeight = FontWeight.Bold)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Target FPS", style = MaterialTheme.typography.labelSmall, color = CyberOnBackground.copy(alpha = 0.6f))
                                            Text("${activeBoostState.finalFps}Hz", style = MaterialTheme.typography.bodyLarge, color = CyberSecondary, fontWeight = FontWeight.Bold)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Cooled Temp", style = MaterialTheme.typography.labelSmall, color = CyberOnBackground.copy(alpha = 0.6f))
                                            Text("${activeBoostState.finalTemp}°C", style = MaterialTheme.typography.bodyLarge, color = CyberTertiary, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            val context = LocalContext.current
                            Button(
                                onClick = {
                                    viewModel.launchRealApp(game, context)
                                    boostFinishedOverlay = false
                                    activeBoostingGame = null
                                    viewModel.clearBoostState()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberTertiary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "LAUNCH ${game.title.uppercase()}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = CyberBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Text(
                                text = "Applying RAM allocations, tuning CPU frequencies, setting graphics pipeline scaled pixels...",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberOnBackground.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                }
            }
        }

        // ADD GAME DIALOG
        if (showAddGameDialog) {
            AddGameDialog(
                viewModel = viewModel,
                onDismiss = { showAddGameDialog = false }
            )
        }

        // PLAN UPGRADE DIALOG
        if (showUpgradeDialog) {
            UpgradePlanDialog(
                targetPlan = upgradeTargetPlan,
                onDismiss = { showUpgradeDialog = false },
                onUpgrade = { planName ->
                    viewModel.updatePlan(planName)
                    showUpgradeDialog = false
                }
            )
        }

        // PLAN ACTIVATION CREDENTIAL FORM DIALOG
        if (showActivationFormDialog) {
            PlanActivationFormDialog(
                planName = activationFormTargetPlan,
                price = activationFormTargetPrice,
                onDismiss = { showActivationFormDialog = false },
                onActivate = { name, number ->
                    viewModel.requestPlanActivation(activationFormTargetPlan, name, number)
                    showActivationFormDialog = false
                }
            )
        }
    }
}

// Custom subscription plan row
@Composable
fun PlanRow(
    title: String,
    price: String,
    benefits: String,
    isActive: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isActive) CyberPrimary.copy(alpha = 0.08f) else CyberSurfaceVariant)
            .border(
                1.dp,
                if (isActive) CyberPrimary else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isActive) CyberPrimary else Color.White,
                    fontWeight = FontWeight.Bold
                )

                if (isActive) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberPrimary)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ACTIVE",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = CyberBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = benefits,
                style = MaterialTheme.typography.labelSmall,
                color = CyberOnBackground.copy(alpha = 0.6f)
            )
        }

        Text(
            text = price,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isActive) CyberPrimary else CyberTertiary
        )
    }
}

// Telemetry dial showing hardware parameters
@Composable
fun TelemetryDial(
    label: String,
    value: String,
    percentage: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(62.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 5.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2

                // Draw background arc
                drawArc(
                    color = Color.White.copy(alpha = 0.1f),
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = Offset((size.width - radius * 2) / 2, (size.height - radius * 2) / 2),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Draw glowing active arc
                drawArc(
                    color = color,
                    startAngle = 135f,
                    sweepAngle = percentage * 270f,
                    useCenter = false,
                    topLeft = Offset((size.width - radius * 2) / 2, (size.height - radius * 2) / 2),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = CyberOnBackground.copy(alpha = 0.6f)
        )
    }
}

// Custom lists inside dashboard representing games
@Composable
fun GameListItem(
    game: Game,
    onBoost: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CyberSurfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    AppIconImage(
                        packageName = game.packageName,
                        defaultEmoji = game.iconEmoji,
                        modifier = Modifier.size(32.dp),
                        emojiSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${game.category} • Boosts: ${game.boostCount}",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberOnBackground.copy(alpha = 0.5f)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // DELETE button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Game",
                        tint = CyberOnBackground.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // BOOST BUTTON
                Button(
                    onClick = onBoost,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "BOOST",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = CyberBackground
                    )
                }
            }
        }
    }
}

// Add Custom Game Dialog
@Composable
fun AddGameDialog(
    viewModel: BoostViewModel,
    onDismiss: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadInstalledApps()
    }

    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Installed, 1 = Custom Mock

    // States for custom mock game
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Action") }
    var selectedEmoji by remember { mutableStateOf("🕹️") }

    val emojis = listOf("🕹️", "🏎️", "🔫", "👾", "⚽", "⚔️", "🏆", "🧩", "🥊", "🎯")
    val categories = listOf("Action", "RPG", "Racing", "Shooter", "Sports", "Arcade")

    val filteredApps = remember(installedApps, searchQuery) {
        if (searchQuery.isBlank()) {
            installedApps
        } else {
            installedApps.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "ADD GAME TO CONSOLE",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Neon styled tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberSurfaceVariant),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = 0 }
                            .background(if (selectedTab == 0) CyberPrimary.copy(alpha = 0.2f) else Color.Transparent)
                            .border(1.dp, if (selectedTab == 0) CyberPrimary else Color.Transparent, RoundedCornerShape(8.dp))
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "REAL SYSTEM APPS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedTab == 0) CyberPrimary else CyberOnBackground.copy(alpha = 0.6f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = 1 }
                            .background(if (selectedTab == 1) CyberPrimary.copy(alpha = 0.2f) else Color.Transparent)
                            .border(1.dp, if (selectedTab == 1) CyberPrimary else Color.Transparent, RoundedCornerShape(8.dp))
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "CUSTOM GAME",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedTab == 1) CyberPrimary else CyberOnBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        },
        text = {
            Box(modifier = Modifier.height(320.dp)) {
                if (selectedTab == 0) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Search bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search installed apps...", color = CyberOnBackground.copy(alpha = 0.4f)) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = CyberPrimary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyberPrimary,
                                unfocusedBorderColor = CyberOnBackground.copy(alpha = 0.15f)
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        )

                        if (filteredApps.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No matches found",
                                    color = CyberOnBackground.copy(alpha = 0.5f),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(filteredApps) { app ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(CyberSurfaceVariant)
                                            .clickable {
                                                viewModel.addRealGame(app)
                                                onDismiss() // Automatically goes home when selected!
                                            }
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(CyberBackground),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AppIconImage(
                                                packageName = app.packageName,
                                                defaultEmoji = app.iconEmoji,
                                                modifier = Modifier.size(24.dp),
                                                emojiSize = 18.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = app.name,
                                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                                color = Color.White,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = app.packageName,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                color = CyberOnBackground.copy(alpha = 0.4f),
                                                maxLines = 1
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.Default.AddCircle,
                                            contentDescription = "Add",
                                            tint = CyberPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Game Title", color = CyberOnBackground.copy(alpha = 0.6f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyberPrimary
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Column {
                            Text(
                                "Game Category",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberOnBackground.copy(alpha = 0.6f),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                categories.take(4).forEach { cat ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (category == cat) CyberPrimary.copy(alpha = 0.2f) else CyberSurfaceVariant)
                                            .border(1.dp, if (category == cat) CyberPrimary else Color.Transparent, RoundedCornerShape(6.dp))
                                            .clickable { category = cat }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(cat, style = MaterialTheme.typography.labelSmall, color = Color.White)
                                    }
                                }
                            }
                        }

                        Column {
                            Text(
                                "Select Game Visual Glyph",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberOnBackground.copy(alpha = 0.6f),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                emojis.forEach { emoji ->
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (selectedEmoji == emoji) CyberPrimary.copy(alpha = 0.2f) else CyberSurfaceVariant)
                                            .border(1.dp, if (selectedEmoji == emoji) CyberPrimary else Color.Transparent, RoundedCornerShape(6.dp))
                                            .clickable { selectedEmoji = emoji },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(emoji, fontSize = 16.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (selectedTab == 1) {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addCustomGame(title, category, selectedEmoji)
                            onDismiss()
                        }
                    },
                    enabled = title.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
                ) {
                    Text("ADD CUSTOM GAME", color = CyberBackground, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CLOSE", color = CyberOnBackground)
            }
        },
        containerColor = CyberSurface
    )
}

// Upgrade Plan Dialog (Supports Mock Purchases)
@Composable
fun UpgradePlanDialog(
    targetPlan: String,
    onDismiss: () -> Unit,
    onUpgrade: (String) -> Unit
) {
    val context = LocalContext.current
    val targetPrice = if (targetPlan == "Ultimate") "$5" else "$2"
    val targetRes = if (targetPlan == "Ultimate") "4K" else "1488P"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Bolt",
                    tint = CyberPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "UNLOCK PREMIUM PIPELINE",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Upgrade to the $targetPlan plan to access ultra high-fidelity rendering outputs up to $targetRes.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = CyberOnBackground.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant),
                    border = BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$targetPlan Sub",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = CyberPrimary
                            )
                            Text(
                                text = "$targetPrice/month",
                                style = MaterialTheme.typography.titleLarge,
                                color = CyberTertiary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        val benefitsList = if (targetPlan == "Ultimate") {
                            listOf("Resolution target: Ultra 4K", "Engine frequency: 144Hz limit", "Priority cloud optimization server", "No resource throttling")
                        } else {
                            listOf("Resolution target: High-fidelity 1488P", "Engine frequency: 120Hz limit", "Standard mobile hardware tuning")
                        }

                        benefitsList.forEach { benefit ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = CyberTertiary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = benefit,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onUpgrade(targetPlan)
                    Toast.makeText(context, "Upgraded to $targetPlan Tier!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberTertiary)
            ) {
                Text("BUY NOW ($targetPrice)", color = CyberBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("DISMISS", color = CyberOnBackground)
            }
        },
        containerColor = CyberSurface
    )
}

// Custom real package icon loader
@Composable
fun AppIconImage(
    packageName: String?,
    defaultEmoji: String,
    modifier: Modifier = Modifier,
    emojiSize: androidx.compose.ui.unit.TextUnit = 20.sp
) {
    val context = LocalContext.current
    val drawable = remember(packageName) {
        if (!packageName.isNullOrEmpty()) {
            try {
                context.packageManager.getApplicationIcon(packageName)
            } catch (e: java.lang.Exception) {
                null
            }
        } else {
            null
        }
    }

    if (drawable != null) {
        AndroidView(
            factory = { ctx ->
                ImageView(ctx).apply {
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }
            },
            update = { imageView ->
                imageView.setImageDrawable(drawable)
            },
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(text = defaultEmoji, fontSize = emojiSize)
        }
    }
}

// Plan Activation Credential Form Dialog
@Composable
fun PlanActivationFormDialog(
    planName: String,
    price: String,
    onDismiss: () -> Unit,
    onActivate: (name: String, number: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ACTIVATE ${planName.uppercase()} PLAN",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = "Cost: $price",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberTertiary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberSurfaceVariant, RoundedCornerShape(10.dp))
                        .border(1.dp, CyberPrimary.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "PAYMENT INSTRUCTIONS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = CyberPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Please send the payment to the recipient number below:",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberOnBackground.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "+252633718556",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = CyberPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(
                    text = "After sending the payment, enter your registered sender name and phone number below to trigger the secure 5-minute automated validation pipeline.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CyberOnBackground.copy(alpha = 0.8f)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Enter Full Name", color = CyberOnBackground.copy(alpha = 0.6f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberOnBackground.copy(alpha = 0.15f),
                        focusedContainerColor = CyberSurfaceVariant,
                        unfocusedContainerColor = CyberSurfaceVariant
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text("Enter Number", color = CyberOnBackground.copy(alpha = 0.6f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberOnBackground.copy(alpha = 0.15f),
                        focusedContainerColor = CyberSurfaceVariant,
                        unfocusedContainerColor = CyberSurfaceVariant
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && number.isNotBlank()) {
                        onActivate(name, number)
                    }
                },
                enabled = name.isNotBlank() && number.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
            ) {
                Text("SEND", color = CyberBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = CyberOnBackground)
            }
        },
        containerColor = CyberSurface
    )
}

// Pending activation card showing MM:SS remaining time and credentials
@Composable
fun PendingActivationCard(
    pendingPlan: String,
    timeLeft: Long,
    name: String,
    number: String,
    onInstantBypass: () -> Unit,
    onCancel: () -> Unit
) {
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(1.dp, CyberTertiary.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Pending Activation",
                        tint = CyberTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ACTIVATION VERIFICATION",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                        color = CyberTertiary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CyberTertiary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = CyberTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Activating Pipeline: $pendingPlan Tier",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurfaceVariant, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row {
                    Text("Name Registered: ", style = MaterialTheme.typography.labelSmall, color = CyberOnBackground.copy(alpha = 0.5f))
                    Text(name, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                }
                Row {
                    Text("Account/Number: ", style = MaterialTheme.typography.labelSmall, color = CyberOnBackground.copy(alpha = 0.5f))
                    Text(number, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                }
                Row {
                    Text("Gateway Status: ", style = MaterialTheme.typography.labelSmall, color = CyberOnBackground.copy(alpha = 0.5f))
                    Text("P2P VERIFICATION PENDING...", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = CyberTertiary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Verification takes up to 5 minutes to securely clear. Please keep this screen open or return to home shortly.",
                style = MaterialTheme.typography.labelSmall,
                color = CyberOnBackground.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.textButtonColors(contentColor = CyberAccentPink)
                ) {
                    Text("CANCEL REQUEST", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }

                Button(
                    onClick = onInstantBypass,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberTertiary),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("INSTANT BYPASS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = CyberBackground)
                }
            }
        }
    }
}
