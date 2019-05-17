package interpreters.brace.ast

import interpreters.brace.grammar.Token

sealed class AST: Visitable {
    override fun accept(visitor: Visitor): String = visitor.visit(this)
}
sealed class Expression: AST()
class RangeExpression(val start: Token, val end: Token, val inc: Token?): Expression()
class CSVExpression(val atoms: Array<Atom>): Expression()
sealed class Atom: AST()
class Word(val word: Token): Atom()
class BraceExpansion(val preamble: Atom, val expression: Expression, val postscript: Atom): Atom()