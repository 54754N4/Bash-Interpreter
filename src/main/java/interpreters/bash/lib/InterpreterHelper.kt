package interpreters.bash.lib

import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun main() {
    println(Paths.get("test").toFile().exists())
}

fun redirectionFile(filename: String): Path {
    val path = Paths.get(filename)
    if (!Files.exists(path)) throw FileNotFoundException("$filename doesn't exist")
    return path
}