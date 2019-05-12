package interpreters.brace.ast

import interpreters.brace.exception.InvalidBraceExpansionException
import interpreters.brace.grammar.Token

interface Visitor {
    fun visit(node: Word): String
    fun visit(preamble: Token, expression: RangeExpression, postscript: Token): String
    fun visit(preamble: Token,expression: CSVExpression, postscript: Token): String
    fun visit(ast: AST): String = when (ast) {
        is Word -> this.visit(ast)
        is BraceExpansion -> {
            when (ast.expression) {
                is CSVExpression -> this.visit(ast.preamble, ast.expression, ast.postscript)
                is RangeExpression -> this.visit(ast.preamble, ast.expression, ast.postscript)
            }
        }
        else -> throw InvalidBraceExpansionException("Visiting @ $ast")
    }
}