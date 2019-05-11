package bash.command

val variables = hashMapOf("var" to "I love you baby")
var historyExpand = true
val history = arrayListOf<String>()

fun variableExpansion(input:String):String {
    var string = input
    for (variable in variables)
        if (string.contains(variable.key))
            string = string.replace(variable.key, variable.value)
    return string
}

fun historyExpansion(input:String):String {
    if (!historyExpand)
        return input
    // TODO history expand somehow, read docs ===================================================
    return input
}

