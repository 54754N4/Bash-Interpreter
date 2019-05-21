package testing

import command.Command
import command.custom.MyCat
import command.custom.SpitShit
import command.NativeCommand
import io.*
import java.nio.file.Paths

fun main() {
//    testNative()
//    testCustom()
//    testInput()
//    testNativePipe()
//    testCustomInPipe()
//    testCustomIOPipe()
//    testCustomMergedIOPipe()
//    testPipeOperator()
}

// Command IO tests

private fun testPipeOperator() = println(
    SpitShit()
        .pipe( MyCat() pipe MyCat() pipe MyCat() pipe MyCat())
        .launch()
        .outputString())

private fun testCustomMergedIOPipe() = println(
    MyCat().redirIn(
        MyCat().redirIn(
            MyCat().redirIn(
                MyCat().redirIn(
                    SpitShit().merge().launch().output)
                    .merge().launch().output)
                .merge().launch().output)
            .merge().launch().output)
        .merge().launch().outputString())

private fun testCustomIOPipe() = println(
    MyCat().redirIn(
        MyCat().redirIn(
            MyCat().redirIn(
                MyCat().redirIn(
                    SpitShit().launch().output)
                .launch().output)
            .launch().output)
        .launch().output)
    .launch().outputString())

private fun testCustomInPipe() = println(
    MyCat()
        .redirIn(
            NativeCommand("dir")
                .launch()
                .output)
        .launch()
        .outputString())

private fun testNativePipe() = println(
    NativeCommand("find /I \"test\"")
        .redirIn(
            NativeCommand("dir")
                .launch()
                .output)
        .launch()
        .outputString())

private fun testInput() = println(
    NativeCommand("sort")
        .redirIn(Paths.get("test.txt"))
        .merge()
        .launch()
        .outputString())

private fun testCustom() = println(
    SpitShit()
        .merge()
        .launch()
        .outputString())

private fun testNative() = println(
    NativeCommand("dir")
        .merge()
        .launch()
        .outputString())

private fun debug(cmd: Command) {
    print(cmd.command+"\t")
    print(cmd.input+"\t")
    print(cmd.output+"\t")
    println(cmd.error+"\t")
}

// Bridge Tests

private fun testBridges() {
    val file = Paths.get("test.txt")
    val fOut = file.inputStream()
    val bridge = Bridge()
    val bridge1 = Bridge()
    fOut.writeTo(bridge.pin)
    bridge.pout.writeTo(bridge1.pin)
    bridge1.pout.writeTo(STD.output.toFile().outputStream())
    STD.output.printAll()
}

private fun testBridge1() {
    val file = Paths.get("test.txt")
    val fOut = file.inputStream()
    val bridge = Bridge()
    fOut.writeTo(bridge.pin)
    bridge.pout.writeTo(STD.output.outputStream())
    STD.output.printAll()
}

private fun testBridge0() {
    val file = Paths.get("test.txt")
    val fOut = file.inputStream()
    val bridge = Bridge()
    bridge.pin.readFrom(fOut)
    STD.output.outputStream().readFrom(bridge.pout)
    STD.output.printAll()
}