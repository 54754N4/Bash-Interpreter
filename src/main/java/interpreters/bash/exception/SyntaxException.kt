package interpreters.bash.exception

class SyntaxException(override var message: String): Exception(message)