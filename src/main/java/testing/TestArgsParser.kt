package testing

import command.custom.Test
import interpreters.args.grammar.Parser
import kotlin.math.sqrt

fun main() {
    var duration = System.currentTimeMillis()
    print(testStupidPrime(9223372036854775783))
    println(" "+(System.currentTimeMillis() - duration))
    duration = System.currentTimeMillis()
    print(testPrime(9223372036854775783))
    println(" "+(System.currentTimeMillis() - duration))
    //println(testStupidPrime(14231413412346))
//    val max:Double = Double.MAX_VALUE
//    println(max)
//    var prime = max.toLong()
//    while (!testPrime(prime))
//        prime--
//    println(prime)
}  //testArgsCommand()

fun testArgsCommand() = println(Test("--dump --key=valye --a0=\"i - am a big -- string\" Hello, World!").launch().outputString())

fun testArgsParsing() {
    val input = " -n --named_no_value --named=\"with value\" and extra input"
    val parser = Parser(input)
    println(parser.parse())
}

fun testStupidPrime(num: Long): Boolean {
    for (i in 2 until num)
        if (num%i==0L)
            return false
    return true
}

fun testPrime(num: Long): Boolean {
    val sqrt = sqrt(num.toDouble()).toLong()
    if (num%2L == 0L) return false
    for (i in 3.toLong()..sqrt step 2)
        if (num%i == 0L)
            return false
    return true
}