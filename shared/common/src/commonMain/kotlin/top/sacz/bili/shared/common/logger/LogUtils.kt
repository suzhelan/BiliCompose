package top.sacz.bili.shared.common.logger

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import okio.FileSystem
import okio.SYSTEM
import okio.buffer
import okio.use

object LogUtils {
    private val fs = FileSystem.SYSTEM
    private val dir = FileSystem.SYSTEM_TEMPORARY_DIRECTORY
    private val debugSuffix = "debug.log"
    private val warningSuffix = "warning.log"
    private val errorSuffix = "error.log"
    private val infoSuffix = "info.log"

    init {
        Napier.base(DebugAntilog())
    }
    fun d(message: Any) {
        d("Default", message)
    }

    fun d(tag: String = "Default", message: Any) {
        Napier.d(message.toString(), null, tag)
        fs.appendingSink(dir / "$tag-$debugSuffix").buffer().use { sink ->
            sink.writeUtf8(message.toString())
            sink.writeUtf8("\n")
        }
    }

    fun i(message: Any) {
        i("Default", message)
    }

    fun i(tag: String = "Default", message: Any) {
        Napier.i(message.toString(), null, tag)
        fs.appendingSink(dir / "$tag-$infoSuffix").buffer().use { sink ->
            sink.writeUtf8(message.toString())
            sink.writeUtf8("\n")
        }
    }

    fun w(message: Any) {
        w("Default", message)
    }

    fun w(tag: String = "Default", message: Any) {
        Napier.w(message.toString(), null, tag)
        fs.appendingSink(dir / "$tag-$warningSuffix").buffer().use { sink ->
            sink.writeUtf8(message.toString())
            sink.writeUtf8("\n")
        }
    }


    fun e(message: Any) {
        e("Default", message.toString(), Throwable())
    }

    fun e(tag: String, throwable: Throwable) {
        e(tag, message = "Error", throwable = throwable)
    }

    fun e(tag: String = "Default", message: Any = "Error", throwable: Throwable) {
        Napier.e(message.toString(), throwable, tag)
        fs.appendingSink(dir / "$tag-$errorSuffix").buffer().use { sink ->
            sink.writeUtf8(message.toString())
            sink.writeUtf8("\n")
            sink.writeUtf8(throwable.stackTraceToString())
            sink.writeUtf8("\n")
        }
    }
}