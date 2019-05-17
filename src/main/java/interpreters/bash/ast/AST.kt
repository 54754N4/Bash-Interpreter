package interpreters.bash.ast

import command.Command
import interpreters.bash.grammar.Token
import java.util.*

sealed class AST: Visitable {
    override fun accept(visitor: Visitor): Command = visitor.visit(this)
}
data class Word(val word: Token): AST()
data class Assignment(val key: Token, val value:ArrayList<Token>): AST()
data class CommandSub(val command: Token): AST()
data class ProcessSub(val command: Token): AST()  // same as command sub but creates temporary file
data class Redirection(val num: Token?, val op: Token, val word: Token): AST()

data class SimpleCommand(val args:ArrayList<AST>, val redirects:ArrayList<Redirection>): AST()
data class Pipeline(val left: AST, val op: Token, val right: AST): AST()
data class Compound(val pipelines:ArrayList<AST>): AST()