package com.example.coursetable.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coursetable.data.CourseRepository
import com.example.coursetable.data.Grade

@Composable
fun GradeScreen(
    repository: CourseRepository,
    modifier: Modifier = Modifier
) {
    var grades by remember { mutableStateOf(repository.loadGrades()) }
    var showAddDialog by remember { mutableStateOf(false) }
    val gpa = remember(grades) { Grade.calculateGPA(grades) }
    val totalCredits = remember(grades) { grades.sumOf { it.credit.toDouble() }.toFloat() }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // GPA Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "平均绩点",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = String.format("%.2f", gpa),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 56.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${grades.size}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "科目数",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = String.format("%.1f", totalCredits),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "总学分",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Grade List Header
            Text(
                text = "成绩列表",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (grades.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无成绩记录\n点击右下角 + 添加",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(grades) { grade ->
                        GradeCard(
                            grade = grade,
                            onDelete = {
                                repository.deleteGrade(grade.id)
                                grades = repository.loadGrades()
                            }
                        )
                    }
                    // Bottom spacing for FAB
                    item { Spacer(modifier = Modifier.height(80.dp)) }
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
            Icon(Icons.Default.Add, contentDescription = "添加成绩")
        }
    }

    // Add Grade Dialog
    if (showAddDialog) {
        AddGradeDialog(
            repository = repository,
            existingGrades = grades,
            onDismiss = { showAddDialog = false },
            onConfirm = { grade ->
                repository.saveGrade(grade)
                grades = repository.loadGrades()
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun GradeCard(
    grade: Grade,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradePointColor = when {
        grade.gradePoint >= 3.5f -> MaterialTheme.colorScheme.primary
        grade.gradePoint >= 2.5f -> MaterialTheme.colorScheme.tertiary
        grade.gradePoint >= 1.0f -> MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
        else -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Grade point circle
            Box(
                modifier = Modifier
                    .width(64.dp)
                    .height(64.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(gradePointColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format("%.1f", grade.gradePoint),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = gradePointColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Course info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = grade.courseName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "成绩：${grade.score}分",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "学分：${grade.credit}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Delete button
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun AddGradeDialog(
    repository: CourseRepository,
    existingGrades: List<Grade>,
    onDismiss: () -> Unit,
    onConfirm: (Grade) -> Unit
) {
    // Get unique course names from current courses
    val allCourses = remember { repository.loadCourses() }
    val courseNames = remember(allCourses) {
        allCourses.map { it.name }.distinct().sorted()
    }
    // Filter out courses that already have grades
    val availableCourses = remember(courseNames, existingGrades) {
        val gradedCourseNames = existingGrades.map { it.courseName }.toSet()
        courseNames.filter { it !in gradedCourseNames }
    }

    var selectedCourse by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var scoreText by remember { mutableStateOf("") }
    var creditText by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加成绩") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Course selection dropdown
                Box {
                    OutlinedTextField(
                        value = selectedCourse,
                        onValueChange = {},
                        label = { Text("选择课程") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "展开")
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                    ) {
                        if (availableCourses.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("所有课程已添加成绩") },
                                onClick = { expanded = false }
                            )
                        } else {
                            availableCourses.forEach { courseName ->
                                DropdownMenuItem(
                                    text = { Text(courseName) },
                                    onClick = {
                                        selectedCourse = courseName
                                        expanded = false
                                        error = null
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = scoreText,
                    onValueChange = { scoreText = it },
                    label = { Text("成绩（百分制）") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = creditText,
                    onValueChange = { creditText = it },
                    label = { Text("学分") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (selectedCourse.isBlank()) {
                    error = "请选择课程"
                    return@TextButton
                }
                val score = scoreText.toIntOrNull()
                if (score == null || score !in 0..100) {
                    error = "请输入有效成绩（0-100）"
                    return@TextButton
                }
                val credit = creditText.toFloatOrNull()
                if (credit == null || credit <= 0) {
                    error = "请输入有效学分"
                    return@TextButton
                }

                val grade = Grade(
                    courseName = selectedCourse,
                    score = score,
                    credit = credit,
                    gradePoint = Grade.calculateGradePoint(score)
                )
                onConfirm(grade)
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
