package command

import io.*

// Adapter class makes custom commands threaded and easier to use
abstract class CustomCommand(command: String): Command(command), Runnable {
    protected lateinit var inputLines: List<String>
    protected lateinit var out: StringBuilder
    protected lateinit var err: StringBuilder
    private lateinit var thread: Thread

    override fun launch() = apply {
        preExecute()
        thread.start()
        postExecute()
    }

    override fun run() = execute()

    override fun preExecute() {
        out = StringBuilder()
        err = StringBuilder()
        thread = Thread(this)
        inputLines = inputStream().readAsString().toLines()
        exit = EXIT_SUCCESS
    }

    override fun postExecute() {
        if (waitFor)
            thread.join()
        if (merge) out.writeToPipe().mergeWith(err.writeToPipe()).writeTo(outputStream())
        else {
            out.writeToPipe().writeTo(outputStream())
            err.writeToPipe().writeTo(errorStream())
        }
    }
}