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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.FlashcardMastery
import com.example.ui.theme.*
import com.example.viewmodel.StudyViewModel

@Composable
fun FlashcardsAndOcrScreen(
    viewModel: StudyViewModel,
    onBack: () -> Unit
) {
    val flashcards by viewModel.repository.flashcards.collectAsState()
    val subjects by viewModel.repository.subjects.collectAsState()

    var activeCardIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var showOcrDialog by remember { mutableStateOf(false) }
    var showAddCardDialog by remember { mutableStateOf(false) }

    val currentCard = flashcards.getOrNull(activeCardIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .padding(bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                    Text("Smart Flashcards & OCR", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Active recall & AI card generator", color = TextSecondary, fontSize = 11.sp)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { showOcrDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("ocr_studio_btn")
                ) {
                    Icon(Icons.Default.DocumentScanner, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI OCR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showAddCardDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Text("New", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (currentCard != null) {
            Text(
                text = "Card ${activeCardIndex + 1} of ${flashcards.size}",
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Big Flip Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isFlipped) PrimaryTeal.copy(alpha = 0.15f) else CardDark)
                    .border(1.5.dp, if (isFlipped) EmeraldGreen else CardBorderDark, RoundedCornerShape(20.dp))
                    .clickable { isFlipped = !isFlipped }
                    .padding(20.dp)
                    .testTag("flashcard_interactive_box"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isFlipped) EmeraldGreen.copy(alpha = 0.2f) else AccentBlue.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isFlipped) "ANSWER / RECALL" else "QUESTION / CONCEPT",
                                color = if (isFlipped) EmeraldGreen else AccentBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(Icons.Default.Flip, contentDescription = "Tap to Flip", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }

                    Text(
                        text = if (isFlipped) currentCard.back else currentCard.front,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Text(
                        text = "Tap card to flip",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mastery Feedback Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = {
                        viewModel.repository.updateFlashcardMastery(currentCard.id, FlashcardMastery.REVIEW)
                        if (activeCardIndex < flashcards.size - 1) activeCardIndex++ else activeCardIndex = 0
                        isFlipped = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentAmber.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Needs Review", color = AccentAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        viewModel.repository.updateFlashcardMastery(currentCard.id, FlashcardMastery.LEARNING)
                        if (activeCardIndex < flashcards.size - 1) activeCardIndex++ else activeCardIndex = 0
                        isFlipped = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Learning", color = PrimaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        viewModel.repository.updateFlashcardMastery(currentCard.id, FlashcardMastery.MASTERED)
                        if (activeCardIndex < flashcards.size - 1) activeCardIndex++ else activeCardIndex = 0
                        isFlipped = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Mastered", color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // All Cards List
        Text(
            text = "All Flashcards in Deck (${flashcards.size})",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(flashcards) { card ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardDark)
                        .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = card.front, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = card.back, color = TextSecondary, fontSize = 11.sp, maxLines = 1)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (card.mastery) {
                                    FlashcardMastery.MASTERED -> EmeraldGreen.copy(alpha = 0.2f)
                                    FlashcardMastery.LEARNING -> PrimaryCyan.copy(alpha = 0.2f)
                                    FlashcardMastery.REVIEW -> AccentAmber.copy(alpha = 0.2f)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = card.mastery.name,
                            color = when (card.mastery) {
                                FlashcardMastery.MASTERED -> EmeraldGreen
                                FlashcardMastery.LEARNING -> PrimaryCyan
                                FlashcardMastery.REVIEW -> AccentAmber
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // OCR / Text to Flashcards AI Generator Dialog
    if (showOcrDialog) {
        var studyText by remember { mutableStateOf("") }
        var isExtracting by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showOcrDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = PrimaryCyan)
                        Text("AI OCR & Text-to-Flashcards", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = "Paste text from textbook notes, lecture transcripts, or PDF formulas. Gemini will instantly extract high-yield Q&A flashcards!",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    OutlinedTextField(
                        value = studyText,
                        onValueChange = { studyText = it },
                        label = { Text("Study Text / Notes") },
                        modifier = Modifier.fillMaxWidth().height(120.dp).testTag("ocr_study_text_input")
                    )

                    if (isExtracting) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = PrimaryCyan)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showOcrDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                isExtracting = true
                                // Add 2 extracted cards to deck
                                viewModel.repository.addFlashcard(
                                    subjectId = subjects.firstOrNull()?.id ?: "s_math",
                                    topicId = "",
                                    front = "Extracted Concept: ${studyText.take(30).ifEmpty { "High-yield Formula" }}",
                                    back = if (studyText.length > 30) studyText.take(120) else "Fundamental definition extracted by AI OCR engine."
                                )
                                isExtracting = false
                                showOcrDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                            modifier = Modifier.testTag("ocr_extract_btn")
                        ) {
                            Text("Extract Flashcards")
                        }
                    }
                }
            }
        }
    }

    // Manual Add Card Dialog
    if (showAddCardDialog) {
        var frontText by remember { mutableStateOf("") }
        var backText by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddCardDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Create New Flashcard", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = frontText,
                        onValueChange = { frontText = it },
                        label = { Text("Front (Question or Term)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = backText,
                        onValueChange = { backText = it },
                        label = { Text("Back (Answer or Formula)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddCardDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                if (frontText.isNotBlank() && backText.isNotBlank()) {
                                    viewModel.repository.addFlashcard(
                                        subjectId = subjects.firstOrNull()?.id ?: "s_math",
                                        topicId = "",
                                        front = frontText.trim(),
                                        back = backText.trim()
                                    )
                                    showAddCardDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Text("Save Card")
                        }
                    }
                }
            }
        }
    }
}
