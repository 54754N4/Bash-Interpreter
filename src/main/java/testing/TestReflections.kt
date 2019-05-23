package testing

import command.CustomCommand
import org.reflections.scanners.Scanner
import reflection.retrieveKotlinSubtypes

fun main() = testReflection()

private fun testReflection() {
    for (`class` in retrieveKotlinSubtypes<CustomCommand>())
        println(`class`)
}

private fun testScanners() {
    for (`class` in supportedScanners())
        println(`class`)
}

private fun supportedScanners() = retrieveKotlinSubtypes<Scanner>("org.reflections")