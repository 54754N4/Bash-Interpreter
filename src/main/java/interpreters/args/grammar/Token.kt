package interpreters.args.grammar

// WORD EQUALS DOUBLE_QUOTES SPACE NAMED UNNAMED

data class Token(val type: Type, val value: String) {
    constructor(type: Type): this(type, type.string)
}