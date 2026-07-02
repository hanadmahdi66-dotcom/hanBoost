package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Game
import com.example.data.GameRepository
import com.example.data.UserManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import kotlin.random.Random

enum class AppScreen {
    Splash,
    Signup,
    Dashboard
}

data class InstalledApp(
    val name: String,
    val packageName: String,
    val iconEmoji: String
)

data class SystemStats(
    val cpuUsage: Int,
    val ramFreePercent: Int,
    val ramUsedGbs: Double,
    val totalRamGbs: Double = 12.0,
    val temperature: Double,
    val fps: Int,
    val connectionStatus: String = "Optimized"
)

data class BoostState(
    val isBoosting: Boolean = false,
    val activeGame: Game? = null,
    val currentStepIndex: Int = 0,
    val steps: List<String> = emptyList(),
    val progress: Float = 0f,
    val finalFps: Int = 60,
    val finalResolution: String = "720P",
    val finalTemp: Double = 36.5
)

class BoostViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = GameRepository(database.gameDao())
    val userManager = UserManager(application)

    // Screen navigation
    private val _currentScreen = MutableStateFlow(AppScreen.Splash)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Splash progress state
    private val _splashProgress = MutableStateFlow(0f)
    val splashProgress: StateFlow<Float> = _splashProgress.asStateFlow()

    private val _splashStatus = MutableStateFlow("Initializing hanOs core...")
    val splashStatus: StateFlow<String> = _splashStatus.asStateFlow()

    // Reactive games list
    val games: StateFlow<List<Game>> = repository.allGames.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // User configurations
    private val _nickname = MutableStateFlow(userManager.nickname)
    val nickname: StateFlow<String> = _nickname.asStateFlow()

    private val _subscriptionPlan = MutableStateFlow(userManager.subscriptionPlan)
    val subscriptionPlan: StateFlow<String> = _subscriptionPlan.asStateFlow()

    private val _selectedResolution = MutableStateFlow(userManager.selectedResolution)
    val selectedResolution: StateFlow<String> = _selectedResolution.asStateFlow()

    private val _selectedFpsLimit = MutableStateFlow(userManager.selectedFpsLimit)
    val selectedFpsLimit: StateFlow<Int> = _selectedFpsLimit.asStateFlow()

    // Plan Activation states
    private val _pendingPlan = MutableStateFlow(userManager.pendingPlanName)
    val pendingPlan: StateFlow<String> = _pendingPlan.asStateFlow()

    private val _pendingTimeLeft = MutableStateFlow(0L)
    val pendingTimeLeft: StateFlow<Long> = _pendingTimeLeft.asStateFlow()

    private val _pendingName = MutableStateFlow(userManager.pendingPlanNameInput)
    val pendingName: StateFlow<String> = _pendingName.asStateFlow()

    private val _pendingNumber = MutableStateFlow(userManager.pendingPlanNumberInput)
    val pendingNumber: StateFlow<String> = _pendingNumber.asStateFlow()

    // Live performance metrics
    private val _liveStats = MutableStateFlow(SystemStats(45, 68, 3.8, 12.0, 37.2, 58))
    val liveStats: StateFlow<SystemStats> = _liveStats.asStateFlow()

    // Active boosting state
    private val _boostState = MutableStateFlow(BoostState())
    val boostState: StateFlow<BoostState> = _boostState.asStateFlow()

    // Jobs
    private var statsJob: Job? = null
    private var splashJob: Job? = null
    private var activationJob: Job? = null

    init {
        // Start splash sequence
        startSplashSequence()

        // Start real-time performance tracking simulation
        startLiveStatsSimulation()

        // Check for any pending plan activations
        startActivationTimer()
    }

    private fun startSplashSequence() {
        splashJob = viewModelScope.launch {
            val statuses = listOf(
                "Initializing hanOs core...",
                "Loading kernel modules...",
                "Scanning game directories...",
                "Tuning CPU governor variables...",
                "Optimizing graphics pipeline...",
                "Engaging RAM clean sweep...",
                "hanOs fully loaded. Starting..."
            )

            val totalDurationMs = 5000L
            val stepTime = totalDurationMs / statuses.size
            
            for (i in statuses.indices) {
                _splashStatus.value = statuses[i]
                val startTime = System.currentTimeMillis()
                while (System.currentTimeMillis() - startTime < stepTime) {
                    val elapsedTime = (System.currentTimeMillis() - startTime).toFloat()
                    val totalElapsedTime = (i * stepTime) + elapsedTime
                    _splashProgress.value = (totalElapsedTime / totalDurationMs).coerceIn(0f, 1f)
                    delay(50)
                }
            }
            _splashProgress.value = 1f
            delay(200)

            // Splash end: Navigate based on signup state
            if (userManager.isSignedUp) {
                _currentScreen.value = AppScreen.Dashboard
            } else {
                _currentScreen.value = AppScreen.Signup
            }
        }
    }

    private fun startLiveStatsSimulation() {
        statsJob = viewModelScope.launch {
            while (true) {
                // If currently boosting, stats fluctuate differently
                val state = _boostState.value
                val isB = state.isBoosting
                
                val currentCpu = if (isB) Random.nextInt(75, 95) else Random.nextInt(32, 54)
                val currentRamFree = if (isB) Random.nextInt(82, 94) else Random.nextInt(52, 70)
                val totalR = 12.0
                val currentRamUsed = totalR * (1.0 - (currentRamFree / 100.0))
                
                val currentTemp = if (isB) {
                    // Under high optimization, temperature is cooled down!
                    (34.2 + Random.nextDouble(-0.3, 0.4))
                } else {
                    (38.5 + Random.nextDouble(-0.5, 0.6))
                }

                // FPS limit is based on user selections
                val targetFps = _selectedFpsLimit.value
                val currentFps = if (isB) {
                    // Maximum high-fidelity FPS
                    Random.nextInt(targetFps - 2, targetFps + 1)
                } else {
                    Random.nextInt(45, 61)
                }

                _liveStats.value = SystemStats(
                    cpuUsage = currentCpu,
                    ramFreePercent = currentRamFree,
                    ramUsedGbs = Math.round(currentRamUsed * 10.0) / 10.0,
                    temperature = Math.round(currentTemp * 10.0) / 10.0,
                    fps = currentFps,
                    connectionStatus = if (isB) "Low Latency (Fast)" else "Normal Network"
                )
                delay(1500)
            }
        }
    }

    // Signup action
    fun performSignup(name: String) {
        viewModelScope.launch {
            userManager.nickname = name
            userManager.isSignedUp = true
            _nickname.value = name
            _currentScreen.value = AppScreen.Dashboard
        }
    }

    // Set plan
    fun updatePlan(plan: String) {
        userManager.subscriptionPlan = plan
        _subscriptionPlan.value = plan
        
        // Auto-adjust resolution to max allowable if user changes plan
        when (plan) {
            "Free" -> {
                userManager.selectedResolution = "720P"
                _selectedResolution.value = "720P"
            }
            "Pro" -> {
                userManager.selectedResolution = "1488P"
                _selectedResolution.value = "1488P"
            }
            "Ultimate" -> {
                userManager.selectedResolution = "4K"
                _selectedResolution.value = "4K"
            }
        }
    }

    // Set resolution (safely checks plan restriction first)
    fun updateResolution(resolution: String): Boolean {
        val plan = _subscriptionPlan.value
        val allowed = when (resolution) {
            "720P" -> true
            "1488P" -> plan == "Pro" || plan == "Ultimate"
            "4K" -> plan == "Ultimate"
            else -> false
        }
        
        if (allowed) {
            userManager.selectedResolution = resolution
            _selectedResolution.value = resolution
            return true
        }
        return false
    }

    // Request Plan Activation with enter name/number form
    fun requestPlanActivation(planName: String, name: String, number: String) {
        userManager.pendingPlanName = planName
        userManager.pendingPlanTimestamp = System.currentTimeMillis()
        userManager.pendingPlanNameInput = name
        userManager.pendingPlanNumberInput = number
        
        _pendingPlan.value = planName
        _pendingName.value = name
        _pendingNumber.value = number
        
        startActivationTimer()
    }

    // Cancel pending activation
    fun cancelPendingActivation() {
        userManager.pendingPlanName = ""
        userManager.pendingPlanTimestamp = 0L
        userManager.pendingPlanNameInput = ""
        userManager.pendingPlanNumberInput = ""
        
        _pendingPlan.value = ""
        _pendingTimeLeft.value = 0L
        _pendingName.value = ""
        _pendingNumber.value = ""
        
        activationJob?.cancel()
    }

    // Timer for plan activation (5 minutes / 300 seconds)
    fun startActivationTimer() {
        activationJob?.cancel()
        activationJob = viewModelScope.launch {
            while (true) {
                val timestamp = userManager.pendingPlanTimestamp
                val name = userManager.pendingPlanName
                if (timestamp == 0L || name.isEmpty()) {
                    _pendingPlan.value = ""
                    _pendingTimeLeft.value = 0L
                    break
                }
                
                val elapsedMs = System.currentTimeMillis() - timestamp
                val totalDurationMs = 5 * 60 * 1000L // 5 minutes
                val remainingMs = totalDurationMs - elapsedMs
                
                if (remainingMs <= 0) {
                    // Unlock plan!
                    updatePlan(name)
                    
                    // Clear pending status
                    userManager.pendingPlanName = ""
                    userManager.pendingPlanTimestamp = 0L
                    userManager.pendingPlanNameInput = ""
                    userManager.pendingPlanNumberInput = ""
                    
                    _pendingPlan.value = ""
                    _pendingTimeLeft.value = 0L
                    break
                } else {
                    _pendingPlan.value = name
                    _pendingTimeLeft.value = remainingMs / 1000L
                    delay(1000L)
                }
            }
        }
    }

    // Set FPS limit
    fun updateFpsLimit(limit: Int) {
        userManager.selectedFpsLimit = limit
        _selectedFpsLimit.value = limit
    }

    // Real system installed apps list flow
    private val _installedApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val installedApps: StateFlow<List<InstalledApp>> = _installedApps.asStateFlow()

    fun loadInstalledApps() {
        viewModelScope.launch {
            try {
                _installedApps.value = getInstalledAppsList()
            } catch (e: Exception) {
                _installedApps.value = emptyList()
            }
        }
    }

    private fun getInstalledAppsList(): List<InstalledApp> {
        val pm = getApplication<Application>().packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        
        // Query launchable applications
        val resolvedInfos = pm.queryIntentActivities(mainIntent, 0)
        val gameEmojis = listOf("🎮", "🕹️", "⚡", "👾", "🎯", "🎲", "⚔️", "🏆", "🏎️", "⚽")
        
        return resolvedInfos.mapIndexed { index, info ->
            val appName = info.loadLabel(pm).toString()
            val pkgName = info.activityInfo.packageName
            
            // Pick an emoji dynamically
            val lowercaseName = appName.lowercase()
            val emoji = when {
                lowercaseName.contains("race") || lowercaseName.contains("car") || lowercaseName.contains("asphalt") || lowercaseName.contains("speed") -> "🏎️"
                lowercaseName.contains("shoot") || lowercaseName.contains("pubg") || lowercaseName.contains("call") || lowercaseName.contains("duty") || lowercaseName.contains("cod") || lowercaseName.contains("gun") -> "⚔️"
                lowercaseName.contains("soccer") || lowercaseName.contains("football") || lowercaseName.contains("fifa") || lowercaseName.contains("pes") || lowercaseName.contains("ball") -> "⚽"
                lowercaseName.contains("puzzle") || lowercaseName.contains("candy") || lowercaseName.contains("crush") || lowercaseName.contains("match") -> "🧩"
                lowercaseName.contains("fight") || lowercaseName.contains("mortal") || lowercaseName.contains("street") -> "🥊"
                lowercaseName.contains("card") || lowercaseName.contains("poker") || lowercaseName.contains("solitaire") -> "🃏"
                lowercaseName.contains("run") || lowercaseName.contains("subway") || lowercaseName.contains("temple") -> "🏃"
                else -> gameEmojis[index % gameEmojis.size]
            }
            
            InstalledApp(name = appName, packageName = pkgName, iconEmoji = emoji)
        }.sortedBy { it.name.lowercase() }
    }

    // Manage Games
    fun addRealGame(app: InstalledApp) {
        viewModelScope.launch {
            val game = Game(
                title = app.name,
                category = "Installed App",
                iconEmoji = app.iconEmoji,
                packageName = app.packageName
            )
            repository.insert(game)
        }
    }

    fun launchRealApp(game: Game, context: Context) {
        val pkgName = game.packageName
        if (!pkgName.isNullOrEmpty()) {
            try {
                val pm = context.packageManager
                val intent = pm.getLaunchIntentForPackage(pkgName)
                if (intent != null) {
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Could not find launcher for ${game.title}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error launching app: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Manage Games
    fun addCustomGame(title: String, category: String, icon: String) {
        viewModelScope.launch {
            val game = Game(title = title, category = category, iconEmoji = icon)
            repository.insert(game)
        }
    }

    fun deleteGame(game: Game) {
        viewModelScope.launch {
            repository.deleteById(game.id)
        }
    }

    // Start Boosting Sequence
    fun startBoostSequence(game: Game, onFinished: () -> Unit) {
        viewModelScope.launch {
            val selectedRes = _selectedResolution.value
            val selectedFps = _selectedFpsLimit.value
            
            val steps = listOf(
                "Detecting active game environment: ${game.title}...",
                "Clearing cached garbage memory...",
                "Force killing 14 background battery-draining services...",
                "Engaging hanOs high-performance thread governor...",
                "Overclocking graphic core render frequencies...",
                "Adjusting screen pixel scaling to $selectedRes (minimum 1448p)...",
                "Locking FPS target to ${selectedFps}Hz...",
                "Cooling down thermal engine parameters (T-3C)...",
                "System fully optimized at $selectedRes resolution!"
            )

            _boostState.value = BoostState(
                isBoosting = true,
                activeGame = game,
                currentStepIndex = 0,
                steps = steps,
                progress = 0f
            )

            val totalDurationMs = 4500L
            val stepTime = totalDurationMs / steps.size

            for (i in steps.indices) {
                _boostState.value = _boostState.value.copy(
                    currentStepIndex = i,
                    progress = (i.toFloat() / steps.size)
                )
                delay(stepTime)
            }

            // Save details to DB
            repository.incrementBoost(game.id, System.currentTimeMillis())

            _boostState.value = _boostState.value.copy(
                progress = 1f,
                finalFps = selectedFps,
                finalResolution = selectedRes,
                finalTemp = 33.8
            )
            
            delay(500)
            onFinished()
        }
    }

    fun clearBoostState() {
        _boostState.value = BoostState(isBoosting = false, activeGame = null)
    }

    override fun onCleared() {
        super.onCleared()
        statsJob?.cancel()
        splashJob?.cancel()
    }
}
