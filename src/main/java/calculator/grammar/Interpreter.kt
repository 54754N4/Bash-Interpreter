package calculator.grammar

import calculator.ast.BinaryOperator
import calculator.ast.Visitor
import calculator.ast.Composition
import calculator.ast.Number
import calculator.ast.UnaryOperator
import calculator.exception.InterpretationException
import kotlin.math.*

class Interpreter(private val parser: Parser): Visitor {

    fun interpret() = visit(parser.parse())

    private fun error(): Double = throw InterpretationException("Invalid Interpretation @ ${parser.errorMessage()}")

    override fun visit(node: Composition): Double {
        val value = visit(node.expr)
        return when (node.token.type) {
            Type.ABS -> abs(value)
            Type.COS -> cos(value)
            Type.SIN -> sin(value)
            Type.TAN -> tan(value)
            Type.SQRT -> sqrt(value)
            Type.EXP -> exp(value)
            Type.LN -> ln(value)
            Type.LOG -> log(value, 10.0)
            Type.ARC_COS -> acos(value)
            Type.ARC_COS_HYPERBOLIC -> acosh(value)
            Type.ARC_SIN -> asin(value)
            Type.ARC_SIN_HYPERBOLIC -> asinh(value)
            Type.ARC_TAN -> atan(value)
            Type.ARC_TAN_HYPERBOLIC -> atanh(value)
            Type.COS_HYPERBOLIC -> cosh(value)
            Type.SIN_HYPERBOLIC -> sinh(value)
            Type.TAN_HYPERBOLIC -> tanh(value)
            Type.CEIL -> ceil(value)
            Type.FLOOR -> floor(value)
            Type.ROUND -> round(value)
            else -> error()
        }
    }

    override fun visit(node: BinaryOperator): Double {
        return when (node.token.type) {
            Type.PLUS -> visit(node.left) + visit(node.right)
            Type.MINUS -> visit(node.left) - visit(node.right)
            Type.MULTIPLY -> visit(node.left) * visit(node.right)
            Type.DIVIDE -> visit(node.left) / visit(node.right)
            Type.FLOOR_DIVIDE -> (visit(node.left).toInt() / visit(node.right).toInt()).toDouble()
            Type.POWER -> visit(node.left).pow(visit(node.right))
            Type.AND -> (visit(node.left).toInt() and visit(node.right).toInt()).toDouble()
            Type.OR -> (visit(node.left).toInt() or visit(node.right).toInt()).toDouble()
            Type.XOR -> (visit(node.left).toInt() xor visit(node.right).toInt()).toDouble()
            Type.LEFT_SHIFT -> (visit(node.left).toInt() shl visit(node.right).toInt()).toDouble()
            Type.RIGHT_SHIFT -> (visit(node.left).toInt() shr visit(node.right).toInt()).toDouble()
            else -> error()
        }
    }

    override fun visit(node: UnaryOperator): Double {
        return when (node.token.type) {
            Type.PLUS -> visit(node.expr)
            Type.MINUS -> -visit(node.expr)
            Type.FLIP -> visit(node.expr).toInt().inv().toDouble()
            else -> error()
        }
    }

    override fun visit(node: Number): Double {
        if (node.token.value == PI.toString())
            return PI
        return node.token.value.toDouble()
    }
}