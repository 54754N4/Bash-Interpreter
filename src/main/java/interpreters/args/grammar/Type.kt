package interpreters.args.grammar

enum class Type(val string: String) {
    WORD("WORD"), EQUALS("="), NAMED("--"), UNNAMED("-"), INPUT("INPUT")
}