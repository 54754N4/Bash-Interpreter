package command.custom

import command.CustomCommand
import java.io.File
import java.io.InputStream
import java.nio.file.Path

class MyCat: CustomCommand("MyCat") {

    override fun execute() {
        for (line in inputLines) out.appendln(line)
    }
}