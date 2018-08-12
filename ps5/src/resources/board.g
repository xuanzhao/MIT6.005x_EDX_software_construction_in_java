FILE ::= BOARD LINE+
BOARD := X SPACE Y NEWLINE
LINE ::= (VAL SPACE)* VAL NEWLINE
VAL ::= 0 | 1
X ::= INT
Y ::= INT
SPACE ::= " "
NEWLINE ::= "\n" | "\r" "\n"?
INT ::= [0-9]+


