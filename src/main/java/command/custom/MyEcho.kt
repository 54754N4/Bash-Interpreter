package command.custom

import command.CustomCommand

class MyEcho(args:String = ""): CustomCommand("MyEcho", args) {
    override fun execute() = outln(arguments.input)
}