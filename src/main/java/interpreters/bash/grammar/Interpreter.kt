package interpreters.bash.grammar

import command.Command
import interpreters.bash.ast.*

/*
Another example of where piping cannot be used:
        echo 'foo' | read; echo ${REPLY}
 will not return foo, because read is started in a
 sub-shell — piping starts a sub-shell. However,
        read < <(echo 'foo'); echo ${REPLY}
 correctly returns foo, because there is no sub-shell.
 */

class Interpreter(private val parser: Parser): Visitor {
    override fun visit(assignment: Assignment): Command {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(word: Word): Command {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(commandSub: CommandSub): Command {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(arithmeticSub: ArithmeticSub): Command {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(processSub: ProcessSub): Command {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(redirection: Redirection): Command {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(simpleCommand: SimpleCommand): Command {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(pipeline: Pipeline): Command {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(compound: Compound): Command {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}