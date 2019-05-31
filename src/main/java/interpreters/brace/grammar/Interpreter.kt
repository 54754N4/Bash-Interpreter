package interpreters.brace.grammar

import interpreters.brace.ast.*
import interpreters.brace.lib.generateRange

class Interpreter(val input: String): Visitor {

    fun interpret() = visit(Parser(Lexer(input)).parse())

    //private fun error(): String = throw InvalidBraceExpansionException("Interpreting @ ${parser.errorMessage()}")

    override fun visit(node: Word): String {
        return node.word.value
    }

    override fun visit(preamble: AST, expression: RangeExpression, postscript: AST): String {
        val result = StringBuilder()
        val range = generateRange(expression.start, expression.end, expression.inc)
        val preamble = visit(preamble)
        val postscript = visit(postscript)
        return if (range != null) {
            for (p in preamble.split(" "))
                for (c in range)
                    for (po in postscript.split(" "))
                        result.append(p).append(c).append(po).append(" ")
            result.deleteCharAt(result.length-1).toString()
        } else
            preamble + Type.EXPR_START.string + expression.start.value + Type.RANGE.string + expression.end.value + Type.EXPR_END.string + postscript
    }

    override fun visit(preamble: AST, expression: CSVExpression, postscript: AST): String {
        val result = StringBuilder()
        for (atom in expression.atoms)
            result.append(visit(preamble)).append(visit(atom)).append(visit(postscript)).append(" ")
        return result.deleteCharAt(result.length-1).toString()
    }
}