package calculator.grammar

import calculator.exception.ParsingException
import kotlin.math.PI

// LEXER CODE
class Lexer(private val text: String) {
    private var pos: Int = 0
    private var line: Int = 0
    private var currentChar = text[0]
    var finished = false

    private fun advance() {
        try { currentChar = text[++pos] }
        catch (e: StringIndexOutOfBoundsException) { finished = true }  // gracefully finish parsing
    }

    private fun advance(i: Int) {
        var counter = i
        while (counter-->0) advance()
    }

    private fun peek(target: String) = text.substring(pos).toLowerCase().startsWith(target)

    private fun skipWhitespace() {
        while (currentChar.isWhitespace())
            advance()
    }

    private fun number(): Double {
        var result = ""
        var foundDot = false
        while ((currentChar.isDigit() || currentChar == '.') && !finished) {
            if (currentChar == '.' && !foundDot)
                foundDot = true
            else if (currentChar == '.' && foundDot)
                error()
            result += currentChar
            advance()
        }
        return result.toDouble()
    }

    private fun matchWord(): Token {
        val words = arrayOf(
            Type.ABS,
            Type.COS,
            Type.EXP,
            Type.LN,
            Type.LOG,
            Type.SIN,
            Type.SQRT,
            Type.TAN,
            Type.PI,
            Type.ARC_COS,
            Type.ARC_COS_HYPERBOLIC,
            Type.ARC_SIN,
            Type.ARC_SIN_HYPERBOLIC,
            Type.ARC_TAN,
            Type.ARC_TAN_HYPERBOLIC,
            Type.COS_HYPERBOLIC,
            Type.SIN_HYPERBOLIC,
            Type.TAN_HYPERBOLIC,
            Type.CEIL,
            Type.FLOOR,
            Type.ROUND
        )
        for (word in words) {
            if (peek(word.string.toLowerCase())) {
                advance(word.string.length)
                return Token(
                    word,
                    if (word == Type.PI) PI.toString() else word.string
                )
            }
        }
        return error()
    }

    fun getNextToken(): Token {
        loop@ while (!finished) {
            when {
                currentChar.isWhitespace() -> {
                    skipWhitespace()
                    continue@loop
                }
                currentChar.isLetter() -> return matchWord()
                currentChar.isDigit() -> return Token(
                    Type.NUMBER,
                    "${number()}"
                )
                currentChar == '+' -> {
                    advance()
                    return Token(Type.PLUS)
                }
                currentChar == '-' -> {
                    advance()
                    return Token(Type.MINUS)
                }
                currentChar == '~' -> {
                    advance()
                    return Token(Type.FLIP)
                }
                currentChar == '%' -> {
                    advance()
                    return Token(Type.MODULUS)
                }
                currentChar == '|' -> {
                    advance()
                    return Token(Type.OR)
                }
                currentChar == '&' -> {
                    advance()
                    return Token(Type.AND)
                }
                currentChar == '^' -> {
                    advance()
                    return Token(Type.XOR)
                }
                currentChar == '*' -> {
                    advance()
                    if (currentChar == '*') {
                        advance()
                        return Token(Type.POWER)
                    }
                    return Token(Type.MULTIPLY)
                }
                currentChar == '/' -> {
                    advance()
                    if (currentChar == '/') {
                        advance()
                        return Token(Type.FLOOR_DIVIDE)
                    }
                    return Token(Type.DIVIDE)
                }
                currentChar == '<' -> {
                    advance()
                    if (currentChar == '<') {
                        advance()
                        return Token(Type.LEFT_SHIFT)
                    }
                    return error()
                }
                currentChar == '>' -> {
                    advance()
                    if (currentChar == '>') {
                        advance()
                        return Token(Type.RIGHT_SHIFT)
                    }
                    return error()
                }
                currentChar == '(' -> {
                    advance()
                    return Token(Type.LEFT_PARENTHESIS)
                }
                currentChar == ')' -> {
                    advance()
                    return Token(Type.RIGHT_PARENTHESIS)
                }
                else -> error()
            }
        }
        return Token(Type.EOF)
    }

    fun errorMessage():String = "[$line,$pos] Invalid syntax after: $currentChar"
    private fun error(): Token = throw ParsingException("Parsing Error @ ${errorMessage()}")
}