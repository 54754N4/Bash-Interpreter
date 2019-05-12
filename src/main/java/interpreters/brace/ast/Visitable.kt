package interpreters.brace.ast

interface Visitable {
    fun accept(visitor: Visitor): String
}