package interpreters.bash.ast

import command.Command
import interpreters.bash.exception.InvalidASTVisiteeException
import interpreters.bash.grammar.Token
import java.util.*

interface Visitable {
    fun accept(visitor: Visitor): Any
}
sealed class AST: Visitable {
    override fun accept(visitor: Visitor): Any = visitor.visit(this)
}
data class Word(val token: Token): AST()
data class Assignment(val key: Token, val value: ArrayList<Token>): AST()
data class CommandSub(val command: Token): AST()
data class ProcessSub(val command: Token): AST()  // same as command sub but creates temporary file
data class Redirection(val num: Token?, val op: Token, val word: Token): AST()

data class SimpleCommand(val args: ArrayList<AST>, val redirects: ArrayList<Redirection>): AST()
data class Pipeline(val left: AST, val op: Token, val right: AST): AST()
data class Compound(val pipelines: ArrayList<AST>): AST()

/*
compound:           pipeline (';' pipeline)*
pipeline:           simple_command ('|' | '&&' | '||' | '|&' simple_command)*
simple_command:     word+ redirection*
redirection:        NUMBER? ('<' | '<<' | '>' | '>>' | '<>') word
word:	            COMMAND_SUB | PROCESS_SUB | '(' compound ')' | WORD ['=' word*]
*/
interface Visitor {
    fun visit(assignment: Assignment): String
    fun visit(word: Word): Command
    fun visit(commandSub: CommandSub): Command
    fun visit(processSub: ProcessSub): Command
    fun visit(simpleCommand: SimpleCommand): Command
    fun visit(pipeline: Pipeline): Command
    fun visit(compound: Compound): Command

    fun visit(ast: AST): Any = when (ast) {
        is Assignment -> this.visit(ast)
        is Word -> this.visit(ast)
        is CommandSub -> this.visit(ast)
        is ProcessSub -> this.visit(ast)
        is SimpleCommand -> this.visit(ast)
        is Pipeline -> this.visit(ast)
        is Compound -> this.visit(ast)
        else -> throw InvalidASTVisiteeException("$ast has no visit handler in the interpreter.")
    }
}