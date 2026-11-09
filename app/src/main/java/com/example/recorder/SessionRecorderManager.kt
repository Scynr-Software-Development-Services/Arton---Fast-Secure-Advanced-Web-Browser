package com.example.recorder

import android.graphics.Bitmap
import android.os.Build
import com.example.data.model.BugReport
import com.example.data.model.RecordedFrame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SessionRecorderManager {

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private val _recordedFrames = MutableStateFlow<List<RecordedFrame>>(emptyList())
    val recordedFrames: StateFlow<List<RecordedFrame>> = _recordedFrames.asStateFlow()

    private val _latestReport = MutableStateFlow<BugReport?>(null)
    val latestReport: StateFlow<BugReport?> = _latestReport.asStateFlow()

    private var startTimestamp = 0L

    fun startRecording(initialUrl: String) {
        _isRecording.value = true
        _isPaused.value = false
        _elapsedSeconds.value = 0
        _recordedFrames.value = emptyList()
        _latestReport.value = null
        startTimestamp = System.currentTimeMillis()

        recordStep(initialUrl, "Recording session started")
    }

    fun tickSecond() {
        if (_isRecording.value && !_isPaused.value) {
            _elapsedSeconds.value += 1
        }
    }

    fun togglePause() {
        if (_isRecording.value) {
            _isPaused.value = !_isPaused.value
        }
    }

    fun recordStep(url: String, note: String = "") {
        if (!_isRecording.value || _isPaused.value) return
        val currentList = _recordedFrames.value.toMutableList()
        currentList.add(
            RecordedFrame(
                frameIndex = currentList.size + 1,
                timestamp = System.currentTimeMillis(),
                url = url,
                note = note.ifEmpty { "Navigated to $url" }
            )
        )
        _recordedFrames.value = currentList
    }

    fun stopRecording(
        activeUrl: String,
        consoleErrorsCount: Int,
        userNotes: String = ""
    ): BugReport {
        _isRecording.value = false
        _isPaused.value = false

        val duration = _elapsedSeconds.value
        val frames = _recordedFrames.value

        val sysInfo = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT}) - Device: ${Build.MANUFACTURER} ${Build.MODEL}"

        val report = BugReport(
            timestamp = System.currentTimeMillis(),
            url = activeUrl,
            durationSeconds = duration,
            frameCount = frames.size,
            consoleErrorsCount = consoleErrorsCount,
            userNotes = userNotes.ifEmpty { "Arton workflow session recording" },
            systemInfo = sysInfo
        )

        _latestReport.value = report
        return report
    }

    fun clearReport() {
        _latestReport.value = null
    }

    fun formatDuration(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format(Locale.US, "%02d:%02d", mins, secs)
    }

    fun generateShareableSummary(report: BugReport): String {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateStr = df.format(Date(report.timestamp))

        val sb = StringBuilder()
        sb.append("=== ARTON BROWSER SESSION & BUG REPORT ===\n")
        sb.append("Date: $dateStr\n")
        sb.append("Duration: ${formatDuration(report.durationSeconds)}\n")
        sb.append("URL: ${report.url}\n")
        sb.append("Workflow Steps Captured: ${report.frameCount}\n")
        sb.append("Console Errors: ${report.consoleErrorsCount}\n")
        sb.append("Notes: ${report.userNotes}\n")
        sb.append("Environment: ${report.systemInfo}\n")
        sb.append("------------------------------------------\n")
        sb.append("Workflow Steps:\n")
        _recordedFrames.value.forEach { frame ->
            val stepTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(frame.timestamp))
            sb.append("• [$stepTime] ${frame.note} (${frame.url})\n")
        }
        sb.append("==========================================")
        return sb.toString()
    }
}
