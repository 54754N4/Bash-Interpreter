package interpreters.bash.ast

import command.Command
import interpreters.bash.grammar.Token
import java.util.*

interface Visitor {
    fun visit(word: Word): Command
    fun visit(assignment: Assignment): Command
    fun visit(commandSub: CommandSub): Command
    fun visit(arithmeticSub: ArithmeticSub): Command
    fun visit(processSub: ProcessSub): Command
    fun visit(redirection: Redirection): Command
    fun visit(simpleCommand: SimpleCommand): Command
    fun visit(pipeline: Pipeline): Command
    fun visit(compound: Compound): Command
    fun visit(ast: AST): Command = when (ast) {  // since we're using "when" it automatically casts
        is Assignment -> this.visit(ast)
        is Word -> this.visit(ast)
        is CommandSub -> this.visit(ast)
        is ArithmeticSub -> this.visit(ast)
        is ProcessSub -> this.visit(ast)
        is Redirection -> this.visit(ast)
        is SimpleCommand -> this.visit(ast)
        is Pipeline -> this.visit(ast)
        is Compound -> this.visit(ast)
    }
}
interface Visitable {
    fun accept(visitor: Visitor): Command
}
sealed class AST: Visitable {
    override fun accept(visitor: Visitor): Command = visitor.visit(this)
}
data class Word(val word: Token): AST()
data class Assignment(val key: Token, val value:ArrayList<Token>):
    AST()
data class CommandSub(val command: Token): AST()
data class ArithmeticSub(val expression: Token): AST()
data class ProcessSub(val command: Token): AST()  // same as command sub but creates temporary file
data class Redirection(val num: Token?, val op: Token, val word: Token):
    AST()

data class SimpleCommand(val args:ArrayList<AST>, val redirects:ArrayList<Redirection>):
    AST()
data class Pipeline(val left: AST, val op: Token, val right: AST):
    AST()
data class Compound(val pipelines:ArrayList<AST>): AST()