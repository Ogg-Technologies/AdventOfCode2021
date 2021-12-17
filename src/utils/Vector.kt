package utils

import kotlin.math.*

data class Vector(
    val x: Double,
    val y: Double
) {
    constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())

    companion object {
        fun fromPolar(angle: Number, dist: Number): Vector =
            Vector(cos(angle.toDouble()), sin(angle.toDouble())) * dist

        val zero = Vector(0, 0)
    }

    val dist: Double by lazy { hypot(x, y) }
    val squaredDist: Double by lazy { x * x + y * y }
    val angle: Double by lazy { atan2(y, x) }

    operator fun plus(v: Vector): Vector = Vector(x + v.x, y + v.y)
    operator fun minus(v: Vector): Vector = Vector(x - v.x, y - v.y)
    operator fun unaryMinus(): Vector = Vector(-x, -y)
    operator fun times(k: Number): Vector = Vector(x * k.toDouble(), y * k.toDouble())
    operator fun div(k: Number): Vector = Vector(x / k.toDouble(), y / k.toDouble())

    fun withDistance(newDistance: Number): Vector {
        check(dist != 0.0) { "Zero vector: $this cannot be projected to another distance" }
        return this * (newDistance.toDouble() / dist)
    }

    fun clampDistance(maxDistance: Number): Vector {
        return if (dist <= maxDistance.toDouble()) {
            this
        } else {
            withDistance(maxDistance)
        }
    }

    fun withAngle(newAngle: Number): Vector = fromPolar(newAngle.toDouble(), dist)
    fun rotated(deltaAngle: Number): Vector =
        if (deltaAngle == 0) this else withAngle(angle + deltaAngle.toDouble())

    fun withX(newX: Number): Vector = Vector(newX, y)
    fun withY(newY: Number): Vector = Vector(x, newY)

    fun asUnitVector() = withDistance(1.0)

    fun rounded(): Vector = Vector(intX, intY)

    val intX: Int get() = x.roundToInt()
    val intY: Int get() = y.roundToInt()

    val floatX: Float get() = x.toFloat()
    val floatY: Float get() = y.toFloat()
}

fun Pair<Number, Number>.toVector() = Vector(first, second)
