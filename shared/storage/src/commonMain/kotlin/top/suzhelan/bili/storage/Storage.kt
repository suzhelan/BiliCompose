package top.suzhelan.bili.storage


import com.russhwolf.settings.contains
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import top.suzhelan.bili.storage.config.SettingsConfig.factor

/**
 * 存储配置类
 */
class Storage(private val database: String) {
    val settings = factor.create(database)

    val json = Json {
        //忽略未知jsonKey
        ignoreUnknownKeys = true
        //是否将null的属性写入json 默认true
        explicitNulls = true
        //是否使用默认值 默认false
        encodeDefaults = true
        //是否格式化json
        prettyPrint = true
        //宽容解析模式 可以解析不规范的json格式
        isLenient = false
        //解析失败按null值
        coerceInputValues = true
    }
    
    fun hasKey(key: String): Boolean = settings.hasKey(key)

    fun remove(key: String) {
        settings.remove(key)
    }

    fun clear() {
        settings.clear()
    }

    /**
     * 获取对象
     * @param key 键
     * @param default 默认值
     * @return 对象
     */
    inline fun <reified T> getObject(key: String, default: T): T {
        val defaultValue = json.encodeToString(default)
        return json.decodeFromString(settings.getString(key, defaultValue))
    }

    /**
     * 获取对象
     * @param serializer 序列化器 传入可处理更复杂的泛型场景和多态
     * @param key 键
     * @param default 默认值
     * @return 对象
     */
    inline fun <reified T> getObject(serializer: KSerializer<T>, key: String, default: T): T {
        return json.decodeFromString(
            serializer,
            settings.getString(key, json.encodeToString(default))
        )
    }

    /**
     * 获取对象
     * @param key 键
     * @return 对象
     */
    inline fun <reified T> getObjectOrNull(key: String): T? {
        if (!settings.contains(key)) {
            return null
        }
        return json.decodeFromString(settings.getStringOrNull(key)!!)
    }

    /**
     * 获取对象
     * @param serializer 序列化器 传入可处理更复杂的泛型场景和多态
     * @param key 键
     * @return 对象
     */
    inline fun <reified T> getObjectOrNull(serializer: KSerializer<T>, key: String): T? {
        if (!settings.contains(key)) {
            return null
        }
        return json.decodeFromString(serializer, settings.getStringOrNull(key)!!)
    }

    /**
     * 存储对象
     * @param key 键
     * @param value 值
     */
    inline fun <reified T> putObject(key: String, value: T) {
        settings.putString(key, json.encodeToString(value))
    }

    /**
     * 存储对象
     * @param serializer 序列化器 传入可处理更复杂的泛型场景和多态
     * @param key 键
     * @param value 值
     */
    inline fun <reified T> putObject(serializer: KSerializer<T>, key: String, value: T) {
        settings.putString(key, json.encodeToString(serializer, value))
    }


    fun getString(key: String, default: String): String = settings.getString(key, default)
    fun getInt(key: String, default: Int): Int = settings.getInt(key, default)
    fun getLong(key: String, default: Long): Long = settings.getLong(key, default)
    fun getBoolean(key: String, default: Boolean): Boolean = settings.getBoolean(key, default)
    fun getFloat(key: String, default: Float): Float = settings.getFloat(key, default)
    fun getDouble(key: String, default: Double): Double = settings.getDouble(key, default)

    fun getStringOrNull(key: String): String? = settings.getStringOrNull(key)
    fun getIntOrNull(key: String): Int? = settings.getIntOrNull(key)
    fun getLongOrNull(key: String): Long? = settings.getLongOrNull(key)
    fun getBooleanOrNull(key: String): Boolean? = settings.getBooleanOrNull(key)
    fun getFloatOrNull(key: String): Float? = settings.getFloatOrNull(key)
    fun getDoubleOrNull(key: String): Double? = settings.getDoubleOrNull(key)


    fun putString(key: String, value: String) = settings.putString(key, value)
    fun putInt(key: String, value: Int) = settings.putInt(key, value)
    fun putLong(key: String, value: Long) = settings.putLong(key, value)
    fun putBoolean(key: String, value: Boolean) = settings.putBoolean(key, value)
    fun putFloat(key: String, value: Float) = settings.putFloat(key, value)
    fun putDouble(key: String, value: Double) = settings.putDouble(key, value)

}