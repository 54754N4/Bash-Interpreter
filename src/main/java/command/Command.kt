package command

import io.STD
import io.readAsString
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Path

abstract class Command(var command: String) {
    var exit = EXIT_SUCCESS
    var workingDir = STD.root
    var input = STD.input
    var output = STD.output
    var error = STD.error
    var merge = false
    var appendOut = false
    var appendErr = false
    var waitFor = true

    // Builder-like pattern
    fun redirIn(input: Path) = apply { this.input = input }
    fun redirOut(output: Path) = apply { this.output = output }
    fun redirErr(error: Path) = apply { this.error = error }

    fun appendOut(appendOut: Boolean) = apply { this.appendOut = appendOut}
    fun appendErr(appendErr: Boolean) = apply { this.appendErr = appendErr}
    fun merge(merge: Boolean) = apply { this.merge = merge }
    fun merge() = merge(true)

    protected fun outputStream() = FileOutputStream(output.toFile(), appendOut)
    protected fun errorStream() = FileOutputStream(error.toFile(), appendErr)
    protected fun inputStream() = FileInputStream(input.toFile())

    fun outputString() = output.toFile().inputStream().readAsString()
    fun errorString() = error.toFile().inputStream().readAsString()

    open fun launch() = apply {     // can be overridden so we can mess with execution ?
        preExecute()
        execute()
        postExecute()
    }

    infix fun pipe(cmd: Command): Command {
        launch()
        return cmd.redirIn(output)
    }

    protected open fun preExecute() { }
    protected abstract fun execute()
    protected open fun postExecute() { }

    companion object {
        const val EXIT_SUCCESS = 0
        val builder = ProcessBuilder()

        fun environment() = builder.environment()
        fun workingDir() = builder.directory()

        fun wrapCommand(cmd: String) = wrapCommand(*cmd.split(" ").toTypedArray())

        fun wrapCommand(vararg cmd:String): Array<String> {
            val interpreter=
                if (File.separatorChar == '\\')
                    arrayOf("cmd.exe", "/C")
                else
                    arrayOf("/bin/bash", "-c")
            val cmdLen = cmd.size
            val c = Array(interpreter.size + cmdLen) { "" }
            System.arraycopy(interpreter, 0, c, 0, interpreter.size)
            System.arraycopy(cmd, 0, c, interpreter.size, cmdLen)
            return c
        }
    }
}