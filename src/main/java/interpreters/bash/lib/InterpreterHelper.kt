package interpreters.bash.lib

import command.Command
import java.io.InputStream

infix fun Command.redirOutput(output: InputStream?): Command {
    this.output = output
    return this
}

infix fun Command.redirInput(input: InputStream?) = this(input)

infix fun Command.pipe(cmd: Command): Command {
    launch()
    return cmd redirInput output
}

operator fun Command.invoke(input: InputStream?): Command {
    this.input = input
    return this
}