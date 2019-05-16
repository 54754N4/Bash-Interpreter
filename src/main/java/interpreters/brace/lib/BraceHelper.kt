package interpreters.brace.lib

import interpreters.brace.grammar.Token
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException

fun main() {
    println(generateIntRange("10", "0001", "2"))
    println(generateCharRange("z", "a", "1"))
    println(isZeroPadded("002"))
}

fun generateRange(start: Token, end: Token, inc: Token?): List<String>? {
    return if (isInt(start.value) && isInt(end.value))
        generateIntRange(start.value, end.value, inc?.value)
    else if (start.value[0].isLetter() && end.value[0].isLetter())
        generateCharRange(start.value, end.value, inc?.value)
    else
        null
}

private fun generateCharRange(start: String, end: String, inc: String?): List<String> {
    val inc = inc?.toInt() ?: 1
    return if (start[0] < end[0]) (start[0] .. end[0] step inc).map { it.toString() }
    else (start[0] downTo end[0] step inc).map { it.toString() }
}

private fun generateIntRange(start: String, end: String, inc: String?): List<String> {
    val range = intRange(start.toInt(), end.toInt(), inc?.toInt() ?: 1)
    val max = Math.max(start.length, end.length)
    if (isZeroPadded(start) || isZeroPadded(end))
        return padZeros(max, range)
    return range
}

private fun intRange(start: Int, end: Int, inc: Int): List<String> {
    return if (start < end) (start .. end step inc).map { it.toString() }
    else (start downTo end step inc).map{ it.toString() }
}

private fun padZeros(size: Int, range: List<String>): List<String> = range.map { string -> string.padStart(size, '0')}

private fun isZeroPadded(string: String): Boolean {
    if (!isInt(string))
        throw IllegalArgumentException("$string is not even an int bro")
    return (string != string.toInt().toString())
}

private fun isInt(string: String): Boolean {
    return try {
        string.toInt()
        true
    }
    catch (e: NumberFormatException) {false}
}