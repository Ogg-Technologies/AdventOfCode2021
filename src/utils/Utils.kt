package utils

fun <K, V> memoize(func: (K) -> V): (K) -> V {
    val cache = mutableMapOf<K, V>()
    return { key ->
        cache.getOrPut(key) { func(key) }
    }
}