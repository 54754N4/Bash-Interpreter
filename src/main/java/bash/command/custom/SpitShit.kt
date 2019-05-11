package bash.command.custom

import bash.command.CustomCommand
import java.io.InputStream

class SpitShit: CustomCommand {
    constructor(): super("SpitShit")
    constructor(input: InputStream): super("SpitShit", input)

    override fun execute() {
        out.appendln("My Output = ")
        for (i in 0..10) {
            out.appendln("line $i")
            err.appendln("line $i")
        }
    }
}