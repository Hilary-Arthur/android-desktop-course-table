package com.example.coursetable.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coursetable.data.CourseRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    repository: CourseRepository,
    modifier: Modifier = Modifier,
    onCoursesImported: () -> Unit = {},
    onSemesterStartChanged: () -> Unit = {}
) {
    var showImportDialog by remember { mutableStateOf(false) }
    var importJson by remember { mutableStateOf("") }
    var importError by remember { mutableStateOf<String?>(null) }
    var hasImported by remember { mutableStateOf(repository.hasImportedCourses()) }
    var semesterStart by remember { mutableStateOf(repository.getSemesterStart()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showSkillGuide by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "设置",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Import course card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "导入课表",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = if (hasImported) "已导入自定义课表，再次导入将覆盖当前课表" else "粘贴课表 JSON 文本来导入你的课程",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = {
                        importJson = ""
                        importError = null
                        showImportDialog = true
                    }) {
                        Text(if (hasImported) "重新导入" else "导入课表")
                    }

                    if (hasImported) {
                        OutlinedButton(onClick = {
                            repository.clearImportedCourses()
                            hasImported = false
                            onCoursesImported()
                        }) {
                            Text("恢复默认")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Skill guide card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "使用AI转换课表",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "如果你的课表是截图或文本格式，可以使用AI（如ChatGPT、Claude等）自动转换为JSON格式",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Button(
                    onClick = { showSkillGuide = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("查看转换教程")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Semester start date card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "学期起始日期",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = if (semesterStart != null) {
                        "当前设置：${dateFormat.format(Date(semesterStart!!))}"
                    } else {
                        "未设置，课程表将从第1周开始显示"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { showDatePicker = true }) {
                        Text(if (semesterStart != null) "修改日期" else "选择日期")
                    }

                    if (semesterStart != null) {
                        OutlinedButton(onClick = {
                            repository.setSemesterStart(0L)
                            semesterStart = null
                            onSemesterStartChanged()
                        }) {
                            Text("清除")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // School info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "学校信息",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = "当前版本：${repository.getSchoolName()}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Import dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("导入课表") },
            text = {
                Column {
                    Text(
                        text = "请粘贴课表 JSON 文本（格式为课程对象数组）",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = importJson,
                        onValueChange = {
                            importJson = it
                            importError = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        placeholder = { Text("[{\"name\":\"高等数学\",\"day\":1,\"start\":1,\"end\":2,\"weeks\":\"1-16\",...}]") },
                        isError = importError != null,
                        supportingText = importError?.let {
                            { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val trimmed = importJson.trim()
                    if (trimmed.isEmpty()) {
                        importError = "请输入 JSON 文本"
                        return@TextButton
                    }
                    try {
                        repository.saveImportedCourses(trimmed)
                        hasImported = true
                        showImportDialog = false
                        onCoursesImported()
                    } catch (e: Exception) {
                        importError = "JSON 格式错误：${e.message}"
                    }
                }) {
                    Text("导入")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // Date picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = semesterStart ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        repository.setSemesterStart(millis)
                        semesterStart = millis
                        onSemesterStartChanged()
                    }
                    showDatePicker = false
                }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Skill guide screen
    if (showSkillGuide) {
        SkillGuideScreen(
            onBack = { showSkillGuide = false }
        )
    }
}
