package interpreters.brace.grammar

import interpreters.brace.exception.InvalidBraceExpansionException

class Lexer(private val text: String) {
    private enum class State {PREAMBLE, EXPRESSION, POSTSCRIPT, FINISHED}
    private var pos: Int = 0
    private var line: Int = 0
    private var currentChar = text[0]
    private var finished = false
    private var currentState = State.PREAMBLE

    private fun reset() {
        pos = 0
        currentChar = text[0]
        finished = false
        currentState = State.PREAMBLE
    }

    fun getTokens(): ArrayList<Token> {
        val tokens = arrayListOf<Token>()
        var token = getNextToken()
        while (token.type != Type.EOF) {
            tokens.add(token)
            token = getNextToken()
        }
        tokens.add(token)
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

    private fun preamble(): Token {
        val preamble = StringBuilder()
        while (!finished && currentChar != '{') {
            preamble.append(currentChar)
            advance()
        }
        currentState = State.EXPRESSION
        return Token(Type.WORD, preamble.toString())
    }

    private fun postscript(): Token {
        val postscript = StringBuilder()
        while (!finished) {
            postscript.append(currentChar)
            advance()
        }
        currentState = State.FINISHED
        return Token(Type.WORD, postscript.toString())
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
        while (currentState != State.FINISHED) {
            return when (currentState) {
                State.PREAMBLE -> preamble()
                State.POSTSCRIPT -> postscript()
                State.EXPRESSION -> {
                    when {
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
                            currentState = State.POSTSCRIPT
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
                State.FINISHED -> Token(Type.EOF)
            }
        }
    }

    fun errorMessage():String = "[$line,$pos] Invalid syntax at : $currentChar"
    private fun error(): Token = throw InvalidBraceExpansionException("Lexing @ ${errorMessage()}")
}

//package interpreters.brace.grammar
//
//import interpreters.brace.exception.InvalidBraceExpansionException
//
//class Lexer(private val text: String) {
//    private enum class State {PREAMBLE, EXPRESSION, POSTSCRIPT, FINISHED}
//    private var pos: Int = 0
//    private var line: Int = 0
//    private var currentChar = text[0]
//    private var finished = false
//    private var currentState = State.PREAMBLE
//
//    private fun reset() {
//        pos = 0
//        currentChar = text[0]
//        finished = false
//        currentState = State.PREAMBLE
//    }
//
//    fun getTokens(): ArrayList<Token> {
//        val tokens = arrayListOf<Token>()
//        var token = getNextToken()
//        while (token.type != Type.EOF) {
//            tokens.add(token)
//            token = getNextToken()
//        }
//        tokens.add(token)
//        reset()
//        return tokens
//    }
//
//    private fun advance() {
//        try { currentChar = text[++pos] }
//        catch (e: StringIndexOutOfBoundsException) { finished = true }  // gracefully finish parsing
//    }
//
//    private fun peek(start: String) = text.substring(pos).startsWith(start)
//
//    private fun escaped(): Boolean {
//        return when (pos) {
//            0 -> false            // first char can never be escaped
//            else -> text[pos-1] == '\\'
//        }
//    }
//
//    private fun number(): Token {
//        val result = StringBuilder()
//        while (currentChar.isDigit()) {
//            result.append(currentChar)
//            advance()
//        }
//        return Token(Type.NUMBER, result.toString())
//    }
//
//    private fun preamble(): Token {
//        val preamble = StringBuilder()
//        while (!finished && currentChar != '{') {
//            preamble.append(currentChar)
//            advance()
//        }
//        currentState = State.EXPRESSION
//        return Token(Type.WORD, preamble.toString())
//    }
//
//    private fun postscript(): Token {
//        val postscript = StringBuilder()
//        while (!finished) {
//            postscript.append(currentChar)
//            advance()
//        }
//        currentState = State.FINISHED
//        return Token(Type.WORD, postscript.toString())
//    }
//
//    private fun word(): Token {
//        val result = StringBuilder()
//        val stops = arrayOf('}', '{', ',')
//        while (!finished && currentChar !in stops || (currentChar in stops && escaped())) {
//            if (peek(".."))
//                break
//            result.append(currentChar)
//            advance()
//        }
//        if (result.length == 1)
//            return Token(Type.CHAR, result.toString())
//        return Token(Type.WORD, result.toString())
//    }
//
//    fun getNextToken(): Token {
//        while (currentState != State.FINISHED) {
//            return when (currentState) {
//                State.PREAMBLE -> preamble()
//                State.POSTSCRIPT -> postscript()
//                State.EXPRESSION -> {
//                    when {
//                        currentChar.isDigit() -> number()
//                        currentChar == ' ' || currentChar.isLetter() -> word()
//                        currentChar == ',' -> {
//                            advance()
//                            Token(Type.COMMA)
//                        }
//                        currentChar == '{' -> {
//                            advance()
//                            Token(Type.EXPR_START)
//                        }
//                        currentChar == '}' -> {
//                            advance()
//                            currentState = State.POSTSCRIPT
//                            Token(Type.EXPR_END)
//                        }
//                        peek("..") -> {
//                            advance()
//                            advance()
//                            Token(Type.RANGE)
//                        }
//                        else -> error()
//                    }
//                }
//                State.FINISHED -> Token(Type.EOF)
//            }
//        }
//    }
//
//    fun errorMessage():String = "[$line,$pos] Invalid syntax at : $currentChar"
//    private fun error(): Token = throw InvalidBraceExpansionException("Lexing @ ${errorMessage()}")
//}