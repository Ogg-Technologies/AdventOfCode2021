package utils

interface TileWorld<T> {
    operator fun get(pos: Vector): T
    operator fun set(pos: Vector, value: T)
}
