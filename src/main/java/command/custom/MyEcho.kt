package command.custom

import command.CustomCommand

class MyEcho(args:String = ""): CustomCommand("echo", args) {
    override fun execute() = outln(arguments.input)
}