MESSAGE ::= BOARD | BOOM | HELP | HELLO
BOARD ::= LINE+
LINE ::= (SQUARE SPACE)* SQUARE NEWLINE
SQUARE ::= "-" | "F" | COUNT | SPACE
SPACE ::= " "
NEWLINE ::= "\n" | "\r" "\n"?
COUNT ::= [1-8]
BOOM ::= "BOOM!" NEWLINE
HELP ::= [^\r\n]+ NEWLINE
HELLO ::= "Welcome to Minesweeper. Players: " N " including you. Board: "
X " columns by " Y " rows. Type 'help' for help." NEWLINE
N ::= INT
X ::= INT
Y ::= INT
INT ::= "-"? [0-9]+