package com.example.ui.screens.subscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.model.Note
import com.example.data.model.NoteCategory
import com.example.ui.theme.*
import com.example.viewmodel.StudyViewModel

@Composable
fun NotesAndFormulasScreen(
    viewModel: StudyViewModel,
    onBack: () -> Unit
) {
    val notes by viewModel.repository.notes.collectAsState()
    val formulas by viewModel.repository.formulas.collectAsState()
    val subjects by viewModel.repository.subjects.collectAsState()

    var activeView by remember { mutableStateOf("Notes") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddNoteDialog by remember { mutableStateOf(false) }

    val filteredNotes = notes.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.content.contains(searchQuery, ignoreCase = true)
    }

    val filteredFormulas = formulas.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.formulaText.contains(searchQuery, ignoreCase = true)
    }

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
                    Text("Notes & Formula Library", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Searchable short-tricks & key definitions", color = TextSecondary, fontSize = 11.sp)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { activeView = if (activeView == "Notes") "Formulas" else "Notes" },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(if (activeView == "Notes") "Formulas" else "Notes", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showAddNoteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Text("New", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search formulas, tricks, equations...", color = TextSecondary, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CardDark,
                unfocusedContainerColor = CardDark
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (activeView == "Notes") {
            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filteredNotes) { note ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CardDark)
                            .border(1.dp, CardBorderDark, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (note.isPinned) {
                                        Icon(Icons.Default.PushPin, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(16.dp))
                                    }
                                    Text(text = note.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AccentAmber.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = note.category.name.replace("_", " "), color = AccentAmber, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(text = note.content, color = TextSecondary, fontSize = 12.sp, lineHeight = 18.sp)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Saved: ${note.createdAt}", color = TextMuted, fontSize = 10.sp)
                                IconButton(onClick = { viewModel.repository.deleteNote(note.id) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filteredFormulas) { form ->
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
                                Text(form.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Icon(
                                    imageVector = if (form.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = AccentAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceDark)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = form.formulaText,
                                    color = EmeraldGreen,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(form.description, color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    // Add Note Dialog
    if (showAddNoteDialog) {
        var noteTitle by remember { mutableStateOf("") }
        var noteContent by remember { mutableStateOf("") }
        var category by remember { mutableStateOf(NoteCategory.SHORT_TRICK) }

        Dialog(onDismissRequest = { showAddNoteDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Add Quick Academic Note", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = noteTitle, onValueChange = { noteTitle = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth().testTag("note_title_input"))
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("Note Content / Trick") },
                        modifier = Modifier.fillMaxWidth().height(110.dp).testTag("note_content_input")
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddNoteDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                if (noteTitle.isNotBlank()) {
                                    viewModel.repository.addNote(
                                        title = noteTitle.trim(),
                                        content = noteContent.trim(),
                                        category = category,
                                        isPinned = true
                                    )
                                    showAddNoteDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Text("Save Note")
                        }
                    }
                }
            }
        }
    }
}
