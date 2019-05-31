package io

import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun File.printAll() = forEachLine { println(it) }

fun Path.printAll() = toFile().printAll()
fun Path.outputStream(append: Boolean) = FileOutputStream(toFile(), append)
fun Path.outputStream() = outputStream(false)
fun Path.inputStream() = toFile().inputStream()

class STD {
    companion object {
        private const val stdPath = "io/std%s"
        var input = Paths.get(stdPath.format("in"))
        var output = Paths.get(stdPath.format("out"))
        var error = Paths.get(stdPath.format("err"))
        var root = Paths.get(".")

        init {
            if (!Files.exists(input)) input = Files.createFile(input)
            if (!Files.exists(output)) output = Files.createFile(output)
            if (!Files.exists(error)) error = Files.createFile(error)
            if (!Files.exists(root)) root = Files.createFile(root)
        }
    }
}

class FDManager {
    companion object {
        private const val FD_PATH_FORMAT = "io/fd%d"
        private const val FD_MAX = 100
        val fds = mutableMapOf<Int, Path>()

        init {
            fds[0] = STD.input
            fds[1] = STD.output
            fds[2] = STD.error
            var path: Path
            for (i in 3..FD_MAX) {
                path = Paths.get(FD_PATH_FORMAT.format(i))
                if (!Files.exists(path))
                    Files.createFile(path)
                fds[i] = path
            }
        }
    }
}