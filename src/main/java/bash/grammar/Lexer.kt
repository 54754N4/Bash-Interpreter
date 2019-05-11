package bash.grammar

import bash.command.*
import bash.exception.ParsingException

val reserved = hashMapOf(
    "if" to Type.IF, "then" to Type.THEN, "else" to Type.ELSE, "elif" to Type.ELIF,
    "fi" to Type.FI, "case" to Type.CASE, "esac" to Type.ESAC, "for" to Type.FOR,
    "while" to Type.WHILE, "until" to Type.UNTIL, "do" to Type.DO, "done" to Type.DONE,
    "function" to Type.FUNCTION, "in" to Type.IN, "select" to Type.SELECT, "time" to Type.TIME
)

/**
 * Bash does alias/history/arithmetic expansion and recognizes process substitution during
 * lexing i think based on the docs.
 * So let's just try and recreate that functionality using the Type.EXPANSION token, that's
 * only used by the lexer and not the parser cause every expansion will later on be treated
 * as a Type.WORD in the parser.
 */
class Lexer(private val text: String) {
    private var condition = false
    private var pos: Int = 0
    private var line: Int = 0
    private var currentChar = text[0]
    private var finished = false
    init {
        history.add(text)
    }

    private fun advance() {
        try { currentChar = text[++pos] }
        catch (e: StringIndexOutOfBoundsException) { finished = true }  // gracefully finish parsing
    }

    private fun peek(string: String):Boolean = text.substring(pos).startsWith(string)

    private fun unescaped() = when (pos) {
        0 -> true
        else -> text[pos-1] != '\\'
    }

    private fun skipComment() {
        while (currentChar != '\n' && !finished)
            advance()
    }

    private fun word(): String {
        val result = StringBuilder()
        while ((currentChar.isLetter()
            || currentChar.isDigit()
            || currentChar == '_'
            || currentChar == '['
            || currentChar == ']'
            || currentChar == '.'
            || currentChar == '-')
            && !finished) {
            result.append(currentChar)
            advance()
        }
        return result.toString()
    }

    private fun quoted(by: Char): String {
        val result = StringBuilder()
        advance()   //skip current quote
        while (currentChar != by && unescaped() && !finished) {
            result.append(currentChar)
            advance()
        }
        advance()   //skip ending unescaped quote
       return result.toString()
    }

    private fun expansion(): Token {
        val result = StringBuilder()
        val stop: String
        advance()              // skip expansion char
        if (currentChar == '{') {
            if (peek("{{")) {
                stop = "}}"
                advance()
            } else stop = "}"
            advance()
        } else if (currentChar == '(') {
            advance()
            stop = ")"
        } else stop = " "
        while (!finished && !peek(stop)) {
            result.append(currentChar)
            advance()
        }
        when (stop) {   // skip closing braces
            "}}" -> {
                advance()
                advance()
            }
            "}",")"," " -> advance()
        }
        val string = historyExpansion(result.toString())   // do hist. exp. on cmd. sub. + ari. exp. ?
        return when (stop) {
            " " -> Token(Type.WORD, variableExpansion(result.toString()))
            ")" -> Token(Type.PROCESS_SUBSTITUTION, result.toString())
            "}" -> Token(Type.COMMAND_SUBSTITUTION, string)
            "}}" -> Token(Type.ARITHMETIC_EXPANSION, string)
            else -> error()
        }
    }

    private fun number(): String {
        val result = StringBuilder()
        while (currentChar.isDigit() && !finished) {
            result.append(currentChar)
            advance()
        }
        return result.toString()
    }

    private fun id(): Token {
        val result = word()
        if (reserved.containsKey(result))
            return Token(reserved[result]!!, result)
        return Token(Type.WORD, result)
    }

    private fun conditionCommand(): Token {
        val result = StringBuilder()
        while (currentChar != ']' && text[pos+1] != ']') {
            result.append(currentChar)
            advance()
        }
        return Token(Type.CONDITION_CMD, result.toString())
    }

    fun getNextToken(): Token {
        if (condition)
            return conditionCommand()
        testChar@while (!finished) {
            return when {
                currentChar.isDigit() -> Token(Type.NUMBER, number())
                currentChar.isLetter() || currentChar == '_' || currentChar == '-' -> id()
                else -> {
                    when (currentChar) {
                        '"' -> Token(Type.WORD, quoted(currentChar))    // QUOTED_WORD
                        '$' ->  expansion()                              // EXPANSION
                        '#' -> {
                            skipComment()
                            continue@testChar
                        }
                        ' ' -> {
                            advance()
                            continue@testChar
                        }
                        '\n' -> {
                            advance()
                            Token(Type.NEW_LINE)
                        }
                        '!' -> {
                            advance()
                            Token(Type.BANG)
                        }
                        '{' -> {
                            advance()
                            Token(Type.LEFT_CURLY_BRACE)
                        }
                        '}' -> {
                            advance()
                            Token(Type.RIGHT_CURLY_BRACE)
                        }
                        '=' -> {
                            advance()
                            Token(Type.ASSIGNMENT)
                        }
                        '-' -> {
                            advance()
                            if (currentChar == 'p' && (pos+1 < text.length && !text[pos+1].isLetter())) {
                                advance()
                                Token(Type.TIMEOPT)
                            } else if (currentChar == '-') {
                                advance()
                                Token(Type.TIMEIGN)
                            }
                            Token(Type.DASH)
                        }
                        '[' -> {
                            advance()
                            if (currentChar == '[') {
                                advance()
                                condition = true
                                Token(Type.CONDITION_START)
                            }
                            Token(Type.LEFT_SQUARE_BRACKET)
                        }
                        ']' -> {
                            advance()
                            if (currentChar == ']') {
                                if (!condition)
                                    error()
                                condition = false
                                advance()
                                Token(Type.CONDITION_END)
                            }
                            Token(Type.RIGHT_SQUARE_BRACKET)
                        }
                        ';' -> {
                            advance()
                            if (currentChar == ';') {
                                advance()
                                Token(Type.SEMI_SEMI)
                            }
                            Token(Type.SEMI)
                        }
                        '>' -> {
                            advance()
                            when (currentChar) {
                                '>' -> {
                                    advance()
                                    Token(Type.GREATER_GREATER)
                                }
                                '&' -> {
                                    advance()
                                    Token(Type.GREATER_AND)
                                }
                                '|' -> {
                                    advance()
                                    Token(Type.GREATER_BAR)
                                }
                                else -> Token(Type.GREATER)
                            }
                        }
                        '<' -> {
                            advance()
                            when (currentChar) {
                                '<' -> {
                                    advance()
                                    if (currentChar == '-') {
                                        advance()
                                        Token(Type.LESS_LESS_MINUS)
                                    } else
                                        Token(Type.LESS_LESS)
                                }
                                '&' -> {
                                    advance()
                                    Token(Type.LESS_AND)
                                }
                                '>' -> {
                                    advance()
                                    Token(Type.LESS_GREATER)
                                }
                                else -> Token(Type.LESS)
                            }
                        }
                        '&' -> {
                            advance()
                            if (currentChar == '>') {
                                advance()
                                Token(Type.AND_GREATER)
                            } else if (currentChar == '&') {
                                advance()
                                Token(Type.AND_AND)
                            }
                            Token(Type.AND)
                        }
                        '|' -> {
                            advance()
                            if (currentChar == '|') {
                                advance()
                                Token(Type.OR_OR)
                            } else if (currentChar == '&') {
                                advance()
                                Token(Type.OR_AND)
                            }
                            Token(Type.OR)
                        }
                        else -> error()
                    }
                }
            }
        }
        return Token(Type.EOF)
    }

    fun errorMessage():String = "[$line,$pos] Invalid syntax at : $currentChar"
    private fun error(): Token = throw ParsingException("Parsing Error @ ${errorMessage()}")
}