package testing

import command.custom.Test
import interpreters.args.grammar.Parser

fun main() = testArgsCommand()

fun testArgsCommand() = println(Test("--dump --key=valye --a0=\"i - am a big -- string\" Hello, World!").launch().outputString())

fun testArgsParsing() {
    val input = " -n --named_no_value --named=\"with value\" and extra input"
    val parser = Parser()
    parser.parse(input)
    parser.parse(input)
    println(parser.parse(input))
}
