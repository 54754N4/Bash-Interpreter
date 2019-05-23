package command

import io.*

class NativeCommand(command: String): Command(command) {

    override fun preExecute() {
        builder.command(*wrapCommand(command)).directory(workingDir.toFile())
    }

    override fun execute() {
        val process = builder.start()
        input.inputStream().writeTo(process.outputStream)
        exit = if (waitFor) process.waitFor() else EXIT_SUCCESS
        if (merge) process.inputStream.mergeWith(process.errorStream).writeTo(outputStream())
        else {
            process.inputStream.writeTo(outputStream())
            process.errorStream.writeTo(errorStream())
        }
    }
}