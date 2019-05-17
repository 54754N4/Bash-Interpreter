package interpreters.bash.grammar

import interpreters.bash.exception.SyntaxException
import interpreters.bash.ast.*

/*
Our grammar production rules are defined as such :
compound:           pipeline (';' pipeline)*
pipeline:           simple_command ('|'|'&&'|'||'|'|&' simple_command)*
simple_command:     word+ redirection*
redirection:        NUMBER? ('<'|'<<'|'>'|'>>'|'<>') word
word:	            COMMAND_SUB | ARITHMETIC_SUB | PROCESS_SUB | '(' compound ')' | WORD ['=' word*]
 */
class Parser(private val lexer: Lexer) {
    companion object {
        private val WORD_STARTS = arrayOf(Type.COMMAND_SUBSTITUTION, Type.ARITHMETIC_EXPANSION, Type.LEFT_PARENTHESIS, Type.WORD)
        private val REDIRECTION_OPERATORS = arrayOf(Type.LESS, Type.LESS_LESS, Type.GREATER, Type.GREATER_GREATER, Type.LESS_GREATER)
        private val PIPELINE_OPERATORS = arrayOf(Type.OR, Type.AND_AND, Type.OR_OR, Type.OR_AND)
    }
    private var currentToken: Token = lexer.getNextToken()

    fun errorMessage() = lexer.errorMessage()
    private fun error(): Token = throw SyntaxException("Invalid Syntax @ ${errorMessage()}")
    private fun errorAST(): AST = throw SyntaxException("Invalid Syntax @ ${errorMessage()}")

    private fun consume(type: Type) {
        if (currentToken.type == type)
            currentToken = lexer.getNextToken()
        else
            error()
    }

    //word: COMMAND_SUB | ARITHMETIC_SUB | PROCESS_SUB | '(' compound ')' | WORD ['=' word*]
    private fun word(): AST {
        val token = currentToken
        return when (token.type) {
            Type.COMMAND_SUBSTITUTION -> {
                consume(Type.COMMAND_SUBSTITUTION)
                CommandSub(token)
            }
            Type.ARITHMETIC_EXPANSION -> {
                consume(Type.ARITHMETIC_EXPANSION)
                ArithmeticSub(token)
            }
            Type.PROCESS_SUBSTITUTION -> {
                consume(Type.PROCESS_SUBSTITUTION)
                ProcessSub(token)
            } Type.LEFT_PARENTHESIS -> {
                consume(Type.LEFT_PARENTHESIS)
                val compound = compound()
                consume(Type.RIGHT_PARENTHESIS)
                compound
            }
            else -> {
                val word = currentToken
                consume(Type.WORD)
                if (currentToken.type == Type.ASSIGNMENT) {
                    consume(Type.ASSIGNMENT)
                    val values = arrayListOf<Token>()
                    while (currentToken.type == Type.WORD) {
                        values.add(currentToken)
                        consume(Type.WORD)
                    }
                    Assignment(word, values)
                } else
                    Word(word)
            }
        }
    }

    //redirection: [NUMBER] ('<'|'<<'|'>'|'>>'|'<>') word
    private fun redirection(): Redirection {
        var number: Token? = null
        val op: Token
        val word: Token
        if (currentToken.type == Type.NUMBER) {
            number = currentToken
            consume(Type.NUMBER)
        }
        return if (currentToken.type in REDIRECTION_OPERATORS) {
            op = currentToken
            consume(op.type)
            word = currentToken
            consume(Type.WORD)
            Redirection(number, op, word)
        } else
            errorAST() as Redirection
    }

    //simple_command:     word+ redirection*
    private fun simple_command(): AST {
        val args = arrayListOf<AST>()
        while (currentToken.type in WORD_STARTS)
            args.add(word())
        val redirects = arrayListOf<Redirection>()
        while (currentToken.type in REDIRECTION_OPERATORS)
            redirects.add(redirection())
        return SimpleCommand(args, redirects)
    }

    //pipeline: simple_command ('|'|'&&'|'||'|'|&' simple_command)*
    private fun pipeline(): AST {
        var node = simple_command()
        while (currentToken.type in PIPELINE_OPERATORS) {
            val token = currentToken
            consume(currentToken.type)
            node = Pipeline(node, token, simple_command())
        }
        return node
    }

    //compound: pipeline (';' pipeline)*
    private fun compound(): Compound {
        val compound = arrayListOf(pipeline())
        while (currentToken.type == Type.SEMI) {
            consume(Type.SEMI)
            compound.add(pipeline())
        }
        return Compound(compound)
    }

    fun parse(): Compound {
        return compound()
    }
}