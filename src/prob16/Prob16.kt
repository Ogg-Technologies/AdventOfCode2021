package prob16

import java.io.File

sealed class Packet(open val version: Int, open val type: Int)
data class Operator(override val version: Int, override val type: Int, val packets: List<Packet>) :
    Packet(version, type)

data class Literal(override val version: Int, override val type: Int, val value: Long) : Packet(version, type)

data class ConsumableString(val value: String, var index: Int = 0) {
    fun consume(length: Int): String {
        index += length
        return value.substring(index - length, index)
    }

    fun remainingString(): String = value.substring(index)
}

fun String.asConsumable() = ConsumableString(this)

fun Packet.versionSum(): Int = when (this) {
    is Operator -> packets.sumOf { it.versionSum() } + version
    is Literal -> version
}

fun Packet.evaluate(): Long {
    if (this is Literal) return value

    val operator = this as Operator
    val values = operator.packets.map { it.evaluate() }
    when (operator.type) {
        0 -> return values.sum()
        1 -> return values.reduce(Long::times)
        2 -> return values.minOf { it }
        3 -> return values.maxOf { it }
        5 -> return if (values[0] > values[1]) 1 else 0
        6 -> return if (values[0] < values[1]) 1 else 0
        7 -> return if (values[0] == values[1]) 1 else 0
    }

    error("Unknown operator type: ${operator.type}")
}

fun main() {
    //val input = "D2FE28"
    //val input = "38006F45291200"
    //val input = "EE00D40C823060"
    //val input = "8A004A801A8002F478"
    //val input = "A0016C880162017C3686B18A3D4780"
    //val input = "9C0141080250320F1802104A08"
    val input = File("src/prob16/prob16Input.txt").readText()

    val consumableString = input.hexToBitString().asConsumable()
    val packet = consumableString.parsePacket()
    println(packet)
    println(consumableString.remainingString())
    println(packet.versionSum())
    println(packet.evaluate())
}

fun String.hexToBitString(): String {
    return this.fold(StringBuilder()) { sb, c ->
        val int = c.toString().toInt(16)
        val bin = Integer.toBinaryString(int)
        sb.append(bin.padStart(4, '0'))
    }.toString()
}

fun ConsumableString.parsePacket(): Packet {
    val version = consume(3).toInt(2)
    val type = consume(3).toInt(2)
    if (type == 4) return Literal(version, type, parseLiteralNumber())

    val lengthTypeId = consume(1)
    if (lengthTypeId == "0") {
        val subPacketsLength = consume(15).toInt(2)
        val subPacketEnd = index + subPacketsLength
        val parsedPackets = mutableListOf<Packet>()
        while (index < subPacketEnd) {
            parsedPackets.add(parsePacket())
        }
        return Operator(version, type, parsedPackets)
    } else {
        val numSubPackets = consume(11).toInt(2)
        val parsedPackets = mutableListOf<Packet>()
        repeat(numSubPackets) {
            parsedPackets.add(parsePacket())
        }
        return Operator(version, type, parsedPackets)
    }
}

private fun ConsumableString.parseLiteralNumber(): Long {
    val valueBuilder = StringBuilder()
    while (true) {
        val isLastGroup = consume(1) == "0"
        valueBuilder.append(consume(4))
        if (isLastGroup) break
    }
    return valueBuilder.toString().toLong(2)
}