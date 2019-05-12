package interpreters.brace.grammar

import interpreters.brace.ast.*
import interpreters.brace.exception.InvalidBraceExpansionException

class Interpreter(private val parser: Parser): Visitor {

    fun interpret() = visit(parser.parse())

    private fun error(): String = throw InvalidBraceExpansionException("Interpreting @ ${parser.errorMessage()}")

    override fun visit(node: Word): String {
        return node.word.value
    }

    override fun visit(preamble: Token, expression: RangeExpression, postscript: Token): String {
        val result = StringBuilder()
        if (expression.inc != null) {
            val increment = expression.inc.value.toInt()
            val range = expression.start.value.toCharArray()[0].rangeTo(expression.end.value.toCharArray()[0]) step increment
            for (c in range)
                result.append(preamble.value).append(c).append(postscript.value).append(" ")
        }else {
            val range = expression.start.value.toCharArray()[0]..expression.end.value.toCharArray()[0]
            for (c in range)
                result.append(preamble.value).append(c).append(postscript.value).append(" ")
        }
        return result.deleteCharAt(result.length-1).toString()
    }

    override fun visit(preamble: Token, expression: CSVExpression, postscript: Token): String {
        val result = StringBuilder()
        for (atom in expression.atoms)
            result.append(preamble.value).append(visit(atom)).append(postscript.value).append(" ")
        return result.deleteCharAt(result.length-1).toString()
    }
}