package top.suzhelan.bili.storage.ext

import top.suzhelan.bili.storage.Storage


/** Equivalent to [Storage.contains] */
inline operator fun Storage.contains(key: String): Boolean = hasKey(key)

/** Equivalent to [Storage.remove] */
inline operator fun Storage.minusAssign(key: String): Unit = remove(key)

/** Equivalent to [Storage.getInt] */
inline operator fun Storage.get(key: String, defaultValue: Int): Int = getInt(key, defaultValue)

/** Equivalent to [Storage.getLong] */
inline operator fun Storage.get(key: String, defaultValue: Long): Long = getLong(key, defaultValue)

/** Equivalent to [Storage.getString] */
inline operator fun Storage.get(key: String, defaultValue: String): String =
    getString(key, defaultValue)

/** Equivalent to [Storage.getFloat] */
inline operator fun Storage.get(key: String, defaultValue: Float): Float =
    getFloat(key, defaultValue)

/** Equivalent to [Storage.getDouble] */
inline operator fun Storage.get(key: String, defaultValue: Double): Double =
    getDouble(key, defaultValue)

/** Equivalent to [Storage.getBoolean] */
inline operator fun Storage.get(key: String, defaultValue: Boolean): Boolean =
    getBoolean(key, defaultValue)

/** Equivalent to [Storage.putInt] */
inline operator fun Storage.set(key: String, value: Int): Unit = putInt(key, value)

/** Equivalent to [Storage.putLong] */
inline operator fun Storage.set(key: String, value: Long): Unit = putLong(key, value)

/** Equivalent to [Storage.putString] */
inline operator fun Storage.set(key: String, value: String): Unit = putString(key, value)

/** Equivalent to [Storage.putFloat] */
inline operator fun Storage.set(key: String, value: Float): Unit = putFloat(key, value)

/** Equivalent to [Storage.putDouble] */
inline operator fun Storage.set(key: String, value: Double): Unit = putDouble(key, value)

/** Equivalent to [Storage.putBoolean] */
inline operator fun Storage.set(key: String, value: Boolean): Unit = putBoolean(key, value)

/**
 * Get the typed value stored at [key] if present, or return null if not. Throws [IllegalArgumentException] if [T] is
 * not one of `Int`, `Long`, `String`, `Float`, `Double`, or `Boolean`.
 */
inline operator fun <reified T : Any> Storage.get(key: String): T? = when (T::class) {
    Int::class -> getIntOrNull(key) as T?
    Long::class -> getLongOrNull(key) as T?
    String::class -> getStringOrNull(key) as T?
    Float::class -> getFloatOrNull(key) as T?
    Double::class -> getDoubleOrNull(key) as T?
    Boolean::class -> getBooleanOrNull(key) as T?
    else -> getObjectOrNull(key)
}

/**
 * Stores a typed value at [key], or remove what's there if [value] is null. Throws [IllegalArgumentException] if [T] is
 * not one of `Int`, `Long`, `String`, `Float`, `Double`, or `Boolean`.
 */
inline operator fun <reified T : Any> Storage.set(key: String, value: T?): Unit =
    if (value == null) {
        this -= key
    } else when (T::class) {
        Int::class -> putInt(key, value as Int)
        Long::class -> putLong(key, value as Long)
        String::class -> putString(key, value as String)
        Float::class -> putFloat(key, value as Float)
        Double::class -> putDouble(key, value as Double)
        Boolean::class -> putBoolean(key, value as Boolean)
        else -> putObject(key, value)
    }

/** Equivalent to [Storage.remove] */
inline operator fun Storage.set(key: String, @Suppress("UNUSED_PARAMETER") value: Nothing?): Unit =
    remove(key)
