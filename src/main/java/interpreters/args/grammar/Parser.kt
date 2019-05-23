package interpreters.args.grammar

import interpreters.args.exception.InvalidParsingException

/** Grammar :
 *
 *  input:          (INPUT | named_param | unnamed_param)+
 *  named_param:    NAMED WORD [ EQUALS WORD ]
 *  unnamed_param:  UNNAMED WORD
 */
class Parser {      // also our interpreter cause it's so simple
    private var current = 0
    private var finished = false
    private lateinit var currentToken: Token
    private lateinit var lexer: Lexer
    private lateinit var dictionary: MutableMap<String, String>
    private lateinit var inputs: MutableList<String>

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

    private fun lateInit(input: String) {
        lexer = Lexer(input)
        dictionary = mutableMapOf()
        inputs = mutableListOf()
        current = 0
        finished = false
        currentToken = lexer.tokens[current]
    }

    fun parse(input: String): Args {
        lateInit(input)
        start()
        return Args(inputs.joinToString(" "), dictionary)
    }

    fun error(msg: String): Nothing = throw InvalidParsingException(msg)
}

