package io

import java.io.*

fun File.printAll() = forEachLine { println(it) }

class STD {
    companion object {
        private const val stdPath = "io/std%s"
        val input = File(stdPath.format("in"))
        val output = File(stdPath.format("out"))
        val error = File(stdPath.format("err"))
        val root = File(".")

        init {
            input.createNewFile()
            output.createNewFile()
            error.createNewFile()
        }
    }
}

class FDManager {
    companion object {
        private const val FD_PATH_FORMAT = "io/fd%d"
        private const val FD_MAX = 100
        val fds = mutableMapOf<Int, File>()
        init {
            var currentFile: File
            for (i in 1..FD_MAX) {
                currentFile = File(FD_PATH_FORMAT.format(i))
                currentFile.createNewFile()
                fds[i] = currentFile
            }
        }
    }
}