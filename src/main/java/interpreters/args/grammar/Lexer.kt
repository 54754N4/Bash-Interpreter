package interpreters.args.grammar

class Lexer(private val text: String) {
    val tokens = retrieveTokens()

    private fun split(): MutableList<String> {
        val words = mutableListOf<String>()
        var skip = false
        var word = StringBuilder()
        for (i in 0 until text.length) {
            when (text[i]) {
                '"' -> {
                    if (i == 0 || text[i-1] != '\\') // if quote is not escaped we toggle skip state
                        skip = !skip
                }
                ' ' -> {
                    if (!skip && word.toString() != "") {
                        words.add(word.toString())
                        word = StringBuilder()
                    } else word.append(text[i])
                }
                else -> word.append(text[i])
            }
        }
        words.add(word.toString())
        return words
    }

    private fun retrieveTokens(): MutableList<Token> {
        val words = split()
        val tokens = mutableListOf<Token>()
        for (word in words) {
            val sanitised = word.trim()
            when {
                sanitised.startsWith("--") -> {
                    tokens.add(Token(Type.NAMED))
                    val equals = sanitised.indexOf('=')
                    if (equals != -1) {
                        tokens.add(Token(Type.WORD, sanitised.substring(2, equals)))
                        tokens.add(Token(Type.EQUALS))
                        tokens.add(Token(Type.WORD, sanitised.substring(equals+1)))
                    } else
                        tokens.add(Token(Type.WORD, sanitised.substring(2)))
                }
                sanitised.startsWith("-") -> {
                    tokens.add(Token(Type.UNNAMED))
                    tokens.add(Token(Type.WORD, sanitised.substring(1)))
                }
                else -> tokens.add(Token(Type.INPUT, word))
            }
        }
        return tokens
    }
}