package calculator.ast

import calculator.grammar.Token

sealed class AST: Visitable {
    override fun accept(visitor: Visitor): Double = visitor.visit(this)
}
class Number(val token: Token):AST()
class BinaryOperator(val left: AST, val token: Token, val right: AST):AST()
class UnaryOperator(val token: Token, val expr: AST):AST()
class Composition(val token: Token, val expr: AST):AST()