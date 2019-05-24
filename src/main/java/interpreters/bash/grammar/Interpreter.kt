package interpreters.bash.grammar

import command.Command
import command.CustomCommand
import command.NativeCommand
import command.variables
import interpreters.bash.ast.*
import interpreters.bash.exception.InterpretationException

/*
Another example of where piping cannot be used:
        echo 'foo' | read; echo ${REPLY}
 will not return foo, because read is started in a
 sub-shell — piping starts a sub-shell. However,
        read < <(echo 'foo'); echo ${REPLY}
 correctly returns foo, because there is no sub-shell.
 */

abstract class Interpreter(private val parser: Parser): Visitor {

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

//    override fun visit(redirection: Redirection): Command {
//        val command = redirection.command
//        return when (redirection.op.type) {
//            Type.LESS -> {
//                if (redirection.num != null) {
//
//                }
//                command.input
//                command
//            }
//            Type.LESS_LESS -> {
//
//            }
//            Type.GREATER -> {
//
//            }
//            Type.GREATER_GREATER -> {
//
//            }
//            Type.LESS_GREATER -> {
//
//            }
//            else -> error
//        }
//
//    }

    override fun visit(simpleCommand: SimpleCommand): Command {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(pipeline: Pipeline): Command {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

//    override fun visit(compound: Compound): Command {
//        for (pipeline in compound.pipelines)
//            visit(pipeline)
//    }

    override fun visit(commandSub: CommandSub): Command {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(processSub: ProcessSub): Command {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}