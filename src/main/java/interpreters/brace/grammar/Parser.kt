package interpreters.brace.grammar

import interpreters.brace.ast.*
import interpreters.brace.exception.InvalidBraceExpansionException

// brace_expand:    WORD? '{' expression '}' WORD?
// expression:      [a-z]..[a-z][..[0-9]+] | atom (',' atom)+
// atom:            brace_expand | WORD

class Parser(private val lexer: Lexer) {
    private val tokens = lexer.getTokens()
    private var current = 0

    fun errorMessage() = lexer.errorMessage()
    private fun error(): Token = throw InvalidBraceExpansionException("Brace Expansion Error @ ${lexer.errorMessage()}")
    private fun errorAST(): AST = throw InvalidBraceExpansionException("Parsing @ ${lexer.errorMessage()}")

    private fun currentToken() = tokens[current]
    private fun consume(type: Type): Any = (if (currentToken().type == type) current++ else error())

    // atom: brace_expand | WORD
    private fun atom(): Atom {
        val backtrackTo = current
        return try {
            brace_expand()
        } catch (e: InvalidBraceExpansionException) {
            current = backtrackTo
            val token = currentToken()
            if (currentToken().type in arrayOf(Type.WORD, Type.CHAR, Type.NUMBER)) consume(currentToken().type)
            else error()
            Word(token)
        }
    }

    // expression: [a-z] '..' [a-z] ['..' [0-9]+] | atom (',' atom)+
    private fun expression(): Expression {
        val backtrackTo = current
        try {
            val start = currentToken()
            when (currentToken().type) {
                Type.CHAR, Type.NUMBER -> consume(currentToken().type)
                else -> error()
            }
            consume(Type.RANGE)
            val end = currentToken()
            when (currentToken().type) {
                Type.CHAR, Type.NUMBER -> consume(currentToken().type)
                else -> error()
            }
            var inc: Token? = null
            if (currentToken().type == Type.RANGE) {
                consume(Type.RANGE)
                inc = currentToken()
                consume(Type.NUMBER)
            }
            return RangeExpression(start, end, inc)
        } catch (e: InvalidBraceExpansionException) {
            current = backtrackTo
            val words = arrayListOf(atom())
            while (currentToken().type == Type.COMMA) {
                consume(Type.COMMA)
                words.add(atom())
            }
            return CSVExpression(words.toArray(arrayOf<Atom>()))
        }
    }

    // brace_expand: WORD? '{' expression '}' WORD?
    private fun brace_expand(): BraceExpansion {
        var preamble = Token(Type.EMPTY)
        var postscript = Token(Type.EMPTY)
        if (currentToken().type == Type.WORD
            || currentToken().type == Type.CHAR
            || currentToken().type == Type.NUMBER) {
            preamble = currentToken()
            consume(currentToken().type)
        }
        consume(Type.EXPR_START)
        val expression = expression()
        consume(Type.EXPR_END)
        if (currentToken().type == Type.WORD
            || currentToken().type == Type.CHAR
            || currentToken().type == Type.NUMBER) {
            postscript = currentToken()
            consume(currentToken().type)
        }
        return BraceExpansion(preamble, expression, postscript)
    }

    fun parse(): AST {
        return brace_expand()
    }
}