package bash.command

import bash.io.STD
import java.io.File
import java.io.InputStream

abstract class Command {
    var command: String
    var waitFor = true
    var merge = false
    var input: InputStream? = null    // InputStream too since we redirect by doing input = output
    var output: InputStream? = null
    var error: InputStream? = null
    var workingDir: File = STD.root
    var exit = EXIT_SUCCESS

    constructor(command: String) {
        this.command = command
    }

    constructor(command: String, input:InputStream?): this(command) {
        this.input = input
    }

    open fun launch() {     // can be overridden so we can mess with execution
        preExecute()
        execute()
        postExecute()
    }

    protected open fun preExecute() {}
    protected abstract fun execute()
    protected open fun postExecute() {}

    companion object {
        const val EXIT_SUCCESS = 0
        val builder = ProcessBuilder()

        fun environment() = builder.environment()
        fun workingDir() = builder.directory()

        fun wrapCommand(cmd: String): Array<String> {
            return wrapCommand(*cmd.split(" ").toTypedArray())
        }
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