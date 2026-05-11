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
import java.io.File

class LaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val hasAskedDelete = prefs.getBoolean("has_asked_delete_apk", false)

        if (!hasAskedDelete) {
            prefs.edit().putBoolean("has_asked_delete_apk", true).apply()
            showDeleteApkDialog()
        } else {
            goToMain()
        }
    }

    private fun showDeleteApkDialog() {
        AlertDialog.Builder(this)
            .setTitle("安装完成")
            .setMessage("应用已成功安装，是否删除安装包以节省存储空间？")
            .setPositiveButton("删除") { _, _ ->
                deleteApkFiles()
                goToMain()
            }
            .setNegativeButton("保留") { _, _ ->
                goToMain()
            }
            .setCancelable(false)
            .show()
    }

    private fun deleteApkFiles() {
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

        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        deleteApkInDirectory(downloadsDir)

        val appDir = getExternalFilesDir(null)
        deleteApkInDirectory(appDir)

        val commonDirs = listOf(
            File("/storage/emulated/0/Download"),
            File("/storage/emulated/0/Downloads"),
            File("/sdcard/Download"),
            File("/sdcard/Downloads")
        )
        commonDirs.forEach { deleteApkInDirectory(it) }
    }

    private fun deleteApkInDirectory(directory: File?) {
        if (directory == null || !directory.exists() || !directory.isDirectory) return

        val packageName = packageName
        directory.listFiles()?.forEach { file ->
            if (file.isFile && file.name.endsWith(".apk") &&
                (file.name.contains("课程表") || file.name.contains("course") ||
                 file.name.contains(packageName) || file.name.contains("v4.1") ||
                 file.name.contains("v4.0") || file.name.contains("v3.6"))) {
                try {
                    file.delete()
                } catch (_: Exception) {
                }
            }
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
