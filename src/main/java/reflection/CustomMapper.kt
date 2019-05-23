package reflection

import command.CustomCommand
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

const val customsPackage = "command.custom"
val customs = mutableMapOf<String, KClass<out CustomCommand>>()


fun initCustomsTable() {
    val customsClasses = retrieveKotlinSubtypes<CustomCommand>(customsPackage, 12)
    val iterator = customsClasses.iterator()
    var kClass: KClass<out CustomCommand>
    while (iterator.hasNext()) {
        kClass = iterator.next()
        customs[kClass.createInstance().name] = kClass
    }
}

fun main() {
    initCustomsTable()
    for (e in customs)
        println(e.key + "=" + e.value)
}
