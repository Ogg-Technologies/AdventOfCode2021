package utils

fun <K, V> memoize(func: (K) -> V): (K) -> V {
    val cache = mutableMapOf<K, V>()
    return { key ->
        cache.getOrPut(key) { func(key) }
    }
}

infix fun <T> Iterable<T>.product(other: Iterable<T>): Iterable<Pair<T, T>> =
    this.flatMap { first -> other.map { second -> first to second } }

fun <T> Iterable<T>.square(): Iterable<Pair<T, T>> = this product this

infix fun IntRange.product(other: IntRange): Iterable<Pair<Int, Int>> = this.toList() product other.toList()

fun IntRange.square(): Iterable<Pair<Int, Int>> = this product this