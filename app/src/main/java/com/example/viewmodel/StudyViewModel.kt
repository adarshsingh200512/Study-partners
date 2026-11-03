package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.ChatMessage
import com.example.data.ai.GeminiStudyService
import com.example.data.model.*
import com.example.data.repository.StudyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class AppScreen {
    HOME,
    CALENDAR,
    SUBJECTS,
    ANALYTICS,
    MORE,
    STUDY_TIMER,
    FOCUS_DISTRACTION_SHIELD,
    SMART_PLANNER,
    REVISIONS,
    EXAMS,
    DAILY_MISSIONS,
    TASKS,
    NOTES,
    MATERIALS,
    QUESTIONS_QUIZ,
    FORMULAS,
    MOCK_TESTS,
    FLASHCARDS_OCR,
    AI_COACH,
    GAMIFICATION,
    PEER_ROOMS,
    BACKUP_SETTINGS
}

enum class HomeTab {
    OVERVIEW,
    CALENDAR,
    EVENTS,
    DAILY_DETAILS,
    ANALYTICS
}

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    val repository = StudyRepository(application)
    private val aiService = GeminiStudyService()

    // Navigation state
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _homeTab = MutableStateFlow(HomeTab.OVERVIEW)
    val homeTab: StateFlow<HomeTab> = _homeTab.asStateFlow()

    private val _previousScreen = MutableStateFlow<AppScreen?>(null)

    // Timer state
    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _isTimerPaused = MutableStateFlow(false)
    val isTimerPaused: StateFlow<Boolean> = _isTimerPaused.asStateFlow()

    private val _timerSecondsLeft = MutableStateFlow(25 * 60)
    val timerSecondsLeft: StateFlow<Int> = _timerSecondsLeft.asStateFlow()

    private val _initialTimerSeconds = MutableStateFlow(25 * 60)
    val initialTimerSeconds: StateFlow<Int> = _initialTimerSeconds.asStateFlow()

    private val _selectedMode = MutableStateFlow(StudyMode.POMODORO)
    val selectedMode: StateFlow<StudyMode> = _selectedMode.asStateFlow()

    private val _activeSubjectId = MutableStateFlow("s_math")
    val activeSubjectId: StateFlow<String> = _activeSubjectId.asStateFlow()

    private val _activeTopicId = MutableStateFlow("t_tw")
    val activeTopicId: StateFlow<String> = _activeTopicId.asStateFlow()

    private val _activeVideoId = MutableStateFlow("")
    val activeVideoId: StateFlow<String> = _activeVideoId.asStateFlow()

    private val _isMiniTimerVisible = MutableStateFlow(false)
    val isMiniTimerVisible: StateFlow<Boolean> = _isMiniTimerVisible.asStateFlow()

    private val _showPostSessionDialog = MutableStateFlow(false)
    val showPostSessionDialog: StateFlow<Boolean> = _showPostSessionDialog.asStateFlow()

    private var timerJob: Job? = null
    private var sessionStartTime: String = ""

    // Quick Add Modal & Dialogs
    private val _showQuickAddDialog = MutableStateFlow(false)
    val showQuickAddDialog: StateFlow<Boolean> = _showQuickAddDialog.asStateFlow()

    private val _showAlertsDialog = MutableStateFlow(false)
    val showAlertsDialog: StateFlow<Boolean> = _showAlertsDialog.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    // AI Coach Chat
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "init_1",
                sender = "coach",
                text = "👋 Hello! I am your Study Tracker AI Coach. I analyze your syllabus, study habits, exams, and weak topics to help you achieve your goals. What would you like to plan today?",
                timestamp = "Just now"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _highThinkingMode = MutableStateFlow(true)
    val highThinkingMode: StateFlow<Boolean> = _highThinkingMode.asStateFlow()

    // Interactive Quiz State
    private val _currentQuizIndex = MutableStateFlow(0)
    val currentQuizIndex: StateFlow<Int> = _currentQuizIndex.asStateFlow()

    private val _selectedQuizOption = MutableStateFlow<Int?>(null)
    val selectedQuizOption: StateFlow<Int?> = _selectedQuizOption.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    // Navigation controls
    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            _previousScreen.value = _currentScreen.value
            _currentScreen.value = screen
        }
        // If moving away from Timer while running, show floating mini timer
        _isMiniTimerVisible.value = (_isTimerRunning.value && screen != AppScreen.STUDY_TIMER && screen != AppScreen.FOCUS_DISTRACTION_SHIELD)
    }

    fun navigateBack() {
        val prev = _previousScreen.value ?: AppScreen.HOME
        _currentScreen.value = prev
        _previousScreen.value = null
        _isMiniTimerVisible.value = (_isTimerRunning.value && prev != AppScreen.STUDY_TIMER && prev != AppScreen.FOCUS_DISTRACTION_SHIELD)
    }

    fun setHomeTab(tab: HomeTab) {
        _homeTab.value = tab
    }

    fun toggleQuickAdd(show: Boolean) {
        _showQuickAddDialog.value = show
    }

    fun toggleAlerts(show: Boolean) {
        _showAlertsDialog.value = show
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchActive(active: Boolean) {
        _isSearchActive.value = active
    }

    // Timer actions
    fun setTimerDurationMinutes(minutes: Int) {
        val secs = minutes * 60
        _initialTimerSeconds.value = secs
        _timerSecondsLeft.value = secs
    }

    fun setTimerPreset(mode: StudyMode) {
        _selectedMode.value = mode
        setTimerDurationMinutes(mode.workMinutes)
    }

    fun setActiveSelection(subjectId: String, topicId: String = "", videoId: String = "") {
        _activeSubjectId.value = subjectId
        _activeTopicId.value = topicId
        _activeVideoId.value = videoId
    }

    fun startTimer() {
        if (_isTimerRunning.value && _isTimerPaused.value) {
            _isTimerPaused.value = false
            return
        }
        _isTimerRunning.value = true
        _isTimerPaused.value = false
        sessionStartTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerSecondsLeft.value > 0) {
                delay(1000)
                if (!_isTimerPaused.value) {
                    _timerSecondsLeft.value = _timerSecondsLeft.value - 1
                }
            }
            // Timer finished
            _isTimerRunning.value = false
            _isTimerPaused.value = false
            _showPostSessionDialog.value = true
        }
    }

    fun pauseTimer() {
        _isTimerPaused.value = true
    }

    fun resumeTimer() {
        _isTimerPaused.value = false
    }

    fun stopTimer() {
        timerJob?.cancel()
        _isTimerRunning.value = false
        _isTimerPaused.value = false
        _isMiniTimerVisible.value = false
        _showPostSessionDialog.value = true
    }

    fun resetTimer() {
        timerJob?.cancel()
        _isTimerRunning.value = false
        _isTimerPaused.value = false
        _timerSecondsLeft.value = _initialTimerSeconds.value
        _isMiniTimerVisible.value = false
    }

    fun completeSession(focusRating: Int, sessionNotes: String, markVideoDone: Boolean) {
        val totalSecs = _initialTimerSeconds.value - _timerSecondsLeft.value
        val actualMinutes = maxOf(1, totalSecs / 60)
        val endTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

        repository.saveStudySession(
            subjectId = _activeSubjectId.value,
            topicId = _activeTopicId.value,
            videoId = _activeVideoId.value,
            startTime = sessionStartTime.ifEmpty { "07:00 PM" },
            endTime = endTime,
            durationMinutes = actualMinutes,
            mode = _selectedMode.value,
            focusRating = focusRating,
            notes = sessionNotes,
            markVideoCompleted = markVideoDone
        )

        _showPostSessionDialog.value = false
        resetTimer()
    }

    fun dismissPostSession() {
        _showPostSessionDialog.value = false
        resetTimer()
    }

    // AI Coach Chat
    fun toggleHighThinking(enabled: Boolean) {
        _highThinkingMode.value = enabled
    }

    fun sendAiPrompt(prompt: String) {
        if (prompt.isBlank()) return
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = "user",
            text = prompt.trim(),
            timestamp = "Just now"
        )
        _chatMessages.value = _chatMessages.value + userMsg
        _isAiThinking.value = true

        viewModelScope.launch {
            val response = aiService.sendCoachMessage(
                history = _chatMessages.value,
                userMessage = prompt.trim(),
                enableHighThinking = _highThinkingMode.value,
                profile = repository.userProfile.value,
                subjects = repository.subjects.value,
                topics = repository.topics.value,
                revisions = repository.revisions.value,
                exams = repository.exams.value,
                sessions = repository.sessions.value
            )
            _chatMessages.value = _chatMessages.value + response
            _isAiThinking.value = false
        }
    }

    fun handleCoachAction(action: String) {
        when {
            action.contains("Planner", ignoreCase = true) -> navigateTo(AppScreen.SMART_PLANNER)
            action.contains("Timer", ignoreCase = true) -> navigateTo(AppScreen.STUDY_TIMER)
            action.contains("Flashcard", ignoreCase = true) -> navigateTo(AppScreen.FLASHCARDS_OCR)
            action.contains("Revision", ignoreCase = true) -> navigateTo(AppScreen.REVISIONS)
        }
    }

    // Quiz Actions
    fun answerQuizQuestion(selectedIdx: Int, isCorrect: Boolean) {
        _selectedQuizOption.value = selectedIdx
        if (isCorrect) {
            _quizScore.value = _quizScore.value + 1
        }
    }

    fun nextQuizQuestion() {
        _selectedQuizOption.value = null
        _currentQuizIndex.value = _currentQuizIndex.value + 1
    }

    fun resetQuiz() {
        _selectedQuizOption.value = null
        _currentQuizIndex.value = 0
        _quizScore.value = 0
    }
}
