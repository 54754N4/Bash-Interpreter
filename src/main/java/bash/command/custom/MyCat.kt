package bash.command.custom

import bash.command.CustomCommand
import java.io.InputStream

class MyCat: CustomCommand {
    constructor(): super("MyCat")
    constructor(input: InputStream?): super("MyCat", input)

    override fun execute() {
        out.appendln("MyCat Input = ")
        var i = 0
        for (line in inputLines) out.appendln("${i++} $line")
    }
}