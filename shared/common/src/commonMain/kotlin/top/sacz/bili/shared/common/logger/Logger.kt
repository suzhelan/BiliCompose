package top.sacz.bili.shared.common.logger

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM

object Logger {
    init {
        Napier.base(
            DebugAntilog()
        )
    }

    private val fs = FileSystem.SYSTEM
    private val dir = FileSystem.SYSTEM_TEMPORARY_DIRECTORY
    private val debugPath = dir / "debug.log".toPath()
    private val warningPath = dir / "warning.log".toPath()
    private val errorPath = dir / "error.log".toPath()
    private val infoPath = dir / "info.log".toPath()

    fun d(message: String) {
        d("Default", message)
    }

    fun d(tag: String = "Default", message: String) {
        Napier.d(message, null, tag)
        fs.write(debugPath) {
            writeUtf8(message)
            writeUtf8("\n")
        }
    }

    fun i(tag: String = "Default", message: String) {
        Napier.i(message, null, tag)
        fs.write(infoPath) {
            writeUtf8(message)
            writeUtf8("\n")
        }
    }

    fun w(tag: String = "Default", message: String) {
        Napier.w(message, null, tag)
        fs.write(warningPath) {
            writeUtf8(message)
            writeUtf8("\n")
        }
    }

    fun e(tag: String = "Default", throwable: Throwable) {
        Napier.e("Error", throwable, tag)
        fs.write(errorPath) {
            writeUtf8(throwable.stackTraceToString())
            writeUtf8("\n")
        }
    }

    fun e(tag: String = "Default", message: String, throwable: Throwable) {
        Napier.e(message, throwable, tag)
        fs.write(errorPath) {
            writeUtf8(message)
            writeUtf8("\n")
            writeUtf8(throwable.stackTraceToString())
            writeUtf8("\n")
        }
    }
}