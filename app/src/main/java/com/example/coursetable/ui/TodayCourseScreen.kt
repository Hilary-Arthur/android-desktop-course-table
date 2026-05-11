package com.example.coursetable.ui

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coursetable.data.Course
import com.example.coursetable.data.CourseRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.foundation.gestures.detectHorizontalDragGestures

@Composable
fun TodayCourseScreen(
    repository: CourseRepository,
    maxWeek: Int,
    refreshTrigger: Int,
    modifier: Modifier = Modifier
) {
    val todayDayOfWeek = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 1
        Calendar.TUESDAY -> 2
        Calendar.WEDNESDAY -> 3
        Calendar.THURSDAY -> 4
        Calendar.FRIDAY -> 5
        Calendar.SATURDAY -> 6
        Calendar.SUNDAY -> 7
        else -> 1
    }

    fun calcWeek(): Int {
        val start = repository.getSemesterStartOrDefault()
        val days = ((System.currentTimeMillis() - start) / (1000 * 60 * 60 * 24)).toInt()
        return ((days / 7) + 1).coerceIn(1, maxWeek.coerceAtLeast(1))
    }

    val todayAbsolute = remember(refreshTrigger) { (calcWeek() - 1) * 7 + (todayDayOfWeek - 1) }
    var absoluteDay by remember(refreshTrigger) { mutableIntStateOf(todayAbsolute) }

    val currentWeek = (absoluteDay / 7) + 1
    val selectedDay = (absoluteDay % 7) + 1

    val displayCalendar = remember(absoluteDay, refreshTrigger) {
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, absoluteDay - todayAbsolute)
        }
    }

    val coursesForWeek = remember(currentWeek, refreshTrigger) { repository.getCoursesForWeek(currentWeek) }
    val dayCourses = coursesForWeek
        .filter { it.day == selectedDay }
        .sortedBy { it.start }

    val courseTimes = remember { try { repository.loadCourseTimes() } catch (_: Exception) { emptyList() } }

    // Class reminder: check if any course today starts within 30 minutes
    var showClassReminder by remember { mutableStateOf(false) }
    var reminderCourseName by remember { mutableStateOf("") }
    var reminderTimeText by remember { mutableStateOf("") }

    LaunchedEffect(refreshTrigger) {
        try {
            if (absoluteDay == todayAbsolute) {
                val now = Calendar.getInstance()
                val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
                val todayCourses = coursesForWeek.filter { it.day == todayDayOfWeek }
                for (course in todayCourses) {
                    val timeInfo = courseTimes.find { it.period == course.start }
                    if (timeInfo != null && timeInfo.startTime.isNotEmpty()) {
                        val parts = timeInfo.startTime.split(":")
                        if (parts.size >= 2) {
                            val courseMinutes = parts[0].toInt() * 60 + parts[1].toInt()
                            val diff = courseMinutes - currentMinutes
                            if (diff in 1..30) {
                                reminderCourseName = course.name
                                reminderTimeText = timeInfo.startTime
                                showClassReminder = true
                                break
                            }
                        }
                    }
                }
            }
        } catch (_: Exception) {}
    }

    val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
    val dayName = when (selectedDay) {
        1 -> "周一"
        2 -> "周二"
        3 -> "周三"
        4 -> "周四"
        5 -> "周五"
        6 -> "周六"
        7 -> "周日"
        else -> ""
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount < -50 && absoluteDay < maxWeek * 7 - 1) {
                        absoluteDay++ // 左滑→下一天
                    } else if (dragAmount > 50 && absoluteDay > 0) {
                        absoluteDay-- // 右滑→前一天
                    }
                }
            }
    ) {
        // Header with day navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (absoluteDay > 0) absoluteDay-- },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "前一天",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${dateFormat.format(displayCalendar.time)} $dayName",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "第 $currentWeek 周",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }

            IconButton(
                onClick = { if (absoluteDay < maxWeek * 7 - 1) absoluteDay++ },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "后一天",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        if (dayCourses.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${dayName}没有课，好好休息吧",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dayCourses) { course ->
                    val startTime = courseTimes.find { it.period == course.start }?.startTime
                    val endTime = courseTimes.find { it.period == course.end }?.endTime
                    val timeText = if (!startTime.isNullOrEmpty() && !endTime.isNullOrEmpty()) {
                        "$startTime-$endTime"
                    } else null
                    TodayCourseCard(course = course, timeText = timeText)
                }
            }
        }
    }

    // Class reminder dialog
    if (showClassReminder) {
        AlertDialog(
            onDismissRequest = { showClassReminder = false },
            title = { Text("课程提醒") },
            text = {
                Text(
                    "【$reminderCourseName】即将在 $reminderTimeText 开始，请做好准备！",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showClassReminder = false }) {
                    Text("知道了")
                }
            }
        )
    }
}

@Composable
private fun TodayCourseCard(course: Course, timeText: String?, modifier: Modifier = Modifier) {
    val courseColor = try {
        Color(android.graphics.Color.parseColor(course.color))
    } catch (e: Exception) {
        Color(0xFF4F46E5)
    }
    val tintedBg = courseColor.copy(alpha = 0.1f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(tintedBg)
            .padding(start = 0.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left color strip
        Box(
            modifier = Modifier
                .width(6.dp)
                .height(80.dp)
                .background(courseColor)
        )

        // Time column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(76.dp)
        ) {
            if (timeText != null) {
                val parts = timeText.split("-")
                Text(
                    text = parts[0],
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = courseColor,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = parts[1],
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = courseColor,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Course info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = course.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = courseColor
            )
            if (course.room.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = course.room,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }
            if (course.teacher.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = course.teacher,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
