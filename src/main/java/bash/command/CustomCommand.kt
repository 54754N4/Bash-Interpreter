package bash.command

import bash.io.mergeWith
import bash.io.readAsString
import bash.io.toLines
import bash.io.writeToPipe
import java.io.InputStream
// Adapter class makes custom commands threaded and easier to use
abstract class CustomCommand: Command, Runnable {
    protected lateinit var inputLines: List<String>
    protected lateinit var out: StringBuilder
    protected lateinit var err: StringBuilder
    private lateinit var thread: Thread

    constructor(command: String): super(command)
    constructor(command: String, input: InputStream?): super(command, input)

    override fun launch() {
        preExecute()
        thread.start()
        postExecute()
    }

    override fun run() = execute()

    override fun preExecute() {
        out = StringBuilder()
        err = StringBuilder()
        thread = Thread(this)
        inputLines = input?.readAsString()?.toLines() ?: listOf("")
        exit = EXIT_SUCCESS
    }

    override fun postExecute() {
        if (waitFor)
            thread.join()
        output = out.writeToPipe()
        error = err.writeToPipe()
        if (merge)
            output = output!!.mergeWith(error!!)
    }
}