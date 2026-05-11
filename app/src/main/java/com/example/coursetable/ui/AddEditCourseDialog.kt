package com.example.coursetable.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coursetable.data.Course

private val courseColors = listOf(
    "#4F46E5", "#7C3AED", "#DB2777", "#DC2626",
    "#EA580C", "#D97706", "#65A30D", "#059669",
    "#0891B2", "#2563EB", "#6366F1", "#8B5CF6"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditCourseDialog(
    existingCourse: Course? = null,
    onDismiss: () -> Unit,
    onConfirm: (Course) -> Unit
) {
    var name by remember { mutableStateOf(existingCourse?.name ?: "") }
    var day by remember { mutableIntStateOf(existingCourse?.day ?: 1) }
    var start by remember { mutableStateOf(existingCourse?.start?.toString() ?: "1") }
    var end by remember { mutableStateOf(existingCourse?.end?.toString() ?: "2") }
    var room by remember { mutableStateOf(existingCourse?.room ?: "") }
    var teacher by remember { mutableStateOf(existingCourse?.teacher ?: "") }
    var weeks by remember { mutableStateOf(existingCourse?.weeks ?: "1-16") }
    var type by remember { mutableStateOf(existingCourse?.type ?: "讲课") }
    var color by remember { mutableStateOf(existingCourse?.color ?: courseColors[0]) }
    var error by remember { mutableStateOf<String?>(null) }

    var dayExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    val dayNames = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
    val typeOptions = listOf("讲课", "实验", "实践")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existingCourse != null) "编辑课程" else "添加课程",
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("课程名称") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // 星期选择
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = dayNames[day - 1],
                            onValueChange = {},
                            label = { Text("星期") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { dayExpanded = true },
                            readOnly = true,
                            enabled = false
                        )
                        DropdownMenu(
                            expanded = dayExpanded,
                            onDismissRequest = { dayExpanded = false }
                        ) {
                            dayNames.forEachIndexed { index, dayName ->
                                DropdownMenuItem(
                                    text = { Text(dayName) },
                                    onClick = {
                                        day = index + 1
                                        dayExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 节次选择
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = start,
                        onValueChange = { start = it.filter { c -> c.isDigit() } },
                        label = { Text("开始节") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = end,
                        onValueChange = { end = it.filter { c -> c.isDigit() } },
                        label = { Text("结束节") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("教室") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = teacher,
                    onValueChange = { teacher = it },
                    label = { Text("教师") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = weeks,
                    onValueChange = { weeks = it },
                    label = { Text("周数 (如: 1-16, 1-12(单))") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // 课程类型选择
                Box {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        label = { Text("课程类型") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { typeExpanded = true },
                        readOnly = true,
                        enabled = false
                    )
                    DropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        typeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    type = option
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                // 颜色选择
                Text(
                    text = "选择颜色",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    courseColors.forEach { colorHex ->
                        val c = Color(android.graphics.Color.parseColor(colorHex))
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(c)
                                .then(
                                    if (color == colorHex) {
                                        Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable { color = colorHex }
                        )
                    }
                }

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
                if (name.isBlank()) {
                    error = "请输入课程名称"
                    return@TextButton
                }
                val startNum = start.toIntOrNull()
                val endNum = end.toIntOrNull()
                if (startNum == null || startNum < 1) {
                    error = "请输入有效的开始节次"
                    return@TextButton
                }
                if (endNum == null || endNum < startNum) {
                    error = "结束节次不能小于开始节次"
                    return@TextButton
                }
                if (weeks.isBlank()) {
                    error = "请输入周数"
                    return@TextButton
                }

                val course = Course(
                    id = existingCourse?.id ?: System.currentTimeMillis(),
                    name = name.trim(),
                    day = day,
                    start = startNum,
                    end = endNum,
                    room = room.trim(),
                    teacher = teacher.trim(),
                    weeks = weeks.trim(),
                    type = type,
                    color = color
                )
                onConfirm(course)
            }) {
                Text(if (existingCourse != null) "保存" else "添加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
