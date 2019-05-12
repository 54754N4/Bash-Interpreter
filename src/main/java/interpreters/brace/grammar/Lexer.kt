package interpreters.brace.grammar

import interpreters.brace.exception.InvalidBraceExpansionException

fun main() {
    val text = "abc{d,e,f,g}hijk"
    val text2 = "abc{1..2}asdf32-w=)"
    val text3 = "a{a..z}z"
    val lexer = Lexer(text2)
    lexer.getTokens()
    var token = lexer.getNextToken()
    while (token.type != Type.EOF) {
        println(token)
        token = lexer.getNextToken()
    }
    println(token)
}

class Lexer(private val text: String) {
    private var pos: Int = 0
    private var line: Int = 0
    private var currentChar = text[0]
    private var finished = false

    fun reset() {
        pos = 0
        currentChar = text[0]
        finished = false
    }

    fun getTokens(): ArrayList<Token> {
        val tokens = arrayListOf<Token>()
        var token = getNextToken()
        while (token.type != Type.EOF) {
            tokens.add(token)
            token = getNextToken()
        }
        reset()
        return tokens
    }

    private fun advance() {
        try { currentChar = text[++pos] }
        catch (e: StringIndexOutOfBoundsException) { finished = true }  // gracefully finish parsing
    }

    private fun peek(start: String) = text.substring(pos).startsWith(start)

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
        while (!finished && currentChar !in stops || (currentChar in stops && escaped())) {
            if (peek(".."))
                break
            result.append(currentChar)
            advance()
        }
        if (result.length == 1)
            return Token(Type.CHAR, result.toString())
        return Token(Type.WORD, result.toString())
    }

    fun getNextToken(): Token {
        while (!finished) {
            return when {
                currentChar.isDigit() -> number()
                currentChar == ' ' || currentChar.isLetter() -> word()
                currentChar == ',' -> {
                    advance()
                    Token(Type.COMMA)
                }
                currentChar == '{' -> {
                    advance()
                    Token(Type.EXPR_START)
                }
                currentChar == '}' -> {
                    advance()
                    Token(Type.EXPR_END)
                }
                peek("..") -> {
                    advance()
                    advance()
                    Token(Type.RANGE)
                }
                else -> error()
            }
        }
        return Token(Type.EOF)
    }

    fun errorMessage():String = "[$line,$pos] Invalid syntax at : $currentChar"
    private fun error(): Token = throw InvalidBraceExpansionException("Lexing @ ${errorMessage()}")
}