package com.example.data.model

data class Subject(
    val id: String,
    val name: String,
    val description: String = "",
    val colorHex: String = "#14B8A6",
    val iconName: String = "book",
    val targetHours: Float = 50f,
    val priority: Priority = Priority.HIGH,
    val examName: String = "",
    val examDate: String = "",
    val notes: String = "",
    val isEnabled: Boolean = true
)

enum class Priority { HIGH, MEDIUM, LOW }

enum class TopicStatus {
    NOT_STARTED,
    IN_PROGRESS,
    REVISION_PENDING,
    COMPLETED
}

data class Chapter(
    val id: String,
    val subjectId: String,
    val name: String,
    val order: Int = 0
)

data class Topic(
    val id: String,
    val chapterId: String,
    val subjectId: String,
    val name: String,
    val description: String = "",
    val estimatedHours: Float = 10f,
    val completedHours: Float = 0f,
    val priority: Priority = Priority.HIGH,
    val status: TopicStatus = TopicStatus.NOT_STARTED,
    val targetDate: String = "",
    val notes: String = "",
    val lastStudiedDate: String = "",
    val nextRevisionDate: String = "",
    val focusRating: Float = 4.0f
)

data class Video(
    val id: String,
    val topicId: String,
    val title: String,
    val durationMinutes: Int = 30,
    val isCompleted: Boolean = false,
    val watchedDate: String = "",
    val watchedTime: String = "",
    val notes: String = "",
    val order: Int = 0
)

enum class StudyMode(val title: String, val workMinutes: Int, val breakMinutes: Int) {
    DAY("Day Study", 60, 10),
    NIGHT("Night Study", 45, 10),
    POMODORO("Pomodoro", 25, 5),
    DEEP_FOCUS("Deep Focus", 90, 15),
    REVISION("Revision Mode", 30, 5),
    EXAM_PREP("Exam Prep", 60, 10),
    CUSTOM("Custom Study", 50, 10)
}

data class StudySession(
    val id: String,
    val subjectId: String,
    val topicId: String = "",
    val videoId: String = "",
    val startTime: String,
    val endTime: String,
    val durationMinutes: Int,
    val date: String, // YYYY-MM-DD
    val mode: StudyMode = StudyMode.POMODORO,
    val focusRating: Int = 4, // 1 to 5
    val notes: String = ""
)

enum class RevisionStatus { PENDING, COMPLETED, OVERDUE }

data class Revision(
    val id: String,
    val topicId: String,
    val subjectId: String,
    val revisionNumber: Int = 1,
    val scheduledDate: String,
    val completedDate: String = "",
    val status: RevisionStatus = RevisionStatus.PENDING,
    val focusRating: Int = 0,
    val notes: String = ""
)

data class Exam(
    val id: String,
    val name: String,
    val subjectIds: List<String> = emptyList(),
    val examDate: String, // YYYY-MM-DD
    val examTime: String = "09:00 AM",
    val location: String = "Main Center",
    val notes: String = "",
    val reminderEnabled: Boolean = true
)

data class DailyMission(
    val id: String,
    val title: String,
    val targetValue: Int,
    val currentValue: Int,
    val unit: String,
    val xpReward: Int,
    val isCompleted: Boolean = false
)

enum class TaskStatus { TODO, IN_PROGRESS, COMPLETED }

data class StudyTask(
    val id: String,
    val title: String,
    val description: String = "",
    val date: String,
    val deadline: String = "11:59 PM",
    val priority: Priority = Priority.HIGH,
    val subjectId: String = "",
    val topicId: String = "",
    val status: TaskStatus = TaskStatus.TODO,
    val estimatedMinutes: Int = 45
)

enum class NoteCategory { GENERAL, FORMULA, SHORT_TRICK, IMPORTANT_QUESTION, DEFINITION }

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val category: NoteCategory = NoteCategory.GENERAL,
    val isPinned: Boolean = false,
    val subjectId: String = "",
    val topicId: String = "",
    val createdAt: String
)

data class StudyMaterial(
    val id: String,
    val title: String,
    val description: String = "",
    val fileType: String = "PDF", // PDF, DOC, IMAGE, LINK
    val linkOrNote: String = "",
    val subjectId: String = "",
    val topicId: String = ""
)

data class Question(
    val id: String,
    val subjectId: String,
    val topicId: String = "",
    val questionText: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String = "",
    val isPYQ: Boolean = false,
    val isImportant: Boolean = false,
    val attemptedCount: Int = 0,
    val correctCount: Int = 0
)

data class Formula(
    val id: String,
    val title: String,
    val formulaText: String,
    val subjectId: String,
    val topicId: String = "",
    val description: String = "",
    val isFavorite: Boolean = false
)

data class MockTest(
    val id: String,
    val testName: String,
    val examId: String = "",
    val subjectId: String = "",
    val date: String,
    val totalQuestions: Int,
    val correct: Int,
    val incorrect: Int,
    val unattempted: Int,
    val totalMarks: Float,
    val marksObtained: Float,
    val timeTakenMinutes: Int,
    val notes: String = ""
) {
    val accuracyPercent: Float
        get() = if (correct + incorrect > 0) (correct.toFloat() / (correct + incorrect)) * 100f else 0f
    val netScorePercent: Float
        get() = if (totalMarks > 0) (marksObtained / totalMarks) * 100f else 0f
}

enum class FlashcardMastery { LEARNING, REVIEW, MASTERED }

data class Flashcard(
    val id: String,
    val subjectId: String,
    val topicId: String = "",
    val front: String,
    val back: String,
    val isFavorite: Boolean = false,
    val mastery: FlashcardMastery = FlashcardMastery.LEARNING,
    val nextReviewDate: String = ""
)

data class PeerRoom(
    val id: String,
    val name: String,
    val participantsCount: Int,
    val tag: String,
    val isActive: Boolean = true
)

data class DistractionLog(
    val id: String,
    val appName: String,
    val timestamp: String,
    val reflection: String = ""
)

data class UserProfile(
    val name: String = "Alex",
    val title: String = "Focused Aspirant",
    val dailyTargetMinutes: Int = 360, // 6h 00m as in screenshot
    val currentStreak: Int = 12,
    val totalXp: Int = 1250,
    val level: Int = 12,
    val darkTheme: Boolean = true,
    val accentColorHex: String = "#14B8A6",
    val aiEnabled: Boolean = true
)
