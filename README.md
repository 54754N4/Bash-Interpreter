# Bash 
bash_grammar.txt : has grammar extracted from yacc file used by Bash itself, just for reference

# Interpreters
For parsing :
- arguments
- brace expansions
- an extended bash arithmetic
- the bash language interpreter itself (not-finished)

# Supports native and custom commands.
For now only pipes and redirections

# Note
The `XFC-Bash-Interpreter` java repository has the same goal but has more features implemented, including:
- an improved brace expansion parser
- support for parameter and variable expansion
- in-memory native and custom command IO, without using intermediary files
- more frequent updates
