package command

import interpreters.bash.grammar.Type
import interpreters.brace.BraceExpansion

val reserved = hashMapOf(
    "if" to Type.IF, "then" to Type.THEN, "else" to Type.ELSE, "elif" to Type.ELIF,
    "fi" to Type.FI, "case" to Type.CASE, "esac" to Type.ESAC, "for" to Type.FOR,
    "while" to Type.WHILE, "until" to Type.UNTIL, "do" to Type.DO, "done" to Type.DONE,
    "function" to Type.FUNCTION, "in" to Type.IN, "select" to Type.SELECT, "time" to Type.TIME
)

val variables = hashMapOf("var" to "I love you baby")
var historyExpand = true
val history = arrayListOf<String>()

fun main(){ //test nested !
    val text = "a{b,c,d}e"
    val text1 = "a{a..z}z"
    val text2 = "a{a..z..2}z"
    println(braceExpand(text1))
}

fun variableExpansion(input: String): String {
    var string = input
    for (variable in variables)
        if (string.contains(variable.key))
            string = string.replace(variable.key, variable.value)
    return string
}

fun historyExpansion(input: String): String {
    if (!historyExpand)
        return input
    // TODO history expand somehow, read docs ===================================================
    return input
}

fun braceExpand(input: String): String {
    return BraceExpansion(input).expand()
}
