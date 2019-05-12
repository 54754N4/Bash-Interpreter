package interpreters.brace

import interpreters.brace.exception.InvalidBraceExpansionException

class BraceExpansion(input: String) {
    private val brace = BraceIndices(input)
    private val preamble = input.substring(0, brace.start)
    private val middle = input.substring(brace.start+1, brace.end)
    private val postscript = input.substring(brace.end+1, input.length)
    private var start = 'a'
    private var end = 'a'
    private var increment = 1

    fun expand(): String {
        val result = StringBuilder()
        if (middle.contains(","))  {
            val words = middle.split(",")
            for (word in words)
                result.append(preamble).append(word).append(postscript).append(" ")
        } else {
            parseExpression()
            for (c in start..end step increment)
                result.append(preamble).append(c).append(postscript).append(" ")
        }
        return result.trimEnd().toString()
    }

    private fun parseExpression() {
        val tokens = middle.split("src/main")
        println(tokens)
        when (tokens.size) {
            2, 3 -> {
                start = tokens[0].toCharArray()[0]
                end = tokens[1].toCharArray()[0]
                if (tokens.size == 3)
                    increment = Integer.parseInt(tokens[2])
            }
            else -> throw InvalidBraceExpansionException("More than 2 or 3 tokens in brace expression.")
        }
    }

    class BraceIndices(string: String) {
        var start: Int = -1
        var end: Int = -1

        init {
            for (i in 0 until string.length-1) {
                if (start == -1 && string[i] == '{' && (i == 0 || string[i-1] != '\\'))
                    start = i
                else if (string[i] == '}' && string[i-1] != '\\')
                    end = i
            }
            println("$start , $end")
            if (start == -1 || end == -1)
                throw InvalidBraceExpansionException("Malformed brace expansion")
        }
    }
}