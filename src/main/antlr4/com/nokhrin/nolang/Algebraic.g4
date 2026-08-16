grammar Algebraic;

program     : NL* (statement (NL+ statement)*)? NL* EOF;
statement   : ID '=' expression | expression;
expression  : term ;
term        : factor (('+' | '-') factor)* ;
factor      : unary (('*' | '/') unary)* ;
unary       : ('+' | '-') unary | power ;
power       : factorial ('^' unary)? ;
factorial   : absolute '!'? ;
absolute    : '|' term '|' | atom ;
atom        : number | variable | parentheses ;
number      : NUM ;
variable    : ID ;
parentheses : '(' expression ')' ;

ID          : ALPHA (ALPHA | DIGIT)* ;
NUM         : DIGIT+ ('.' DIGIT*)? | '.' DIGIT+ ;
fragment
DIGIT       : [0-9] ;
fragment
ALPHA       : [a-zA-Z_] ;
NL          : '\r'? '\n' ;
WS          : [ \t]+ -> skip  ;