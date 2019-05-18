package command

import interpreters.bash.grammar.Type

val reserved = hashMapOf(
    "if" to Type.IF, "then" to Type.THEN, "else" to Type.ELSE, "elif" to Type.ELIF,
    "fi" to Type.FI, "case" to Type.CASE, "esac" to Type.ESAC, "for" to Type.FOR,
    "while" to Type.WHILE, "until" to Type.UNTIL, "do" to Type.DO, "done" to Type.DONE,
    "function" to Type.FUNCTION, "in" to Type.IN, "select" to Type.SELECT, "time" to Type.TIME
)

val variables = hashMapOf("var" to "I love you baby")
var historyExpand = true
val history = arrayListOf<String>()

fun braceExpansion(input: String) = interpreters.brace.grammar.Interpreter(
    interpreters.brace.grammar.Parser(
        interpreters.brace.grammar.Lexer(input)
    )
).interpret()

fun arithmeticExpansion(input: String) = interpreters.calculator.grammar.Interpreter(
    interpreters.calculator.grammar.Parser(
        interpreters.calculator.grammar.Lexer(input)
    )
).interpret().toString()

fun variableExpansion(input: String): String { // TODO fix this shit
    var string = input
    for (variable in variables)
        if (string.contains(variable.key))
            string = string.replace(variable.key, variable.value)
    // this is bad because if var = Test then myvar = myTest
    return string
}

fun historyExpansion(input: String): String {
    if (!historyExpand)
        return input
    // TODO history expand somehow, read docs ===================================================
    return input
}
