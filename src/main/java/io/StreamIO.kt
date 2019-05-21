package io

import java.io.*
import kotlin.concurrent.thread

class Bridge() {
    val pin = PipedOutputStream()
    val pout = PipedInputStream(pin)

    constructor(input: String): this() {
        if (input != "") pin.readFrom(input)
    }

    constructor(input: InputStream): this() {
        if (input.available() > 0) pin.readFrom(input)
    }

    constructor(file: File): this() {
        if (file.length() != 0L) pin.readFrom(file)
    }
}

class StreamTransfer(private val from: InputStream, private val to: OutputStream): Thread() {
    override fun run() {
        if (from.available() > 0) {
            to.bufferedWriter().use { writer ->
                from.bufferedReader().readLines().forEach { line -> writer.appendln(line) }
            }
        }
    }
}

// Extensions

fun Thread.startAndWait() = start().also { join() }

fun String.toLines(): List<String> = split("\r\n")   // on linux remove CR I think
fun String.writeToPipe(): PipedInputStream = Bridge(this).pout

fun StringBuilder.toLines(): List<String> = toString().toLines()
fun StringBuilder.writeToPipe(): PipedInputStream = toString().writeToPipe()

fun InputStream.writeTo(file: File) = StreamTransfer(this, file.outputStream()).startAndWait()
fun InputStream.writeTo(outputStream: OutputStream) = StreamTransfer(this, outputStream).startAndWait()
fun InputStream.mergeWith(with: InputStream): InputStream = if (available() != 0 && with.available() != 0) Bridge(SequenceInputStream(this, with)).pout else this

fun InputStream?.readAsString(): String {
    if (this == null)
        return ""
    val result = StringBuilder()
    if (available() > 0)
        thread(true) { this.bufferedReader().readLines().forEach { result.appendln(it) } }.join()
    return result.toString()
}

fun OutputStream.readFrom(file: File) = StreamTransfer(file.inputStream(), this).startAndWait()
fun OutputStream.readFrom(inputStream: InputStream) = StreamTransfer(inputStream, this).startAndWait()
fun OutputStream.readFrom(input: String) = thread(true) {
    this.bufferedWriter().use {
        it.write(input)
        it.flush()
    }
}.join()