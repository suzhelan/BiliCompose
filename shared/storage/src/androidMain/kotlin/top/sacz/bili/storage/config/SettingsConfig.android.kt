package top.sacz.bili.storage.config

import android.annotation.SuppressLint
import android.content.Context
import com.russhwolf.settings.Settings
import io.fastkv.FastKV

@SuppressLint("StaticFieldLeak")
actual object SettingsConfig {
    lateinit var context: Context

    actual val factor: Settings.Factory by lazy {
        FastKvSettingsFactory()
    }

    class FastKvSettingsFactory : Settings.Factory {
        override fun create(name: String?): Settings {
            return FastKvSettings(name ?: "default")
        }
    }

    class FastKvSettings(name: String) : Settings {
        private val fastKv = FastKV.Builder(context.filesDir.absolutePath, name)
            .build()

        override val keys: Set<String>
            get() = fastKv.all.keys

        override val size: Int
            get() = fastKv.all.size

        override fun clear() {
            fastKv.clear()
        }

        override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
            return fastKv.getBoolean(key, defaultValue)
        }

        override fun getBooleanOrNull(key: String): Boolean? {
            return if (fastKv.contains(key)) {
                fastKv.getBoolean(key)
            } else {
                null
            }
        }

        override fun getDouble(key: String, defaultValue: Double): Double {
            return fastKv.getDouble(key, defaultValue)
        }

        override fun getDoubleOrNull(key: String): Double? {
            return if (fastKv.contains(key)) {
                fastKv.getDouble(key)
            } else {
                null
            }
        }

        override fun getFloat(key: String, defaultValue: Float): Float {
            return fastKv.getFloat(key, defaultValue)
        }

        override fun getFloatOrNull(key: String): Float? {
            return if (fastKv.contains(key)) {
                fastKv.getFloat(key)
            } else {
                null
            }
        }

        override fun getInt(key: String, defaultValue: Int): Int {
            return fastKv.getInt(key, defaultValue)
        }

        override fun getIntOrNull(key: String): Int? {
            return if (fastKv.contains(key)) {
                fastKv.getInt(key)
            } else {
                null
            }
        }

        override fun getLong(key: String, defaultValue: Long): Long {
            return fastKv.getLong(key, defaultValue)
        }

        override fun getLongOrNull(key: String): Long? {
            return if (fastKv.contains(key)) {
                fastKv.getLong(key)
            } else {
                null
            }
        }

        override fun getString(key: String, defaultValue: String): String {
            return fastKv.getString(key, defaultValue)!!
        }

        override fun getStringOrNull(key: String): String? {
            return if (fastKv.contains(key)) {
                fastKv.getString(key)
            } else {
                null
            }
        }

        override fun hasKey(key: String): Boolean {
            return fastKv.contains(key)
        }

        override fun putBoolean(key: String, value: Boolean) {
            fastKv.putBoolean(key, value)
        }

        override fun putDouble(key: String, value: Double) {
            fastKv.putDouble(key, value)
        }

        override fun putFloat(key: String, value: Float) {
            fastKv.putFloat(key, value)
        }

        override fun putInt(key: String, value: Int) {
            fastKv.putInt(key, value)
        }

        override fun putLong(key: String, value: Long) {
            fastKv.putLong(key, value)
        }

        override fun putString(key: String, value: String) {
            fastKv.putString(key, value)
        }

        override fun remove(key: String) {
            fastKv.remove(key)
        }
    }
}