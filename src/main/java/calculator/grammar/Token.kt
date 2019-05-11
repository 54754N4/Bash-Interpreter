package calculator.grammar

data class Token(val type: Type, val value: String) {
    constructor(type: Type): this(type, type.string)
}