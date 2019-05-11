package bash.command

import bash.io.StreamTransfer
import bash.io.mergeWith
import bash.io.startAndWait
import java.io.InputStream

class NativeCommand: Command {
    constructor(command:String): super(command)
    constructor(command: String, input: InputStream?): super(command, input)

    override fun execute() {
        builder.command(*wrapCommand(command))
            .directory(workingDir)
        val process = builder.start()
        if (input != null)
            StreamTransfer(input!!, process.outputStream).startAndWait()
        exit = EXIT_SUCCESS
        if (waitFor)
            exit = process.waitFor()
        if (merge)
            output = process.inputStream.mergeWith(process.errorStream)
        else {
            output = process.inputStream
            error = process.errorStream
        }
    }
}