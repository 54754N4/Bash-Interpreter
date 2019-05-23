package command.custom

import command.CustomCommand
import java.io.File
import java.io.InputStream
import java.nio.file.Path

class MyCat(args: String = ""): CustomCommand("cat", args) {
    override fun execute() {
        for (line in inputLines) outln(line)
    }
}