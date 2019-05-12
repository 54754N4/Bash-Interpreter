package interpreters.calculator.ast

interface Visitor {
    fun visit(node: Number): Double
    fun visit(node: UnaryOperator): Double
    fun visit(node: BinaryOperator): Double
    fun visit(node: Composition): Double
    fun visit(ast: AST): Double = when (ast) {  // since we're using "when" it automatically casts
        is Number -> this.visit(ast)
        is UnaryOperator -> this.visit(ast)
        is BinaryOperator -> this.visit(ast)
        is Composition -> this.visit(ast)
    }
}