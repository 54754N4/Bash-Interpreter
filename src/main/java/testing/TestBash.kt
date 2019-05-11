package testing

import bash.ast.Compound
import bash.grammar.Lexer
import bash.grammar.Parser

fun main() {
    val word = "{a a,v,b}"
    val word1 = "{a..b..2}"
    val pattern = """.*\{.*\}.*""".toRegex(RegexOption.DOT_MATCHES_ALL)
    val pattern1 = """.*\{.*\.\..*\}.*""".toRegex(RegexOption.DOT_MATCHES_ALL)
    println(word.matches(pattern1) || word.matches(pattern))
//    testLexer()
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