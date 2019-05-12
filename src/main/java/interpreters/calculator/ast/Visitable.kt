package interpreters.calculator.ast

interface Visitable {
    fun accept(visitor: Visitor): Double
}