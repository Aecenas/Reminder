package com.ain.reminder.updates

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val versionName: String,
    val releaseName: String,
    val releaseUrl: String,
    val apkName: String,
    val apkDownloadUrl: String
)

sealed class UpdateCheckResult {
    data class Available(val currentVersion: String, val update: UpdateInfo) : UpdateCheckResult()
    data class UpToDate(val currentVersion: String, val latestVersion: String) : UpdateCheckResult()
    data class Failed(val message: String) : UpdateCheckResult()
}

sealed class DownloadProgress {
    data class Running(val downloadedBytes: Long, val totalBytes: Long) : DownloadProgress()
    data object Complete : DownloadProgress()
    data class Failed(val reason: String) : DownloadProgress()
}

object GitHubUpdateService {
    private const val LATEST_RELEASE_URL = "https://api.github.com/repos/Aecenas/Reminder/releases/latest"

    suspend fun checkLatest(context: Context): UpdateCheckResult = withContext(Dispatchers.IO) {
        runCatching {
            val currentVersion = currentVersionName(context)
            val json = fetchLatestReleaseJson()
            val tagName = json.optString("tag_name").trim()
            val releaseName = json.optString("name").ifBlank { tagName.ifBlank { "最新版本" } }
            val latestVersion = tagName.removePrefix("v").removePrefix("V").ifBlank { releaseName }
            val releaseUrl = json.optString("html_url")
            val assets = json.optJSONArray("assets")
            val apkAsset = (0 until (assets?.length() ?: 0))
                .asSequence()
                .map { assets!!.getJSONObject(it) }
                .firstOrNull { it.optString("name").endsWith(".apk", ignoreCase = true) }
                ?: return@withContext UpdateCheckResult.Failed("最新 Release 里没有找到 APK 文件。")

            val update = UpdateInfo(
                versionName = latestVersion,
                releaseName = releaseName,
                releaseUrl = releaseUrl,
                apkName = apkAsset.optString("name").ifBlank { "Reminder-$latestVersion.apk" },
                apkDownloadUrl = apkAsset.optString("browser_download_url")
            )

            if (isNewerVersion(latestVersion, currentVersion)) {
                UpdateCheckResult.Available(currentVersion, update)
            } else {
                UpdateCheckResult.UpToDate(currentVersion, latestVersion)
            }
        }.getOrElse {
            UpdateCheckResult.Failed("检查失败，请稍后重试。")
        }
    }

    fun enqueueApkDownload(context: Context, update: UpdateInfo): Long {
        val request = DownloadManager.Request(Uri.parse(update.apkDownloadUrl))
            .setTitle("该吃药啦 ${update.versionName}")
            .setDescription("正在下载新版安装包")
            .setMimeType("application/vnd.android.package-archive")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, update.apkName)

        return context.getSystemService(DownloadManager::class.java).enqueue(request)
    }

    fun observeDownload(context: Context, downloadId: Long): Flow<DownloadProgress> = flow {
        val manager = context.getSystemService(DownloadManager::class.java)
        val query = DownloadManager.Query().setFilterById(downloadId)
        while (true) {
            val progress = manager.query(query)?.use { cursor ->
                if (!cursor.moveToFirst()) return@use DownloadProgress.Failed("下载任务不存在。")
                when (cursor.intColumn(DownloadManager.COLUMN_STATUS)) {
                    DownloadManager.STATUS_SUCCESSFUL -> DownloadProgress.Complete
                    DownloadManager.STATUS_FAILED -> DownloadProgress.Failed("下载失败，请稍后重试。")
                    else -> DownloadProgress.Running(
                        downloadedBytes = cursor.longColumn(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR),
                        totalBytes = cursor.longColumn(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                    )
                }
            } ?: DownloadProgress.Failed("无法读取下载进度。")
            emit(progress)
            if (progress is DownloadProgress.Complete || progress is DownloadProgress.Failed) break
            delay(500)
        }
    }.flowOn(Dispatchers.IO)

    fun openDownloadedApkInstaller(context: Context, downloadId: Long): Boolean {
        val manager = context.getSystemService(DownloadManager::class.java)
        val uri = manager.getUriForDownloadedFile(downloadId) ?: return false
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return runCatching {
            context.startActivity(intent)
            true
        }.getOrDefault(false)
    }

    fun installPermissionIntent(context: Context): Intent? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = Uri.parse("package:${context.packageName}")
            }
        } else {
            null
        }

    fun currentVersionName(context: Context): String {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        @Suppress("DEPRECATION")
        return packageInfo.versionName ?: "0"
    }

    private fun fetchLatestReleaseJson(): JSONObject {
        val connection = (URL(LATEST_RELEASE_URL).openConnection() as HttpURLConnection).apply {
            connectTimeout = 10_000
            readTimeout = 10_000
            requestMethod = "GET"
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("User-Agent", "Reminder-Android")
        }
        return connection.inputStream.bufferedReader().use { reader ->
            JSONObject(reader.readText())
        }
    }

    private fun isNewerVersion(latest: String, current: String): Boolean {
        val latestParts = latest.versionNumbers()
        val currentParts = current.versionNumbers()
        val maxSize = maxOf(latestParts.size, currentParts.size)
        for (index in 0 until maxSize) {
            val left = latestParts.getOrElse(index) { 0 }
            val right = currentParts.getOrElse(index) { 0 }
            if (left != right) return left > right
        }
        return false
    }

    private fun String.versionNumbers(): List<Int> =
        Regex("\\d+").findAll(this).map { it.value.toIntOrNull() ?: 0 }.toList().ifEmpty { listOf(0) }

    private fun Cursor.intColumn(name: String): Int = getInt(getColumnIndexOrThrow(name))

    private fun Cursor.longColumn(name: String): Long = getLong(getColumnIndexOrThrow(name))
}
