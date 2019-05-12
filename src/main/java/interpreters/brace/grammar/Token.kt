package interpreters.brace.grammar

import interpreters.brace.grammar.Type

data class Token(val type: Type, val value: String) {
    constructor(type: Type):this(type, type.string)
}