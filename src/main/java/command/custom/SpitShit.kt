package command.custom

import command.CustomCommand
import java.io.File
import java.io.InputStream
import java.nio.file.Path

class SpitShit(args: String = ""): CustomCommand("SpitShit", args) {
    override fun execute() {
        for (i in 0..10) {
            outln("output line $i")
            errln("error line $i")
        }
    }
}