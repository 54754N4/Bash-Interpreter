package interpreters.brace.grammar

enum class Type(val string: String) {
    CHAR("CHAR"), WORD("WORD"), EXPR_START("{"), EXPR_END("}"),
    COMMA(","), RANGE(".."), NUMBER("NUMBER"),
    EOF("EOF"), EMPTY("")
}