package command.custom

import command.CustomCommand
import java.io.InputStream

class SpitShit: CustomCommand {
    constructor(): super("SpitShit")
    constructor(input: InputStream): super("SpitShit", input)

    override fun execute() {
        for (i in 0..10) {
            out.appendln("output line $i")
            err.appendln("error line $i")
        }
    }
}