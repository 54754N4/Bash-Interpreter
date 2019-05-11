package bash.io

import java.io.*
import kotlin.concurrent.thread

class Bridge() {
    val pin = PipedOutputStream()
    val pout = PipedInputStream(pin)

    constructor(input: String): this() {
        if (input != "")
            pin.write(input)
    }
}

class StreamTransfer(private val from: InputStream, private val to: OutputStream):Thread() {
    override fun run() = to.bufferedWriter().use {
        writer -> from.bufferedReader().readLines().forEach { line -> writer.appendln(line).also { println("transferred $line") } }
    }
}

// Extensions

fun Thread.startAndWait() = start().also { join() }

fun String.toLines(): List<String> = split("\r\n")   // on linux remove CR I think

fun String.writeToPipe(): PipedInputStream = Bridge(this).pout

fun StringBuilder.toLines(): List<String> = toString().toLines()

fun StringBuilder.writeToPipe(): PipedInputStream = toString().writeToPipe()

fun InputStream.readOutputTo(file: File) = StreamTransfer(this, file.outputStream()).startAndWait()

fun InputStream.readOutputTo(outputStream: OutputStream) = StreamTransfer(this, outputStream).startAndWait()

fun InputStream.mergeWith(with: InputStream): InputStream {
    val bridge = Bridge()
    thread {
        bridge.pin.bufferedWriter().use {
            it.write(this.bufferedReader().readText())
            it.append(with.bufferedReader().readText())
            it.flush()   // 2h of debugging/stepping lol, and always the same culprit...
        }
    }.join()
    return bridge.pout
}

fun InputStream?.readAsString(): String {
    if (this == null)
        return ""
    val result = StringBuilder()
    if (available() > 0)
        thread { this.bufferedReader().readLines().forEach { result.appendln(it) } }.join()
    return result.toString()
}

fun OutputStream.writeInputFrom(file: File) = StreamTransfer(file.inputStream(), this).startAndWait()

fun OutputStream.writeInputFrom(inputStream: InputStream) = StreamTransfer(inputStream, this).startAndWait()

fun OutputStream.write(input: String) = thread {
    this.bufferedWriter().use {
        it.write(input)
        it.flush()
    }
}.join()