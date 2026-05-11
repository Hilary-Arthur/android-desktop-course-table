package com.example.coursetable.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coursetable.data.Course
import com.example.coursetable.data.CourseRepository
import com.example.coursetable.util.WeekParser

@Composable
fun CourseTableScreen(repository: CourseRepository) {
    val allCourses = remember { repository.loadCourses() }
    val maxWeek = remember { WeekParser.getMaxWeek(allCourses) }
    var currentWeek by remember { mutableIntStateOf(1) }
    val coursesForWeek = remember(currentWeek) { repository.getCoursesForWeek(currentWeek) }

    val days = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
    val maxPeriod = 18
    val periodHeight = 60.dp
    val dayWidth = 90.dp
    val timeColumnWidth = 36.dp

    Column(modifier = Modifier.fillMaxSize()) {
        WeekSelector(
            currentWeek = currentWeek,
            maxWeek = maxWeek,
            onWeekChange = { currentWeek = it }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
        ) {
            Column {
                Row {
                    Box(
                        modifier = Modifier
                            .width(timeColumnWidth)
                            .height(36.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .border(0.5.dp, Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "节次",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                    days.forEach { day ->
                        Box(
                            modifier = Modifier
                                .width(dayWidth)
                                .height(36.dp)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .border(0.5.dp, Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                for (period in 1..maxPeriod) {
                    Row {
                        Box(
                            modifier = Modifier
                                .width(timeColumnWidth)
                                .height(periodHeight)
                                .background(MaterialTheme.colorScheme.surface)
                                .border(0.5.dp, Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$period",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }

                        for (day in 1..7) {
                            val coursesInCell = coursesForWeek.filter {
                                it.day == day && it.start <= period && it.end >= period
                            }
                            Box(
                                modifier = Modifier
                                    .width(dayWidth)
                                    .height(periodHeight)
                                    .border(0.5.dp, Color.LightGray)
                            ) {
                                if (coursesInCell.isNotEmpty()) {
                                    val course = coursesInCell.first()
                                    if (course.start == period) {
                                        val spanHeight = (course.end - course.start + 1)
                                        Box(
                                            modifier = Modifier
                                                .width(dayWidth)
                                                .height(periodHeight * spanHeight)
                                        ) {
                                            CourseCard(course = course)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        StatusBar(currentWeek = currentWeek)
    }
}
