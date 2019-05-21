package testing

import io.readAsString
import java.lang.ProcessBuilder.Redirect

fun main() = testCmdIO()

private fun testCmdIO() {
    val file = createTempFile()
    file.outputStream().bufferedWriter().use { it.write("Hello\nWorld\nShit\n"); it.flush() }
    println("before = \n"+file.inputStream().readAsString())
    val builder = ProcessBuilder().command("cmd.exe","/c","find /I \"Shit\"")
    builder.redirectInput(Redirect.from(file))
    builder.redirectOutput(Redirect.to(file))
    val proc = builder.start()
    proc.waitFor()
    println("after = \n"+file.inputStream().readAsString())
}