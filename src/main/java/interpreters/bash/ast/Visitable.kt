package interpreters.bash.ast

import command.Command

interface Visitable {
    fun accept(visitor: Visitor): Command
}