package com.example.coursetable.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillGuideScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val skillPrompt = remember {
        """
请将这张课表截图转换为以下JSON格式。

## 格式要求
- 输出纯JSON数组，不要markdown代码块包裹
- 每条记录包含：name(课程名), day(1-7,周一到周日), start(开始节次), end(结束节次), room(教室), teacher(教师), weeks(周次范围), type(讲课/实验/实践), color(十六进制颜色)
- 同一门课不同时间段拆分为多条记录，name保持一致
- 同一门课使用相同颜色
- weeks格式示例："1-16", "2-13", "1-9,11-16", "1-15(单)", "6,11"

## 示例
[
  {"name":"高等数学","day":1,"start":1,"end":2,"room":"A3-1103","teacher":"张三","weeks":"1-16","type":"讲课","color":"#4F46E5"},
  {"name":"高等数学","day":3,"start":3,"end":4,"room":"A3-1103","teacher":"张三","weeks":"1-16","type":"讲课","color":"#4F46E5"},
  {"name":"大学英语","day":2,"start":1,"end":2,"room":"B2-205","teacher":"李四","weeks":"1-16","type":"讲课","color":"#3B82F6"},
  {"name":"程序设计基础","day":2,"start":3,"end":4,"room":"C1-301","teacher":"王五","weeks":"1-16","type":"讲课","color":"#10B981"},
  {"name":"程序设计基础实验","day":5,"start":3,"end":4,"room":"D4-机房02","teacher":"王五","weeks":"2-15","type":"实验","color":"#10B981"}
]

## 注意事项
- 同一门课在不同时间上课，应拆分为多条独立记录，name保持一致
- 同一门课的所有记录使用相同颜色
- start和end是节次编号(1-18)，不是时间
- 如果学校一节大课占2个小节，如实填写起止节次

请直接输出JSON，不要任何其他文字。
        """.trimIndent()
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("获取课表JSON") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Step 1: Download prompt
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "第一步：获取AI提示词",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "点击下方按钮复制提示词到剪贴板，然后粘贴给AI（如ChatGPT、Claude、Gemini等）",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(skillPrompt))
                            Toast.makeText(context, "提示词已复制到剪贴板", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                        Text("  复制提示词")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step 2: Upload screenshot
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "第二步：发送课表给AI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "将你的课表截图或文本发送给AI，并附上刚才复制的提示词。AI会帮你转换成标准JSON格式。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step 3: Copy result
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "第三步：导入JSON",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "复制AI返回的JSON结果，返回上一页点击「导入课表」粘贴即可。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Preview section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "提示词预览",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = skillPrompt,
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        readOnly = true,
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
