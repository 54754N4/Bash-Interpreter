package interpreters.calculator.grammar

import interpreters.calculator.ast.*
import interpreters.calculator.ast.Number
import interpreters.calculator.exception.SyntaxException

/** calculator.Parser grammar
input = expr
expr:			complex_expr
complex_expr: 	xor_expr ('|' xor_expr)*
xor_expr: 		and_expr ('^' and_expr)*
and_expr: 		shift_expr ('&' shift_expr)*
shift_expr: 	arith_expr (('<<'|'>>') arith_expr)*
arith_expr: 	term (('+'|'-') term)*
term: 			factor (('*'|'/'|'%'|'//') factor)*
factor: 		('+'|'-'|'~') factor | power
power: 			atom ['**' factor]
atom: 			func_expr
                | '(' complex_expr ')'
                | number
                | "PI"
 func_expr:     ("EXP"|"LN"|"LOG"|"SQRT"|"COS"|"SIN"|"TAN"|"ABS"|...) '(' complex_expr ')'
 */
class Parser(private val lexer: Lexer) {
    private var currentToken: Token = lexer.getNextToken()

    fun errorMessage() = lexer.errorMessage()
    private fun error(): Token = throw SyntaxException(
        "Invalid Syntax @ ${lexer.errorMessage()}"
    )
    private fun errorAST(): AST = throw SyntaxException("Invalid Syntax @ ${lexer.errorMessage()}")

    private fun consume(type: Type): Any = if (currentToken.type == type) currentToken = lexer.getNextToken() else error()

    // atom: ("EXP"|"LN"|"LOG"|"SQRT"|"COS"|"SIN"|"TAN"|"ABS"|...) '(' complex_expr ')'
    //      | '(' complex_expr ')'
    //      | number
    private fun atom(): AST {
        val token = currentToken
        return when (token.type) {
            Type.EXP, Type.LN, Type.LOG, Type.COS,
            Type.SIN, Type.TAN, Type.SQRT, Type.ABS,
            Type.ARC_COS, Type.ARC_COS_HYPERBOLIC, Type.ARC_SIN,
            Type.ARC_SIN_HYPERBOLIC, Type.ARC_TAN,
            Type.ARC_TAN_HYPERBOLIC, Type.COS_HYPERBOLIC,
            Type.SIN_HYPERBOLIC, Type.TAN_HYPERBOLIC,
            Type.CEIL, Type.FLOOR, Type.ROUND -> {
                val funcToken = currentToken
                consume(funcToken.type)
                consume(Type.LEFT_PARENTHESIS)
                val exprNode = complex_expr()
                consume(Type.RIGHT_PARENTHESIS)
                Composition(funcToken, exprNode)
            }
            Type.LEFT_PARENTHESIS -> {
                consume(Type.LEFT_PARENTHESIS)
                val node = complex_expr()
                consume(Type.RIGHT_PARENTHESIS)
                node
            }
            Type.NUMBER -> {
                consume(Type.NUMBER)
                Number(token)
            }
            Type.PI -> {
                consume(Type.PI)
                Number(token)
            }
            else -> errorAST()
        }
    }

    // power: atom ['**' factor]
    private fun power(): AST {
        var node = atom()
        val token = currentToken
        if (currentToken.type == Type.POWER) {
            consume(Type.POWER)
            node = BinaryOperator(node, token, factor())
        }
        return node
    }

    // factor: ('+'|'-'|'~') factor | power
    private fun factor(): AST {
        return when (currentToken.type) {
            Type.PLUS, Type.MINUS, Type.FLIP -> {
                val token = currentToken
                consume(token.type)
                UnaryOperator(token, factor())
            }
            else -> power()
        }
    }

    // term: factor (('*'|'/'|'%'|'//') factor)*
    private fun term(): AST {
        var node = factor()
        while (currentToken.type in arrayOf(
                Type.MULTIPLY,
                Type.DIVIDE,
                Type.FLOOR_DIVIDE,
                Type.MODULUS
            )) {
            val token = currentToken
            consume(token.type)
            node = BinaryOperator(node, token, factor())
        }
        return node
    }

    // arith_expr: term (('+'|'-') term)*
    private fun arith_expr(): AST {
        var node = term()
        while (currentToken.type in arrayOf(
                Type.PLUS,
                Type.MINUS
            )) {
            val token = currentToken
            consume(token.type)
            node = BinaryOperator(node, token, term())
        }
        return node
    }

    //shift_expr: arith_expr (('<<'|'>>') arith_expr)*
    private fun shift_expr(): AST {
        var node = arith_expr()
        while (currentToken.type in arrayOf(
                Type.LEFT_SHIFT,
                Type.RIGHT_SHIFT
            )) {
            val token = currentToken
            consume(token.type)
            node = BinaryOperator(node, token, arith_expr())
        }
        return node
    }

    //and_expr: shift_expr ('&' shift_expr)*
    private fun and_expr(): AST {
        var node = shift_expr()
        while (currentToken.type == Type.AND) {
            val token = currentToken
            consume(token.type)
            node = BinaryOperator(node, token, shift_expr())
        }
        return node
    }

    //xor_expr: and_expr ('^' and_expr)*
    private fun xor_expr(): AST {
        var node = and_expr()
        while (currentToken.type == Type.XOR) {
            val token = currentToken
            consume(token.type)
            node = BinaryOperator(node, token, and_expr())
        }
        return node
    }

    //complex_expr: xor_expr ('|' xor_expr)*
    private fun complex_expr(): AST {
        var node = xor_expr()
        while (currentToken.type == Type.OR) {
            val token = currentToken
            consume(token.type)
            node = BinaryOperator(node, token, xor_expr())
        }
        return node
    }

    // expr: complex_expr
    private fun expr(): AST {
        return complex_expr()
    }

    fun parse(): AST {
        return expr()
    }
}