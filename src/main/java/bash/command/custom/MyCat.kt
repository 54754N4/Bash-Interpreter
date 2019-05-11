package bash.command.custom

import bash.command.CustomCommand
import java.io.InputStream

class MyCat: CustomCommand {
    constructor(): super("MyCat")
    constructor(input: InputStream?): super("MyCat", input)

    override fun execute() {
        for (line in inputLines) out.appendln(line)
    }
}