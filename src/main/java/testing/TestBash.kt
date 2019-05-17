package testing

import interpreters.bash.ast.Compound
import interpreters.bash.grammar.Lexer
import interpreters.bash.grammar.Parser
import interpreters.brace.grammar.Type

fun main() {
    testBrace()
    //testLexer()
}
fun testBrace() {
    val inputs = listOf("abc{d,e,f,g}hijk", "abc{1..2}asdf32-w=)", "{a..z}", "{A..Z}{0..9}", "{{A..Z},{a..z}}", "{10..01}")
    for (input in inputs) {
        println("$input expands to : ")
        println("${braceExpand(input)}\n")
    }
}

private fun braceExpand(string: String) = interpreters.brace.grammar.Interpreter(interpreters.brace.grammar.Parser(interpreters.brace.grammar.Lexer(string))).interpret()

fun testLexer() {
    val text = listOf(
        "who",
        "ls -l",
        "cc pgm.c &",
        "ls -l >> file",
        "ls -l >file; wc <file",
        "ls -l | wc",
        "if [ \$myvar -gt 10 ]\n" +
                "then\n" +
                "    echo \"OK\"\n" +
                "else\n" +
                "    echo \"Not OK\"\n" +
                "fi",
        "echo \$var \${ls -l} \${{1+2*exp(1)}}  {a,v,b} < somewhere; other --param > output < input")
    val str = text[7]
    val lexer = Lexer(str)
    println(str)
//    var token = lexer.getNextToken()
//    while (token.type != Type.EOF) {
//        println(token)
//        token = lexer.getNextToken()
//    }
//    println(token)
    val parser = Parser(Lexer(str))
    debug(parser.parse())
}

fun debug(compound: Compound) {
    for (pipeline in compound.pipelines)
        println(pipeline)
}