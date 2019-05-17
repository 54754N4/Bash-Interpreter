package interpreters.bash.grammar

// i think i was overly optimistic when i wrote this..
enum class Type(val string: String) {
    IF("IF"), THEN("THEN"), ELSE("ELSE"), ELIF("ELIF"), FI("FI"), CASE("CASE"), ESAC("ESAC"), FOR("FOR"), SELECT("SELECT"), WHILE("WHILE"), UNTIL("UNTIL"), DO("DO"), DONE("DONE"), FUNCTION("FUNCTION"), COPROC("COPROC"),
    CONDITION_START("["), CONDITION_CMD("CONDITION_CMD"), CONDITION_END("]"), CONDITION_ERROR("CONDITION_ERROR"), //error ? really ?
    IN("IN"), BANG("!"), TIME("TIME"), TIMEOPT("-p"), TIMEIGN("--"),
    WORD("WORD"), QUOTED_WORD("QUOTED_WORD"), ASSIGNMENT("="),
    NUMBER("NUMBER"),
    GREATER(">"), LESS("<"), AND_AND("&&"), OR_OR("||"),
    GREATER_GREATER(">>"), LESS_LESS("<<"), LESS_AND("<&"), LESS_LESS_LESS("<<<"),
    GREATER_AND(">&"), SEMI_SEMI(";;"), SEMI_AND(";&"), SEMI_SEMI_AND(";;&"),
    LESS_LESS_MINUS("<<-"), AND_GREATER("&>"), AND_GREATER_GREATER("&>>"),
    LESS_GREATER("<>"), GREATER_BAR(">|"), OR_AND("|&"),
    AND("&"), OR("|"),
    STRING("STRING"), DASH("-"), NEW_LINE("\\n"), EOF("EOF"),
    LEFT_CURLY_BRACE("{"), RIGHT_CURLY_BRACE("}"),
    LEFT_PARENTHESIS("("), RIGHT_PARENTHESIS(")"),
    //LEFT_SQUARE_BRACKET("["), RIGHT_SQUARE_BRACKET("]"),  // renamed to condition start and end
    SEMI(";"),
    ARITHMETIC_EXPANSION("ARITHMETIC_SUB"), COMMAND_SUBSTITUTION("COMMAND_SUB"),
    PROCESS_SUBSTITUTION("PROCESS_SUBSTITUTION"), BRACE_EXPANSION("BRACE_EXPANSION")
}