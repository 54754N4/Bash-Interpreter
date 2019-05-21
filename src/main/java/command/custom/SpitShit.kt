package command.custom

import command.CustomCommand
import java.io.File
import java.io.InputStream
import java.nio.file.Path

class SpitShit: CustomCommand("SpitShit") {

    override fun execute() {
        for (i in 0..10) {
            out.appendln("output line $i")
            err.appendln("error line $i")
        }
    }
}