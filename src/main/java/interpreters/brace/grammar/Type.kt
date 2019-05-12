package interpreters.brace.grammar

enum class Type(val string: String) {
    WORD("WORD"), EXPR_START("{"), EXPR_END("}"),
    COMMA(","), RANGE("src/main"), NUMBER("NUMBER"),
    EOF("EOF")
}