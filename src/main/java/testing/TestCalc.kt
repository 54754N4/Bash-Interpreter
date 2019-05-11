package testing

import calculator.grammar.Interpreter
import calculator.grammar.Lexer
import calculator.grammar.Parser

fun main() {
    while (true) {
        print("calc> ")
        println(calculate(readLine()!!))
    }
}

fun calculate(input: String): Double = Interpreter(Parser(Lexer(input))).interpret()

