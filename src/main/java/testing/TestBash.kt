package testing

import command.CustomCommand
import interpreters.bash.ast.Compound
import interpreters.bash.grammar.Lexer
import interpreters.bash.grammar.Parser
import interpreters.bash.grammar.Type

fun main() {
    testLexer()
    //testBrace()
}

private fun testLexer() {
    val text = listOf(
        "who",
        "ls -l --aasdf=valye -qwe -asd",
        "cc pgm.c &",
        "ls -l >> file",
        "ls -l >file; wc <file",
        "ls -l | wc",                       // 5
        "if [ \$myvar -gt 10 ]\n" +
                "then\n" +
                "    echo \"OK\"\n" +
                "else\n" +
                "    echo \"Not OK\"\n" +
                "fi",
        "echo \$var \${ls -l} \${{1+2*exp(1)}}  {a,v,b} < somewhere; other --param > output < input")  // 7
    val str = text[7]
    val lexer = Lexer(str)
    println(str)
    var token = lexer.getNextToken()
    while (Type.EOF != token.type) {
        println(token)
        token = lexer.getNextToken()
    }
    println(token)
    val parser = Parser(Lexer(str))
    debug(parser.parse())
}

private fun debug(compound: Compound) {
    for (pipeline in compound.pipelines)
        println(pipeline)
}

// BRACE EXPANSION TESTS

private fun testBrace() {
    val inputs = listOf("abc{d,e,f,g}hijk", "abc{1..2}asdf32-w=)", "{a..z}", "{A..Z}{0..9}", "{{A..Z},{a..z}}", "{10..01}", "asdf")
    for (input in inputs)
        println("$input expands to :\n${braceExpand(input)}\n")
}

private fun braceExpand(string: String) = interpreters.brace.grammar.Interpreter(string).interpret()
