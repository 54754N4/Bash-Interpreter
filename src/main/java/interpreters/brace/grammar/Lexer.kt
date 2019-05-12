package interpreters.brace.grammar

import interpreters.brace.exception.InvalidBraceExpansionException
import interpreters.brace.grammar.Type
import interpreters.brace.grammar.Token

// brace_expand:    WORD '{' expression '}' WORD
// expression:      [a-z]..[a-z][..[0-9]+] | atom (',' atom)+
// atom:            brace_expand | WORD

class Lexer(val text: String) {
    private var pos: Int = 0
    private var line: Int = 0
    private var currentChar = text[0]
    private var finished = false

    private fun advance() {
        try { currentChar = text[++pos] }
        catch (e: StringIndexOutOfBoundsException) { finished = true }  // gracefully finish parsing
    }

    private fun peek(string: String):Boolean = text.substring(pos).startsWith(string)

    private fun escaped(): Boolean {
        return when (pos) {
            0 -> false            // first char can never be escaped
            else -> text[pos-1] == '\\'
        }
    }

    private fun number(): Token {
        val result = StringBuilder()
        while (currentChar.isDigit()) {
            result.append(currentChar)
            advance()
        }
        return Token(Type.NUMBER, result.toString())
    }

    private fun word(): Token {
        val result = StringBuilder()
        val stops = arrayOf('}', '{', ',')
        while (currentChar !in stops && escaped()) {
            result.append(currentChar)
            advance()
        }
        return Token(Type.WORD, result.toString())
    }

    fun getNextToken(): Token {
        while (!finished) {
            return when {
                currentChar.isDigit() -> number()
                currentChar.isLetter() -> word()
                currentChar == ',' -> Token(Type.COMMA)
                currentChar == '{' -> Token(Type.EXPR_START)
                currentChar == '}' -> Token(Type.EXPR_END)
                peek("src/main") -> Token(Type.RANGE)
                else -> error()
            }
        }
        return Token(Type.EOF)
    }

    fun errorMessage():String = "[$line,$pos] Invalid syntax at : $currentChar"
    private fun error(): Token = throw InvalidBraceExpansionException(
        "Brace Expansion Error @ ${errorMessage()}"
    )
}