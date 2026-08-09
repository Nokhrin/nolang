grammar Algebraic;

prog : stat* EOF ;
stat : expr NL
     | expr EOF
     | NL
     ;
expr: ID '=' expr                 # assign
    | sum             # sumExpr
    ;
sum: mul (('+' | '-') mul)* ;
mul: unary (('*' | '/') unary)* ;
unary: ('+' | '-') unary
     | pow
     ;
pow: fact ('^' pow)? ;
fact : prim '!'? ;
prim : NUM                         # num
     | ID                          # id
     | '|' expr '|'                # mod
     | '(' expr ')'                # group
     ;

ID    : LETTER (LETTER | DIGIT)*;
NUM   : DIGIT+ ('.' DIGIT*)? | '.' DIGIT+;
fragment DIGIT : [0-9];
fragment LETTER : [a-zA-Z];
NL : '\r'? '\n' ;
WS : [ \t]+ ->skip;