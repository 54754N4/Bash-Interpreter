package interpreters.args.grammar

import interpreters.args.exception.InvalidParsingException

/** Grammar :
 *
 *  input:          (INPUT | named_param | unnamed_param)+
 *  named_param:    NAMED WORD [ EQUALS WORD ]
 *  unnamed_param:  UNNAMED WORD
 */
class Parser(val input: String) {      // also our interpreter cause it's so simple
    private val dictionary = mutableMapOf<String, String>()
    private val inputs = mutableListOf<String>()
    private var current = 0
    private var finished = false
    private var lexer = Lexer(input)
    private var currentToken = lexer.tokens[current]

    private fun advance() = try { currentToken = lexer.tokens[++current] } catch (e: IndexOutOfBoundsException) { finished = true }

    // unnamed_param:    UNNAMED WORD
    private fun unnamed() {
        advance()
        dictionary[currentToken.value] = ""
        advance()
    }

    // named_param:    NAMED WORD [ EQUALS WORD ]
    private fun named() {
        advance()
        val key = currentToken.value
        advance()
        if (currentToken.type != Type.EQUALS)
            dictionary[key] = ""
        else {
            advance()
            dictionary[key] = currentToken.value
            advance()
        }
    }

    private fun start() {
        while (!finished) {
            when (currentToken.type) {
                Type.INPUT -> {
                    inputs.add(currentToken.value)
                    advance()
                }
                Type.NAMED -> named()
                Type.UNNAMED -> unnamed()
                else -> error("Found $currentToken instead of INPUT|named_param|unnamed_param")
            }
        }
    }

    fun parse(): Args {
        start()
        return Args(inputs.joinToString(" "), dictionary)
    }

    fun error(msg: String): Nothing = throw InvalidParsingException(msg)
}

