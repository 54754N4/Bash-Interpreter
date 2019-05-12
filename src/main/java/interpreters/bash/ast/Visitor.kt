package interpreters.bash.ast

import command.Command

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