package command

import interpreters.args.grammar.Args
import interpreters.args.grammar.Parser
import io.*

// Adapter class makes custom commands threaded and easier to use
abstract class CustomCommand(val name: String, val args: String = "") : Command("$name $args".trim()), Runnable {
    private lateinit var thread: Thread
    private lateinit var out: StringBuilder
    private lateinit var err: StringBuilder
    protected lateinit var inputLines: List<String>
    protected lateinit var arguments: Args

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
        arguments = Parser().parse(args)
        exit = EXIT_SUCCESS
    }

    override fun postExecute() {
        if (waitFor)
            thread.join()
        if (merge) (out+err).writeToPipe().writeTo(outputStream())
        else {
            out.writeToPipe().writeTo(outputStream())
            err.writeToPipe().writeTo(errorStream())
        }
    }

    // Convenience methods
    private operator fun StringBuilder.plus(other: StringBuilder): String = "$this$newLine$other"

    protected fun out(string: String) { out.append(string) }
    protected fun outln(string: String) = out("$string$newLine")
    protected fun err(string: String) { err.append(string) }
    protected fun errln(string: String) = err("$string$newLine")
    protected fun hasArgs(vararg needles: String): Boolean {
        var match = false
        for (needle in needles)
            match = match || arguments.table.any { (key) -> key == needle }
        return match
    }

    // Debug

    protected fun dumpArgumentsTable() {
        for (entry in arguments.table)
            outln("${entry.key}=${entry.value}")
    }

    companion object {
        val newLine = System.getProperty("line.separator")  // system dependant new line
    }
}