package interpreters.brace.grammar

import interpreters.brace.ast.*
import interpreters.brace.exception.InvalidBraceExpansionException
import interpreters.brace.lib.generateRange

class Interpreter(private val parser: Parser): Visitor {

    fun interpret() = visit(parser.parse())

    private fun error(): String = throw InvalidBraceExpansionException("Interpreting @ ${parser.errorMessage()}")

    override fun visit(node: Word): String {
        return node.word.value
    }

    override fun visit(preamble: Token, expression: RangeExpression, postscript: Token): String {
        val result = StringBuilder()
        val range = generateRange(expression.start, expression.end, expression.inc)
        if (range != null)
            for (c in range)
                result.append(preamble.value).append(c).append(postscript.value).append(" ")
        return result.deleteCharAt(result.length-1).toString()
    }

    override fun visit(preamble: Token, expression: CSVExpression, postscript: Token): String {
        val result = StringBuilder()
        for (atom in expression.atoms)
            result.append(preamble.value).append(visit(atom)).append(postscript.value).append(" ")
        return result.deleteCharAt(result.length-1).toString()
    }
}