package testing

import bash.command.*
import bash.command.custom.MyCat
import bash.command.custom.SpitShit
import bash.io.*
import java.io.File

fun main() {
    testCustomIOPipe()
}
fun testCustomIOPipe() {
    val shit = SpitShit()
    shit.launch()
    val cat = MyCat(shit.output)
    cat.launch()
    println("CMD OUTPUT =============")
    println(cat.output.readAsString())
    println("ERROR ==============")
    println(cat.error.readAsString())
}
fun testInput() {
    val file = File("test.txt")
    val dir = NativeCommand("type", file.inputStream()) // i wish windows had cat..
    dir.merge = true
    dir.launch()
    println(dir.output.readAsString())
}
fun testCustomInPipe() {
    val dir = NativeCommand("dir /b")
    dir.launch()
    val cat = MyCat(dir.output)
    cat.launch()
    println("CMD OUTPUT =============")
    println(cat.output.readAsString())
    println("ERROR ==============")
    println(cat.error.readAsString())
}
fun testPipe() {
    val dir = NativeCommand("dir /b")
    dir.launch()
    val find = NativeCommand("find /I \"test\"", dir.output)
    find.launch()
    println("CMD OUTPUT =============")
    println(find.output.readAsString())
    println("ERROR ==============")
    println(find.error.readAsString())
}

fun testBridges() {
    val file = File("test.txt")
    val fOut = file.inputStream()
    val bridge = Bridge()
    val bridge1 = Bridge()
    fOut.readOutputTo(bridge.pin)
    bridge.pout.readOutputTo(bridge1.pin)
    bridge1.pout.readOutputTo(STD.output.outputStream())
    STD.output.printAll()
}

fun testBridge1() {
    val file = File("test.txt")
    val fOut = file.inputStream()
    val bridge = Bridge()
    fOut.readOutputTo(bridge.pin)
    bridge.pout.readOutputTo(STD.output.outputStream())
    STD.output.printAll()
}

fun testBridge0() {
    val file = File("test.txt")
    val fOut = file.inputStream()
    val bridge= Bridge()
    bridge.pin.writeInputFrom(fOut)
    STD.output.outputStream().writeInputFrom(bridge.pout)
    STD.output.printAll()
}