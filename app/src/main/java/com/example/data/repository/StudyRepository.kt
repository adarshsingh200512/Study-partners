package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class StudyRepository(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("study_tracker_prefs", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _chapters = MutableStateFlow<List<Chapter>>(emptyList())
    val chapters: StateFlow<List<Chapter>> = _chapters.asStateFlow()

    private val _topics = MutableStateFlow<List<Topic>>(emptyList())
    val topics: StateFlow<List<Topic>> = _topics.asStateFlow()

    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos: StateFlow<List<Video>> = _videos.asStateFlow()

    private val _sessions = MutableStateFlow<List<StudySession>>(emptyList())
    val sessions: StateFlow<List<StudySession>> = _sessions.asStateFlow()

    private val _revisions = MutableStateFlow<List<Revision>>(emptyList())
    val revisions: StateFlow<List<Revision>> = _revisions.asStateFlow()

    private val _exams = MutableStateFlow<List<Exam>>(emptyList())
    val exams: StateFlow<List<Exam>> = _exams.asStateFlow()

    private val _missions = MutableStateFlow<List<DailyMission>>(emptyList())
    val missions: StateFlow<List<DailyMission>> = _missions.asStateFlow()

    private val _tasks = MutableStateFlow<List<StudyTask>>(emptyList())
    val tasks: StateFlow<List<StudyTask>> = _tasks.asStateFlow()

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _materials = MutableStateFlow<List<StudyMaterial>>(emptyList())
    val materials: StateFlow<List<StudyMaterial>> = _materials.asStateFlow()

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    private val _formulas = MutableStateFlow<List<Formula>>(emptyList())
    val formulas: StateFlow<List<Formula>> = _formulas.asStateFlow()

    private val _mockTests = MutableStateFlow<List<MockTest>>(emptyList())
    val mockTests: StateFlow<List<MockTest>> = _mockTests.asStateFlow()

    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    val flashcards: StateFlow<List<Flashcard>> = _flashcards.asStateFlow()

    private val _peerRooms = MutableStateFlow<List<PeerRoom>>(emptyList())
    val peerRooms: StateFlow<List<PeerRoom>> = _peerRooms.asStateFlow()

    private val _distractions = MutableStateFlow<List<DistractionLog>>(emptyList())
    val distractions: StateFlow<List<DistractionLog>> = _distractions.asStateFlow()

    private val _blockedApps = MutableStateFlow<List<String>>(
        listOf("Instagram", "YouTube Shorts", "TikTok", "Reddit", "Mobile Games")
    )
    val blockedApps: StateFlow<List<String>> = _blockedApps.asStateFlow()

    // Selected calendar date
    private val _selectedDate = MutableStateFlow("2024-05-08")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    init {
        loadDataOrPopulateDefaults()
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun updateProfile(transform: (UserProfile) -> UserProfile) {
        val updated = transform(_userProfile.value)
        _userProfile.value = updated
        saveProfile()
    }

    // 11 Automatic Actions upon Saving a Study Session
    fun saveStudySession(
        subjectId: String,
        topicId: String,
        videoId: String,
        startTime: String,
        endTime: String,
        durationMinutes: Int,
        mode: StudyMode,
        focusRating: Int,
        notes: String,
        markVideoCompleted: Boolean = false
    ): StudySession {
        val sessionDate = _selectedDate.value
        val newSession = StudySession(
            id = UUID.randomUUID().toString(),
            subjectId = subjectId,
            topicId = topicId,
            videoId = videoId,
            startTime = startTime,
            endTime = endTime,
            durationMinutes = durationMinutes,
            date = sessionDate,
            mode = mode,
            focusRating = focusRating,
            notes = notes
        )

        // 1. Save session log
        val updatedSessions = _sessions.value + newSession
        _sessions.value = updatedSessions

        // 2 & 7. Update XP & Level
        val earnedXp = durationMinutes * 5 + (focusRating * 10)
        val newTotalXp = _userProfile.value.totalXp + earnedXp
        val newLevel = (newTotalXp / 100) + 1
        _userProfile.value = _userProfile.value.copy(
            totalXp = newTotalXp,
            level = newLevel
        )

        // 3. Update Subject actual hours
        val addHours = durationMinutes / 60f
        _subjects.value = _subjects.value.map { subj ->
            if (subj.id == subjectId) {
                subj.copy(targetHours = maxOf(subj.targetHours, subj.targetHours))
            } else subj
        }

        // 4. Update Topic actual hours & status
        if (topicId.isNotEmpty()) {
            _topics.value = _topics.value.map { top ->
                if (top.id == topicId) {
                    val updatedHours = top.completedHours + addHours
                    val newStatus = if (updatedHours >= top.estimatedHours) TopicStatus.COMPLETED else TopicStatus.IN_PROGRESS
                    top.copy(
                        completedHours = updatedHours,
                        status = newStatus,
                        lastStudiedDate = sessionDate,
                        focusRating = (top.focusRating + focusRating) / 2f
                    )
                } else top
            }
        }

        // 8. Update Daily Missions
        _missions.value = _missions.value.map { mission ->
            when {
                mission.title.contains("Study", ignoreCase = true) -> {
                    val updatedVal = mission.currentValue + (durationMinutes / 60)
                    mission.copy(
                        currentValue = updatedVal,
                        isCompleted = updatedVal >= mission.targetValue
                    )
                }
                mission.title.contains("Focus", ignoreCase = true) -> {
                    val updatedVal = mission.currentValue + 1
                    mission.copy(
                        currentValue = updatedVal,
                        isCompleted = updatedVal >= mission.targetValue
                    )
                }
                else -> mission
            }
        }

        // 10. Mark matching planner task completed if linked
        if (topicId.isNotEmpty()) {
            _tasks.value = _tasks.value.map { task ->
                if (task.topicId == topicId && task.status != TaskStatus.COMPLETED) {
                    task.copy(status = TaskStatus.COMPLETED)
                } else task
            }
        }

        // 11. Update video status if linked
        if (videoId.isNotEmpty() && markVideoCompleted) {
            markVideoCompleted(videoId, isCompleted = true)
        }

        saveAll()
        return newSession
    }

    // Video Tracking Actions
    fun markVideoCompleted(videoId: String, isCompleted: Boolean) {
        val nowTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val nowDate = _selectedDate.value
        _videos.value = _videos.value.map { v ->
            if (v.id == videoId) {
                v.copy(
                    isCompleted = isCompleted,
                    watchedDate = if (isCompleted) nowDate else "",
                    watchedTime = if (isCompleted) nowTime else ""
                )
            } else v
        }

        // Award XP for completing video
        if (isCompleted) {
            _userProfile.value = _userProfile.value.copy(
                totalXp = _userProfile.value.totalXp + 50
            )
            // Update Daily Mission for videos
            _missions.value = _missions.value.map { m ->
                if (m.title.contains("video", ignoreCase = true)) {
                    val newVal = m.currentValue + 1
                    m.copy(currentValue = newVal, isCompleted = newVal >= m.targetValue)
                } else m
            }
        }
        saveAll()
    }

    fun addVideo(topicId: String, title: String, durationMinutes: Int, notes: String = "") {
        val newVideo = Video(
            id = UUID.randomUUID().toString(),
            topicId = topicId,
            title = title,
            durationMinutes = durationMinutes,
            notes = notes,
            order = _videos.value.count { it.topicId == topicId } + 1
        )
        _videos.value = _videos.value + newVideo
        saveAll()
    }

    fun deleteVideo(videoId: String) {
        _videos.value = _videos.value.filterNot { it.id == videoId }
        saveAll()
    }

    // Subject & Topic CRUD
    fun addSubject(name: String, colorHex: String, targetHours: Float, priority: Priority, notes: String = "") {
        val newSubject = Subject(
            id = UUID.randomUUID().toString(),
            name = name,
            colorHex = colorHex,
            targetHours = targetHours,
            priority = priority,
            notes = notes
        )
        _subjects.value = _subjects.value + newSubject
        saveAll()
    }

    fun deleteSubject(subjectId: String) {
        _subjects.value = _subjects.value.filterNot { it.id == subjectId }
        _chapters.value = _chapters.value.filterNot { it.subjectId == subjectId }
        _topics.value = _topics.value.filterNot { it.subjectId == subjectId }
        saveAll()
    }

    fun addChapter(subjectId: String, name: String) {
        val chapter = Chapter(
            id = UUID.randomUUID().toString(),
            subjectId = subjectId,
            name = name,
            order = _chapters.value.count { it.subjectId == subjectId } + 1
        )
        _chapters.value = _chapters.value + chapter
        saveAll()
    }

    fun addTopic(subjectId: String, chapterId: String, name: String, estimatedHours: Float, priority: Priority) {
        val topic = Topic(
            id = UUID.randomUUID().toString(),
            chapterId = chapterId,
            subjectId = subjectId,
            name = name,
            estimatedHours = estimatedHours,
            priority = priority,
            status = TopicStatus.NOT_STARTED
        )
        _topics.value = _topics.value + topic
        saveAll()
    }

    fun deleteTopic(topicId: String) {
        _topics.value = _topics.value.filterNot { it.id == topicId }
        _videos.value = _videos.value.filterNot { it.topicId == topicId }
        saveAll()
    }

    // Revision Management
    fun completeRevision(revisionId: String, focusRating: Int = 4, notes: String = "") {
        val nowDate = _selectedDate.value
        var topicIdToScheduleNext = ""
        var subjectIdToScheduleNext = ""
        var currentCycle = 1

        _revisions.value = _revisions.value.map { r ->
            if (r.id == revisionId) {
                topicIdToScheduleNext = r.topicId
                subjectIdToScheduleNext = r.subjectId
                currentCycle = r.revisionNumber
                r.copy(
                    status = RevisionStatus.COMPLETED,
                    completedDate = nowDate,
                    focusRating = focusRating,
                    notes = notes
                )
            } else r
        }

        // Schedule next revision (+3d, +7d, +14d, +30d)
        if (topicIdToScheduleNext.isNotEmpty() && currentCycle < 5) {
            val nextDays = when (currentCycle) {
                1 -> 3
                2 -> 7
                3 -> 14
                else -> 30
            }
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, nextDays)
            val nextDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)

            val nextRevision = Revision(
                id = UUID.randomUUID().toString(),
                topicId = topicIdToScheduleNext,
                subjectId = subjectIdToScheduleNext,
                revisionNumber = currentCycle + 1,
                scheduledDate = nextDateStr,
                status = RevisionStatus.PENDING
            )
            _revisions.value = _revisions.value + nextRevision
        }

        // Award XP and update mission
        _userProfile.value = _userProfile.value.copy(
            totalXp = _userProfile.value.totalXp + 60
        )
        _missions.value = _missions.value.map { m ->
            if (m.title.contains("revision", ignoreCase = true)) {
                val newVal = m.currentValue + 1
                m.copy(currentValue = newVal, isCompleted = newVal >= m.targetValue)
            } else m
        }
        saveAll()
    }

    // Tasks CRUD
    fun addTask(title: String, date: String, priority: Priority, subjectId: String = "", topicId: String = "", estimatedMinutes: Int = 45) {
        val newTask = StudyTask(
            id = UUID.randomUUID().toString(),
            title = title,
            date = date,
            priority = priority,
            subjectId = subjectId,
            topicId = topicId,
            estimatedMinutes = estimatedMinutes
        )
        _tasks.value = _tasks.value + newTask
        saveAll()
    }

    fun toggleTaskStatus(taskId: String) {
        _tasks.value = _tasks.value.map { t ->
            if (t.id == taskId) {
                val newStatus = if (t.status == TaskStatus.COMPLETED) TaskStatus.TODO else TaskStatus.COMPLETED
                if (newStatus == TaskStatus.COMPLETED) {
                    _userProfile.value = _userProfile.value.copy(
                        totalXp = _userProfile.value.totalXp + 30
                    )
                }
                t.copy(status = newStatus)
            } else t
        }
        saveAll()
    }

    fun deleteTask(taskId: String) {
        _tasks.value = _tasks.value.filterNot { it.id == taskId }
        saveAll()
    }

    // Notes CRUD
    fun addNote(title: String, content: String, category: NoteCategory, subjectId: String = "", topicId: String = "", isPinned: Boolean = false) {
        val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        val note = Note(
            id = UUID.randomUUID().toString(),
            title = title,
            content = content,
            category = category,
            subjectId = subjectId,
            topicId = topicId,
            isPinned = isPinned,
            createdAt = dateStr
        )
        _notes.value = listOf(note) + _notes.value
        saveAll()
    }

    fun deleteNote(noteId: String) {
        _notes.value = _notes.value.filterNot { it.id == noteId }
        saveAll()
    }

    // Flashcards CRUD
    fun addFlashcard(subjectId: String, topicId: String, front: String, back: String) {
        val card = Flashcard(
            id = UUID.randomUUID().toString(),
            subjectId = subjectId,
            topicId = topicId,
            front = front,
            back = back
        )
        _flashcards.value = _flashcards.value + card
        saveAll()
    }

    fun updateFlashcardMastery(cardId: String, mastery: FlashcardMastery) {
        _flashcards.value = _flashcards.value.map { c ->
            if (c.id == cardId) c.copy(mastery = mastery) else c
        }
        saveAll()
    }

    // Mock Tests CRUD
    fun addMockTest(testName: String, examId: String, subjectId: String, totalQuestions: Int, correct: Int, incorrect: Int, totalMarks: Float, marksObtained: Float, timeTakenMinutes: Int, notes: String = "") {
        val mock = MockTest(
            id = UUID.randomUUID().toString(),
            testName = testName,
            examId = examId,
            subjectId = subjectId,
            date = _selectedDate.value,
            totalQuestions = totalQuestions,
            correct = correct,
            incorrect = incorrect,
            unattempted = totalQuestions - (correct + incorrect),
            totalMarks = totalMarks,
            marksObtained = marksObtained,
            timeTakenMinutes = timeTakenMinutes,
            notes = notes
        )
        _mockTests.value = _mockTests.value + mock
        saveAll()
    }

    // Distraction Blocker
    fun logDistractionUrge(appName: String, reflection: String) {
        val timestamp = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val log = DistractionLog(
            id = UUID.randomUUID().toString(),
            appName = appName,
            timestamp = timestamp,
            reflection = reflection
        )
        _distractions.value = listOf(log) + _distractions.value
        saveAll()
    }

    fun addBlockedApp(appName: String) {
        if (!_blockedApps.value.contains(appName)) {
            _blockedApps.value = _blockedApps.value + appName
            saveAll()
        }
    }

    fun removeBlockedApp(appName: String) {
        _blockedApps.value = _blockedApps.value - appName
        saveAll()
    }

    // Smart Planner Recovery: redistributes overdue tasks / missed revisions
    fun runSmartRecovery(): Int {
        val todayStr = _selectedDate.value
        var recoveredCount = 0
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrowStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)

        _tasks.value = _tasks.value.map { task ->
            if (task.status != TaskStatus.COMPLETED && task.date < todayStr) {
                recoveredCount++
                task.copy(date = tomorrowStr, priority = Priority.HIGH)
            } else task
        }

        _revisions.value = _revisions.value.map { rev ->
            if (rev.status == RevisionStatus.PENDING && rev.scheduledDate < todayStr) {
                recoveredCount++
                rev.copy(scheduledDate = tomorrowStr)
            } else rev
        }

        saveAll()
        return recoveredCount
    }

    // Reset to demo data
    fun resetToDemoData() {
        populateDefaultData()
        saveAll()
    }

    // Clear all user data
    fun clearAllData() {
        _subjects.value = emptyList()
        _chapters.value = emptyList()
        _topics.value = emptyList()
        _videos.value = emptyList()
        _sessions.value = emptyList()
        _revisions.value = emptyList()
        _exams.value = emptyList()
        _tasks.value = emptyList()
        _notes.value = emptyList()
        _flashcards.value = emptyList()
        _mockTests.value = emptyList()
        saveAll()
    }

    private fun saveProfile() {
        val json = JSONObject().apply {
            put("name", _userProfile.value.name)
            put("title", _userProfile.value.title)
            put("dailyTargetMinutes", _userProfile.value.dailyTargetMinutes)
            put("currentStreak", _userProfile.value.currentStreak)
            put("totalXp", _userProfile.value.totalXp)
            put("level", _userProfile.value.level)
            put("darkTheme", _userProfile.value.darkTheme)
            put("accentColorHex", _userProfile.value.accentColorHex)
            put("aiEnabled", _userProfile.value.aiEnabled)
        }
        prefs.edit().putString("user_profile_json", json.toString()).apply()
    }

    private fun saveAll() {
        saveProfile()
        // Save flag that initialized
        prefs.edit().putBoolean("data_initialized", true).apply()
    }

    private fun loadDataOrPopulateDefaults() {
        val isInitialized = prefs.getBoolean("data_initialized", false)
        if (!isInitialized) {
            populateDefaultData()
            saveAll()
        } else {
            // Load or populate
            populateDefaultData()
        }
    }

    private fun populateDefaultData() {
        _userProfile.value = UserProfile(
            name = "Alex",
            title = "Focused Aspirant",
            dailyTargetMinutes = 360, // 6h 00m
            currentStreak = 12,
            totalXp = 1250,
            level = 12,
            darkTheme = true,
            accentColorHex = "#14B8A6",
            aiEnabled = true
        )

        val sMath = Subject("s_math", "Mathematics", "Calculus, Algebra, Arithmetic", "#3B82F6", "calculate", 120f, Priority.HIGH, "JEE Main", "2024-07-03")
        val sPhys = Subject("s_phys", "Physics", "Mechanics, Electromagnetism, Optics", "#10B981", "bolt", 100f, Priority.HIGH, "JEE Main", "2024-07-03")
        val sChem = Subject("s_chem", "Chemistry", "Organic, Inorganic, Physical Chemistry", "#F59E0B", "science", 90f, Priority.MEDIUM, "JEE Main", "2024-07-03")
        val sBiz = Subject("s_biz", "Business Studies", "Management, Marketing, Finance", "#8B5CF6", "business_center", 40f, Priority.LOW, "Board Exam", "2024-08-15")

        _subjects.value = listOf(sMath, sPhys, sChem, sBiz)

        // Chapters
        val cTimeWork = Chapter("c_tw", sMath.id, "Time & Work", 1)
        val cCalculus = Chapter("c_calc", sMath.id, "Calculus & Derivatives", 2)
        val cKinematics = Chapter("c_kin", sPhys.id, "Kinematics", 1)
        val cOptics = Chapter("c_opt", sPhys.id, "Ray Optics", 2)
        val cBonding = Chapter("c_bond", sChem.id, "Chemical Bonding", 1)
        val cOrganic = Chapter("c_org", sChem.id, "Hydrocarbons", 2)
        val cMgt = Chapter("c_mgt", sBiz.id, "Principles of Management", 1)

        _chapters.value = listOf(cTimeWork, cCalculus, cKinematics, cOptics, cBonding, cOrganic, cMgt)

        // Topics
        val tTW = Topic("t_tw", cTimeWork.id, sMath.id, "Time & Work", "Efficiency, Pipes & Cisterns", 12f, 7.5f, Priority.HIGH, TopicStatus.IN_PROGRESS, "2024-05-15", "Practice efficiency formulas", "2024-05-08", "2024-05-10", 4.2f)
        val tDeriv = Topic("t_der", cCalculus.id, sMath.id, "Derivatives & Chain Rule", "Differential equations basics", 15f, 6.0f, Priority.HIGH, TopicStatus.IN_PROGRESS, "2024-05-20", "", "2024-05-06", "2024-05-12", 4.0f)
        val tKin = Topic("t_kin", cKinematics.id, sPhys.id, "Kinematics 1D & 2D", "Projectile motion, Relative velocity", 14f, 5.5f, Priority.HIGH, TopicStatus.IN_PROGRESS, "2024-05-14", "Review vectors", "2024-05-08", "2024-05-09", 3.8f)
        val tBond = Topic("t_bond", cBonding.id, sChem.id, "Chemical Bonding", "VSEPR theory, Hybridization", 10f, 3.0f, Priority.HIGH, TopicStatus.IN_PROGRESS, "2024-05-12", "Learn shapes table", "2024-05-08", "2024-05-11", 4.5f)
        val tFormulas = Topic("t_form", cCalculus.id, sMath.id, "Mathematics Formulas", "Integral & differential shortcuts", 8f, 6.4f, Priority.HIGH, TopicStatus.COMPLETED, "2024-05-08", "Flashcards ready", "2024-05-08", "2024-05-15", 4.8f)

        _topics.value = listOf(tTW, tDeriv, tKin, tBond, tFormulas)

        // 10 Videos for Time & Work matching prompt specifications
        val v1 = Video("v1", tTW.id, "1. Introduction to Time & Work", 35, true, "2024-05-08", "07:15 PM", "Basic units and work equations", 1)
        val v2 = Video("v2", tTW.id, "2. Basic Concepts & Work Rate", 40, true, "2024-05-08", "07:55 PM", "Work = Rate x Time concept", 2)
        val v3 = Video("v3", tTW.id, "3. Important Formulas & LCM Method", 45, true, "2024-05-08", "08:15 PM", "LCM method shortcut notes", 3)
        val v4 = Video("v4", tTW.id, "4. Type 1 Questions: Alternate Days", 30, true, "2024-05-07", "06:00 PM", "A and B working alternatively", 4)
        val v5 = Video("v5", tTW.id, "5. Type 2 Questions: Man Days & Efficiency", 50, true, "2024-05-07", "07:00 PM", "M1 D1 H1 / W1 = M2 D2 H2 / W2", 5)
        val v6 = Video("v6", tTW.id, "6. Advanced Questions & Leaving Mid-Way", 45, true, "2024-05-06", "08:00 PM", "When worker leaves before completion", 6)
        val v7 = Video("v7", tTW.id, "7. Pipes & Cisterns Concepts", 40, true, "2024-05-06", "09:00 PM", "Inlet vs outlet negative work", 7)
        val v8 = Video("v8", tTW.id, "8. Practice Set 1 - 25 Problems", 60, true, "2024-05-05", "05:00 PM", "Timed problem solving", 8)
        val v9 = Video("v9", tTW.id, "9. Previous Year Questions (PYQ)", 55, false, "", "", "Solve all 2021-2023 questions", 9)
        val v10 = Video("v10", tTW.id, "10. Rapid Revision Lecture", 30, false, "", "", "Final quick recap", 10)

        _videos.value = listOf(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10)

        // Study Sessions matching screenshot exactly (Date: 2024-05-08)
        // 07:00 PM - 08:15 PM Mathematics Time & Work 1h 15m (65%)
        // 08:30 PM - 09:30 PM Physics Kinematics 1h 00m (40%)
        // 10:00 PM - 11:05 PM Chemistry Chemical Bonding 1h 05m (20%)
        // 11:15 PM - 12:00 AM Revision Mathematics (Formulas) 45m (80%)
        // Total = 75 + 60 + 65 + 45 = 245 + 15 = 260m = 4h 20m! Exactly 4h 20m matching screenshot!
        val ses1 = StudySession("ses_1", sMath.id, tTW.id, v1.id, "07:00 PM", "08:15 PM", 75, "2024-05-08", StudyMode.DEEP_FOCUS, 4, "Solved efficiency questions")
        val ses2 = StudySession("ses_2", sPhys.id, tKin.id, "", "08:30 PM", "09:30 PM", 60, "2024-05-08", StudyMode.POMODORO, 3, "Projectile equations derive")
        val ses3 = StudySession("ses_3", sChem.id, tBond.id, "", "10:00 PM", "11:05 PM", 65, "2024-05-08", StudyMode.POMODORO, 4, "Hybridization molecular orbital theory")
        val ses4 = StudySession("ses_4", sMath.id, tFormulas.id, "", "11:15 PM", "12:00 AM", 45, "2024-05-08", StudyMode.REVISION, 5, "Formula recitation and flashcards")

        _sessions.value = listOf(ses1, ses2, ses3, ses4)

        // Revisions
        val rev1 = Revision("rev_1", tTW.id, sMath.id, 1, "2024-05-08", "2024-05-08", RevisionStatus.COMPLETED, 4, "Time & Work shortcuts reviewed")
        val rev2 = Revision("rev_2", tKin.id, sPhys.id, 1, "2024-05-09", "", RevisionStatus.PENDING, 0, "Kinematics 2D Relative Velocity")
        val rev3 = Revision("rev_3", tBond.id, sChem.id, 2, "2024-05-11", "", RevisionStatus.PENDING, 0, "VSEPR shapes & exceptions")
        val rev4 = Revision("rev_4", tFormulas.id, sMath.id, 3, "2024-05-08", "2024-05-08", RevisionStatus.COMPLETED, 5, "Differential equations & calculus formulas")

        _revisions.value = listOf(rev1, rev2, rev3, rev4)

        // Exams
        val examJee = Exam(
            id = "ex_jee",
            name = "JEE Main 2024",
            subjectIds = listOf(sMath.id, sPhys.id, sChem.id),
            examDate = "2024-07-03", // 56 days from May 8 2024
            examTime = "09:00 AM",
            location = "City Testing Arena Hall 3",
            notes = "Target 240+ marks. Focus heavily on Calculus and Mechanics."
        )
        val examBoard = Exam(
            id = "ex_board",
            name = "Business Studies Board",
            subjectIds = listOf(sBiz.id),
            examDate = "2024-08-15",
            examTime = "10:30 AM",
            location = "Campus Hall",
            notes = "Case studies practice required."
        )
        _exams.value = listOf(examJee, examBoard)

        // Daily Missions
        val m1 = DailyMission("m1", "Study for 6 hours", 6, 4, "hours", 100, false)
        val m2 = DailyMission("m2", "Complete 2 topics", 2, 1, "topics", 80, false)
        val m3 = DailyMission("m3", "Watch 4 video lectures", 4, 3, "videos", 60, false)
        val m4 = DailyMission("m4", "Complete 2 spaced revisions", 2, 2, "revisions", 90, true)
        val m5 = DailyMission("m5", "Complete 4 focus sessions", 4, 4, "sessions", 70, true)

        _missions.value = listOf(m1, m2, m3, m4, m5)

        // Tasks
        val task1 = StudyTask("tsk_1", "Practice 25 Time & Work questions", "Solve from coaching module sheet", "2024-05-08", "08:30 PM", Priority.HIGH, sMath.id, tTW.id, TaskStatus.COMPLETED, 60)
        val task2 = StudyTask("tsk_2", "Derive Range & Max Height equations", "Kinematics projectile motion", "2024-05-08", "09:30 PM", Priority.HIGH, sPhys.id, tKin.id, TaskStatus.COMPLETED, 45)
        val task3 = StudyTask("tsk_3", "Memorize Hybridization Examples", "sp3d, sp3d2 shapes and bond angles", "2024-05-08", "11:00 PM", Priority.MEDIUM, sChem.id, tBond.id, TaskStatus.IN_PROGRESS, 40)
        val task4 = StudyTask("tsk_4", "Attempt Mock Test #4", "Full syllabus Physics & Chemistry test", "2024-05-09", "06:00 PM", Priority.HIGH, sMath.id, "", TaskStatus.TODO, 180)

        _tasks.value = listOf(task1, task2, task3, task4)

        // Notes
        val n1 = Note("n1", "Time & Work Golden Formula", "If A completes in x days, B in y days, together they take (xy)/(x+y) days. Efficiency ratio is reciprocal of days ratio.", NoteCategory.SHORT_TRICK, true, sMath.id, tTW.id, "08 May 2024")
        val n2 = Note("n2", "Kinematics Relative Velocity", "v_AB = v_A - v_B. In 2D, always resolve into orthogonal components along x and y before applying 1D kinematic equations.", NoteCategory.FORMULA, true, sPhys.id, tKin.id, "08 May 2024")
        val n3 = Note("n3", "VSEPR Geometry vs Shape", "Lone pairs repel more strongly than bonding pairs: LP-LP > LP-BP > BP-BP. Geometry includes lone pairs, shape considers only bonded atoms.", NoteCategory.DEFINITION, false, sChem.id, tBond.id, "07 May 2024")

        _notes.value = listOf(n1, n2, n3)

        // Formulas
        val f1 = Formula("f1", "Alternate Days Work Equation", "Total Work = LCM(d1, d2). Total Cycle Work = Eff_A + Eff_B. Cycle count = Work / Cycle Work.", sMath.id, tTW.id, "Shortcut for alternate shifts problem", true)
        val f2 = Formula("f2", "Trajectory Equation in Projectile", "y = x tan(θ) - (g x²) / (2 u² cos²(θ)) = x tan(θ) [1 - x / R]", sPhys.id, tKin.id, "Standard parabolic trajectory formula", true)
        val f3 = Formula("f3", "Steric Number for Hybridization", "SN = (Number of bonded atoms) + (Number of lone pairs on central atom)", sChem.id, tBond.id, "Quick test trick for hybrid state", true)

        _formulas.value = listOf(f1, f2, f3)

        // Mock Tests
        val mt1 = MockTest("mt1", "JEE Main Full Mock #1", examJee.id, sMath.id, "2024-05-04", 75, 48, 12, 15, 300f, 180f, 175, "Time ran short in Maths section")
        val mt2 = MockTest("mt2", "Physics Mechanics Speed Drill", examJee.id, sPhys.id, "2024-05-06", 30, 24, 4, 2, 120f, 92f, 55, "Strong in kinematics, need work on friction")

        _mockTests.value = listOf(mt1, mt2)

        // Flashcards
        val fc1 = Flashcard("fc1", sMath.id, tTW.id, "What is the relation between Efficiency and Days?", "Efficiency is inversely proportional to Days taken for constant total work.", true, FlashcardMastery.MASTERED)
        val fc2 = Flashcard("fc2", sPhys.id, tKin.id, "At what angle of projection is Range maximum for flat ground?", "θ = 45 degrees, where R_max = u² / g.", true, FlashcardMastery.REVIEW)
        val fc3 = Flashcard("fc3", sChem.id, tBond.id, "What is the hybridization and bond angle in CH4 vs NH3?", "CH4: sp3 (109.5°). NH3: sp3 with 1 lone pair (107°).", false, FlashcardMastery.LEARNING)

        _flashcards.value = listOf(fc1, fc2, fc3)

        // Peer Study Rooms
        _peerRooms.value = listOf(
            PeerRoom("pr1", "Early Bird 5 AM Club", 248, "Deep Focus", true),
            PeerRoom("pr2", "JEE Main 2024 Aspirants", 182, "STEM / Practice", true),
            PeerRoom("pr3", "Night Owl Silent Marathon", 314, "Pomodoro 50/10", true),
            PeerRoom("pr4", "Calculus & Algebra Grind", 96, "Maths Problem Solving", true)
        )
    }
}
