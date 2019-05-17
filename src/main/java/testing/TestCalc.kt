package testing

import interpreters.calculator.grammar.Interpreter
import interpreters.calculator.grammar.Lexer
import interpreters.calculator.grammar.Parser

fun main() {
    while (true) {
        print("calc> ")
        println(calculate(readLine()!!))
    }
}

private fun calculate(input: String): Double = Interpreter(Parser(Lexer(input))).interpret()

