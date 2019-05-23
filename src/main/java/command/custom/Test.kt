package command.custom

import command.CustomCommand

class Test(args: String = ""): CustomCommand("Test", args) {
    override fun execute() = dumpArgumentsTable()
}