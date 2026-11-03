package com.example.ui.screens.subscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.viewmodel.StudyViewModel

data class QuizQuestion(
    val id: String,
    val subject: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val isPYQ: Boolean
)

@Composable
fun QuizAndPracticeScreen(
    viewModel: StudyViewModel,
    onBack: () -> Unit
) {
    val mockTests by viewModel.repository.mockTests.collectAsState()
    val subjects by viewModel.repository.subjects.collectAsState()

    var activeTab by remember { mutableStateOf("Quiz") }
    var showAddMockDialog by remember { mutableStateOf(false) }

    val sampleQuiz = remember {
        listOf(
            QuizQuestion(
                id = "q1",
                subject = "Mathematics",
                question = "A can do a piece of work in 12 days and B in 24 days. How many days will they take working together?",
                options = listOf("6 days", "8 days", "10 days", "16 days"),
                correctIndex = 1,
                explanation = "Combined efficiency = 1/12 + 1/24 = 3/24 = 1/8. Therefore, together they take 8 days.",
                isPYQ = true
            ),
            QuizQuestion(
                id = "q2",
                subject = "Physics",
                question = "What is the angle of projection with the horizontal for which the horizontal range and maximum height are equal?",
                options = listOf("θ = 45°", "tan(θ) = 4", "tan(θ) = 2", "θ = 60°"),
                correctIndex = 1,
                explanation = "R = 4H cot(θ). When R = H, cot(θ) = 1/4 => tan(θ) = 4 (approx 76°).",
                isPYQ = true
            ),
            QuizQuestion(
                id = "q3",
                subject = "Chemistry",
                question = "Which molecule has sp³d hybridization with a linear molecular geometry?",
                options = listOf("XeF2", "SF4", "ClF3", "PCl5"),
                correctIndex = 0,
                explanation = "XeF2 has 2 bond pairs and 3 equatorial lone pairs in a trigonal bipyramidal geometry, giving a linear shape.",
                isPYQ = true
            )
        )
    }

    val currentQuizIdx by viewModel.currentQuizIndex.collectAsState()
    val selectedOption by viewModel.selectedQuizOption.collectAsState()
    val quizScore by viewModel.quizScore.collectAsState()
    val currentQuestion = sampleQuiz.getOrNull(currentQuizIdx % sampleQuiz.size)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .padding(bottom = 80.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Column {
                    Text("Question Bank & Mock Tests", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Interactive drills & exam analytics", color = TextSecondary, fontSize = 11.sp)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { activeTab = if (activeTab == "Quiz") "Mocks" else "Quiz" },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(if (activeTab == "Quiz") "Mock Tests" else "Quiz Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (activeTab == "Quiz") {
            // Interactive Practice Quiz View
            if (currentQuestion != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(CardDark)
                        .border(1.dp, CardBorderDark, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(currentQuestion.subject, color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                if (currentQuestion.isPYQ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AccentAmber.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("PYQ", color = AccentAmber, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Text("Score: $quizScore", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = currentQuestion.question,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Options
                        currentQuestion.options.forEachIndexed { idx, opt ->
                            val isChosen = selectedOption == idx
                            val isCorrect = idx == currentQuestion.correctIndex
                            val btnBg = when {
                                selectedOption == null -> SurfaceDark
                                isChosen && isCorrect -> EmeraldGreen.copy(alpha = 0.25f)
                                isChosen && !isCorrect -> AccentRed.copy(alpha = 0.25f)
                                isCorrect -> EmeraldGreen.copy(alpha = 0.25f)
                                else -> SurfaceDark
                            }
                            val btnBorder = when {
                                selectedOption == null -> CardBorderDark
                                isChosen && isCorrect -> EmeraldGreen
                                isChosen && !isCorrect -> AccentRed
                                isCorrect -> EmeraldGreen
                                else -> CardBorderDark
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(btnBg)
                                    .border(1.dp, btnBorder, RoundedCornerShape(12.dp))
                                    .clickable(enabled = selectedOption == null) {
                                        viewModel.answerQuizQuestion(idx, isCorrect)
                                    }
                                    .padding(12.dp)
                                    .testTag("quiz_option_$idx"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "${('A' + idx)}.",
                                    color = if (isChosen) EmeraldGreen else TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = opt,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Explanation
                        if (selectedOption != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceDark)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "💡 Explanation: ${currentQuestion.explanation}",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Button(
                                onClick = { viewModel.nextQuizQuestion() },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.align(Alignment.End).testTag("next_quiz_question_btn")
                            ) {
                                Text("Next Question", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // Mock Tests List View
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Logged Mock Tests (${mockTests.size})", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("+ Log Test", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { showAddMockDialog = true })
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(mockTests) { mock ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CardDark)
                            .border(1.dp, CardBorderDark, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(mock.testName, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("${mock.marksObtained.toInt()} / ${mock.totalMarks.toInt()} Marks", color = EmeraldGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("Accuracy: ${mock.accuracyPercent.toInt()}% • Correct: ${mock.correct} • Wrong: ${mock.incorrect} • Unattempted: ${mock.unattempted}", color = TextSecondary, fontSize = 11.sp)
                            if (mock.notes.isNotEmpty()) {
                                Text("Analysis: ${mock.notes}", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Mock Dialog
    if (showAddMockDialog) {
        var testName by remember { mutableStateOf("") }
        var totalQ by remember { mutableStateOf("75") }
        var correct by remember { mutableStateOf("50") }
        var incorrect by remember { mutableStateOf("10") }
        var marks by remember { mutableStateOf("190") }
        var notes by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddMockDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Log Mock Test Attempt", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = testName, onValueChange = { testName = it }, label = { Text("Test Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = totalQ, onValueChange = { totalQ = it }, label = { Text("Total Questions") }, modifier = Modifier.fillMaxWidth())
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = correct, onValueChange = { correct = it }, label = { Text("Correct") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = incorrect, onValueChange = { incorrect = it }, label = { Text("Incorrect") }, modifier = Modifier.weight(1f))
                    }
                    OutlinedTextField(value = marks, onValueChange = { marks = it }, label = { Text("Marks Obtained") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Analysis & Weak Sections") }, modifier = Modifier.fillMaxWidth())

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddMockDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                if (testName.isNotBlank()) {
                                    viewModel.repository.addMockTest(
                                        testName = testName.trim(),
                                        examId = "ex_jee",
                                        subjectId = subjects.firstOrNull()?.id ?: "s_math",
                                        totalQuestions = totalQ.toIntOrNull() ?: 75,
                                        correct = correct.toIntOrNull() ?: 50,
                                        incorrect = incorrect.toIntOrNull() ?: 10,
                                        totalMarks = 300f,
                                        marksObtained = marks.toFloatOrNull() ?: 190f,
                                        timeTakenMinutes = 180,
                                        notes = notes
                                    )
                                    showAddMockDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Text("Save Test")
                        }
                    }
                }
            }
        }
    }
}
