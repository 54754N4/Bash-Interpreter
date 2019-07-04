package interpreters.bash.grammar

import command.Command
import command.CustomCommand
import command.NativeCommand
import command.variables
import interpreters.bash.ast.*
import interpreters.bash.exception.InterpretationException
import interpreters.bash.exception.InvalidCommandException
import interpreters.bash.lib.getRedirectionFile
import io.STD

/*
Another example of where piping cannot be used:
        echo 'foo' | read; echo ${REPLY}
 will not return foo, because read is started in a
 sub-shell — piping starts a sub-shell. However,
        read < <(echo 'foo'); echo ${REPLY}
 correctly returns foo, because there is no sub-shell.
 */

class Interpreter(private val parser: Parser): Visitor {

    fun interpret() = visit(parser.parse())

    private fun error(): Command = throw InterpretationException("Interpreting @ ${parser.errorMessage()}")

    override fun visit(assignment: Assignment): String {
        val key = assignment.key.value
        val value = StringBuilder()
        for (word in assignment.value)
            value.append(word.value+" ")
        variables[key] = value.toString().trim()
        return variables[key]!!
    }

    override fun visit(word: Word): Command  {
        return if (word.token.type == Type.WORD) CustomCommand[word.token.value] ?: NativeCommand(word.token.value)
        else error()
    }

    // TODO make it handle custom file descriptors
    override fun visit(command: Command, redirection: Redirection) {
        val file = getRedirectionFile(redirection.file.value)
        when (redirection.op.type) {
            Type.LESS -> command.redirIn(file)
            Type.GREATER -> command.redirOut(file)
            Type.GREATER_GREATER -> command.redirOut(file).appendOut(true)
            Type.LESS_GREATER -> command.redirIn(file).redirOut(file)
            Type.AND_GREATER,
            Type.GREATER_AND -> command.merge()
            Type.AND_GREATER_GREATER -> command.merge().appendOut(true)
            //            Type.LESS_LESS -> {} //wtf right -- heredocs should be managed in lexer no ?
            else -> error()
        }
    }

    override fun visit(simpleCommand: SimpleCommand): Command {
        var command = visit(simpleCommand.word)
        when (command) {
            !is Command -> throw InvalidCommandException("${simpleCommand.word}")
            else -> {
                for (word in simpleCommand.args){

                }
            }
        }
        return command
    }

    override fun visit(pipeline: Pipeline): Command {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(compound: Compound) {
        for (pipeline in compound.pipelines)
            visit(pipeline)
    }

    override fun visit(commandSub: CommandSub): Command {
        val interpeted = Interpreter(Parser(Lexer(commandSub.command.value))).interpret()
        STD.output
    }

    override fun visit(processSub: ProcessSub): Command {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}