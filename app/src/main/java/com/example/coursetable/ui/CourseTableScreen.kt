package com.example.coursetable.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.coursetable.data.CourseRepository
import com.example.coursetable.util.WeekParser
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun CourseTableScreen(repository: CourseRepository) {
    var refreshTrigger by remember { mutableStateOf(0) }
    val allCourses = remember(refreshTrigger) {
        try { repository.loadCourses() } catch (_: Exception) { emptyList() }
    }
    val maxWeek = remember(allCourses) { WeekParser.getMaxWeek(allCourses) }
    var selectedTab by remember { mutableIntStateOf(0) }

    // Exam reminder: only check once on first load
    var showExamReminder by remember { mutableStateOf(false) }
    var examReminderText by remember { mutableStateOf("") }
    var hasCheckedExamReminder by remember { mutableStateOf(false) }

    LaunchedEffect(hasCheckedExamReminder) {
        if (hasCheckedExamReminder) return@LaunchedEffect
        hasCheckedExamReminder = true

        try {
            val exams = repository.loadExams()
            val today = LocalDate.now()
            val upcoming = mutableListOf<String>()
            for (exam in exams) {
                try {
                    val examDate = LocalDate.parse(exam.examTime.substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val days = ChronoUnit.DAYS.between(today, examDate)
                    when (days) {
                        7L -> upcoming.add("${exam.subject}（还有1周）")
                        1L -> upcoming.add("${exam.subject}（明天）")
                        0L -> upcoming.add("${exam.subject}（今天）")
                    }
                } catch (_: Exception) {}
            }
            if (upcoming.isNotEmpty()) {
                examReminderText = upcoming.joinToString("\n")
                showExamReminder = true
            }
        } catch (_: Exception) {}
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Content area
        when (selectedTab) {
            0 -> TodayCourseScreen(
                repository = repository,
                maxWeek = maxWeek,
                refreshTrigger = refreshTrigger,
                modifier = Modifier.weight(1f)
            )
            1 -> SettingsScreen(
                repository = repository,
                modifier = Modifier.weight(1f),
                onCoursesImported = { refreshTrigger++ },
                onSemesterStartChanged = { refreshTrigger++ }
            )
            2 -> ExamScreen(
                repository = repository,
                modifier = Modifier.weight(1f)
            )
        }

        // Bottom navigation bar
        NavigationBar {
            NavigationBarItem(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                icon = { Icon(Icons.Default.CalendarToday, contentDescription = "今日课表") },
                label = { Text("今日课表") }
            )
            NavigationBarItem(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                icon = { Icon(Icons.Default.EditCalendar, contentDescription = "考试信息") },
                label = { Text("考试信息") }
            )
            NavigationBarItem(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                icon = { Icon(Icons.Default.Settings, contentDescription = "个人设置") },
                label = { Text("个人设置") }
            )
        }
    }

    // Exam reminder dialog
    if (showExamReminder) {
        AlertDialog(
            onDismissRequest = { showExamReminder = false },
            title = { Text("考试提醒") },
            text = {
                Text(
                    "以下考试即将到来：\n\n$examReminderText",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showExamReminder = false }) {
                    Text("知道了")
                }
            }
        )
    }

}
