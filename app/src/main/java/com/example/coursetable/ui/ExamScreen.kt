package com.example.coursetable.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coursetable.data.CourseRepository
import com.example.coursetable.data.Exam
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    repository: CourseRepository,
    modifier: Modifier = Modifier
) {
    var exams by remember { mutableStateOf(repository.loadExams().sortedBy { it.examTime }) }
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "考试信息",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            if (exams.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无考试信息，点击右下角按钮添加",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(exams, key = { it.id }) { exam ->
                        ExamCard(
                            exam = exam,
                            onDelete = {
                                repository.deleteExam(exam.id)
                                exams = repository.loadExams().sortedBy { it.examTime }
                            }
                        )
                    }
                    // Bottom spacing for FAB
                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "添加考试")
        }
    }

    if (showAddDialog) {
        AddExamDialog(
            courseNames = repository.getCourseNames(),
            onDismiss = { showAddDialog = false },
            onConfirm = { exam ->
                repository.saveExam(exam)
                exams = repository.loadExams().sortedBy { it.examTime }
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun ExamCard(exam: Exam, onDelete: () -> Unit) {
    val now = LocalDateTime.now()
    val examDateTime = try {
        LocalDateTime.parse(exam.examTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    } catch (e: Exception) {
        null
    }

    val countdownText = if (examDateTime != null) {
        val daysBetween = ChronoUnit.DAYS.between(now.toLocalDate(), examDateTime.toLocalDate())
        when {
            daysBetween < 0 -> "考试已结束"
            daysBetween == 0L -> {
                if (now.isBefore(examDateTime)) "今天考试" else "考试已结束"
            }
            daysBetween == 1L -> "明天考试"
            else -> "距离考试还有 ${daysBetween} 天"
        }
    } else {
        "时间格式错误"
    }

    val countdownColor = if (examDateTime != null) {
        val daysBetween = ChronoUnit.DAYS.between(now.toLocalDate(), examDateTime.toLocalDate())
        when {
            daysBetween < 0 -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            daysBetween <= 3 -> MaterialTheme.colorScheme.error
            daysBetween <= 7 -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.primary
        }
    } else {
        MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Countdown badge
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(80.dp)
            ) {
                if (examDateTime != null) {
                    val daysBetween = ChronoUnit.DAYS.between(now.toLocalDate(), examDateTime.toLocalDate())
                    if (daysBetween >= 0) {
                        Text(
                            text = "${maxOf(daysBetween, 0)}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = countdownColor
                        )
                        Text(
                            text = "天",
                            fontSize = 12.sp,
                            color = countdownColor.copy(alpha = 0.7f)
                        )
                    } else {
                        Text(
                            text = "已结束",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = countdownColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Vertical divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Exam info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exam.subject,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = exam.examTime,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                if (exam.room.isNotEmpty()) {
                    Text(
                        text = exam.room,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Text(
                    text = countdownText,
                    fontSize = 12.sp,
                    color = countdownColor,
                    fontWeight = FontWeight.Medium
                )
            }

            // Delete button
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExamDialog(
    courseNames: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (Exam) -> Unit
) {
    var selectedSubject by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var examTimeText by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }
    var subjectError by remember { mutableStateOf<String?>(null) }
    var timeError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        examTimeText = String.format("%04d-%02d-%02d %02d:%02d", year, month + 1, day, hour, minute)
                        timeError = null
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加考试") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Subject dropdown
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSubject,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("考试科目") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        isError = subjectError != null,
                        supportingText = subjectError?.let {
                            { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        if (courseNames.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("暂无课程，请先导入课表") },
                                onClick = { dropdownExpanded = false }
                            )
                        } else {
                            courseNames.forEach { name ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        selectedSubject = name
                                        subjectError = null
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Exam time
                OutlinedTextField(
                    value = examTimeText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("考试时间") },
                    placeholder = { Text("点击选择日期和时间") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    isError = timeError != null,
                    supportingText = timeError?.let {
                        { Text(it, color = MaterialTheme.colorScheme.error) }
                    }
                )
                TextButton(
                    onClick = { showDateTimePicker() },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("选择时间")
                }

                // Room
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("考试教室（选填）") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                var hasError = false
                if (selectedSubject.isEmpty()) {
                    subjectError = "请选择考试科目"
                    hasError = true
                }
                if (examTimeText.isEmpty()) {
                    timeError = "请选择考试时间"
                    hasError = true
                }
                if (!hasError) {
                    onConfirm(
                        Exam(
                            id = System.currentTimeMillis(),
                            subject = selectedSubject,
                            examTime = examTimeText,
                            room = room
                        )
                    )
                }
            }) {
                Text("添加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
