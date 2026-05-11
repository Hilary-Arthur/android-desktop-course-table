package com.example.coursetable

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.coursetable.data.CourseRepository
import com.example.coursetable.ui.CourseTableScreen
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 检查是否是首次启动，询问是否删除APK
        checkAndAskDeleteApk()

        val repository = CourseRepository(this)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CourseTableScreen(repository = repository)
                }
            }
        }
    }

    private fun checkAndAskDeleteApk() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val hasAskedDelete = prefs.getBoolean("has_asked_delete_apk", false)

        if (!hasAskedDelete) {
            // 标记已询问过
            prefs.edit().putBoolean("has_asked_delete_apk", true).apply()

            // 延迟显示对话框，确保Activity已完全初始化
            window.decorView.post {
                showDeleteApkDialog()
            }
        }
    }

    private fun showDeleteApkDialog() {
        AlertDialog.Builder(this)
            .setTitle("安装完成")
            .setMessage("应用已成功安装，是否删除安装包以节省存储空间？")
            .setPositiveButton("删除") { _, _ ->
                deleteApkFiles()
            }
            .setNegativeButton("保留") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun deleteApkFiles() {
        // 获取存储权限（Android 11+需要）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                } catch (e: Exception) {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    startActivity(intent)
                }
                return
            }
        }

        // 删除下载目录中的APK文件
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        deleteApkInDirectory(downloadsDir)

        // 删除应用私有目录中的APK
        val appDir = getExternalFilesDir(null)
        deleteApkInDirectory(appDir)

        // 删除常见的APK存储位置
        val commonDirs = listOf(
            File("/storage/emulated/0/Download"),
            File("/storage/emulated/0/Downloads"),
            File("/sdcard/Download"),
            File("/sdcard/Downloads")
        )
        commonDirs.forEach { deleteApkInDirectory(it) }

        android.widget.Toast.makeText(this, "安装包清理完成", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun deleteApkInDirectory(directory: File?) {
        if (directory == null || !directory.exists() || !directory.isDirectory) return

        val packageName = packageName
        directory.listFiles()?.forEach { file ->
            if (file.isFile && file.name.endsWith(".apk") &&
                (file.name.contains("课程表") || file.name.contains("course") ||
                 file.name.contains(packageName) || file.name.contains("v3.6") ||
                 file.name.contains("v3.5") || file.name.contains("v3.4"))) {
                try {
                    file.delete()
                } catch (_: Exception) {
                    // 忽略删除失败的情况
                }
            }
        }
    }
}
