package testing

import command.CustomCommand
import command.CustomCommand.Companion.customsPackage
import org.reflections.scanners.Scanner
import reflection.retrieveKotlinSubtypes
import kotlin.reflect.full.createInstance

fun main() = testCreation()

private fun testCreation() {
    val customs = retrieveKotlinSubtypes<CustomCommand>(customsPackage, 12)
    val command = customs.iterator().next()
    println(command.createInstance().name)
}

private fun testReflection() {
    for (`class` in retrieveKotlinSubtypes<CustomCommand>(customsPackage))
        println(`class`)
}

private fun testScanners() {
    for (`class` in supportedScanners())
        println(`class`)
}

private fun supportedScanners() = retrieveKotlinSubtypes<Scanner>("org.reflections")