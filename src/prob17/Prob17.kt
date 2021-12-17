package prob17

import utils.Vector
import utils.product
import utils.toVector


fun main() {

    //part1()
    part2()
}

data class Boundary(val minPos: Vector, val maxPos: Vector)

private fun part2() {
    //test target area: x=20..30, y=-10..-5
    //val boundary = Boundary(Vector(20, -10), Vector(30, -5))

    //real target area: x=241..275, y=-75..-49
    val boundary = Boundary(Vector(241, -75), Vector(275, -49))


    val startYSpeeds = boundary.minPos.intY..1500
    val startXSpeeds = 1..boundary.maxPos.intX+10
    val startVelocities = (startXSpeeds product startYSpeeds).map { it.toVector() }
    val validInitialVelocities = mutableListOf<Vector>()
    println(startVelocities.size)
    for (startVel in startVelocities) {
        if (isValidStartVel(startVel, boundary)) {
            validInitialVelocities.add(startVel)
        }
    }
    println("valid initial velocities: $validInitialVelocities")
    println("Number of valid initial velocities: ${validInitialVelocities.size}")

    println(isValidStartVel(Vector(7, 2), boundary))
}

private infix fun Vector.isWithin(boundary: Boundary): Boolean {
    return this.intX in boundary.minPos.intX..boundary.maxPos.intX &&
            this.intY in boundary.minPos.intY..boundary.maxPos.intY
}

private fun isValidStartVel(startVel: Vector, boundary: Boundary): Boolean {
    var vel = startVel
    var pos = Vector.zero
    while (pos.x <= boundary.maxPos.x && pos.y >= boundary.minPos.y) {
        pos += vel
        vel = Vector(if (vel.intX > 0) vel.intX-1 else vel.intX, vel.intY-1)
        if (pos isWithin boundary) return true
    }
    return false
}

private fun part1() {
    var highestAllowedYPos = 0

    for (startYSpeed in 0..1000) {
        var highestYPos = 0

        var ySpeed = startYSpeed
        var yPos = 0
        while (yPos > -100) {
            yPos += ySpeed
            ySpeed -= 1
            if (yPos > highestYPos) highestYPos = yPos

            if (yPos in -75..-49) {
                if (highestYPos > highestAllowedYPos) highestAllowedYPos = highestYPos
            }
        }
    }
    println(highestAllowedYPos)
}