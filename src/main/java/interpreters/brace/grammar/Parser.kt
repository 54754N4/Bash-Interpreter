package interpreters.brace.grammar

import interpreters.brace.ast.*
import interpreters.brace.exception.InvalidBraceExpansionException

// brace_expand:    WORD? '{' expression '}' WORD?
// expression:      [a-z]..[a-z][..[0-9]+] | atom (',' atom)+
// atom:            WORD | brace_expand

class Parser(private val lexer: Lexer) {
    private val tokens = lexer.getTokens()
    private var current = 0

    fun errorMessage() = lexer.errorMessage()
    private fun error(): Token = throw InvalidBraceExpansionException("Brace Expansion Error @ ${lexer.errorMessage()}")
    private fun errorAST(): AST = throw InvalidBraceExpansionException("Parsing @ ${lexer.errorMessage()}")

    private fun currentToken() = tokens[current]
    private fun consume(type: Type): Any = (if (currentToken().type == type) current++ else error())

    // atom: WORD | brace_expand
    private fun atom(): AST {
        val token = currentToken()
        return if (currentToken().type in arrayOf(Type.WORD, Type.CHAR, Type.NUMBER)) {
                consume(currentToken().type)
                Word(token)
            }
            else brace_expand()
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
    private fun brace_expand(): AST {
        var preamble: AST = Word(Token(Type.EMPTY))
        var postscript: AST = Word(Token(Type.EMPTY))
        if (currentToken().type == Type.WORD || currentToken().type == Type.CHAR || currentToken().type == Type.NUMBER)
            preamble = atom()
        if (currentToken().type == Type.EXPR_START) {
            consume(Type.EXPR_START)
            val expression = expression()
            consume(Type.EXPR_END)
            if (currentToken().type == Type.WORD || currentToken().type == Type.CHAR || currentToken().type == Type.NUMBER || currentToken().type == Type.EXPR_START)
                postscript = atom()
            return BraceExpansion(preamble, expression, postscript)
        }
        return preamble
    }

    fun parse(): AST {
        return brace_expand()
    }
}