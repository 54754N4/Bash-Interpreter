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
    val text = "abc{d,e,f,g}hijk"
    val text2 = "abc{1..2}asdf32-w=)"
    val text3 = "{a..z}"
    val lexer = interpreters.brace.grammar.Lexer(text2)
    println(lexer.getTokens())
    println(interpreters.brace.grammar.Interpreter(interpreters.brace.grammar.Parser(interpreters.brace.grammar.Lexer(text2))).interpret())
}
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