package calculator.grammar

// Tokens
enum class Type(val string: String) {
    NUMBER("NUMBER"), PLUS("+"), MINUS("-"), FLIP("~"), MULTIPLY("*"),
    DIVIDE("/"), FLOOR_DIVIDE("//"), MODULUS("%"), LEFT_SHIFT("<<"),
    RIGHT_SHIFT(">>"), AND("&"), OR("|"), XOR("^"),
    POWER("**"), SQRT("SQRT"), EXP("EXP"), LN("LN"), LOG("LOG"),
    COS("COS"), SIN("SIN"), TAN("TAN"), ABS("ABS"),
    LEFT_PARENTHESIS("("), RIGHT_PARENTHESIS(")"), EOF("EOF"),

    PI("PI"), ARC_COS("ACOS"), ARC_COS_HYPERBOLIC("ACOSH"),
    ARC_SIN("ASIN"), ARC_SIN_HYPERBOLIC("ASINH"), ARC_TAN("ATAN"),
    ARC_TAN_HYPERBOLIC("ATANH"), COS_HYPERBOLIC("COSH"), SIN_HYPERBOLIC("SINH"),
    TAN_HYPERBOLIC("TANH"), CEIL("CEIL"), FLOOR("FLOOR"), ROUND("ROUND")
}