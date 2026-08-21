grammar Algebraic;

program     : NL* (statement)* EOF;
statement   : assignment NL* ;
assignment  : ID '=' term             #assignStatement
            | term                    #termStatement
            ;
term        : factor (('+' | '-') factor)* ;
factor      : unary (('*' | '/') unary)* ;
unary       : ('+' | '-') unary       #unaryExpression
            | factorial ('^' unary)?  #powerExpression
            ;
factorial   : atom '!'? ;
atom        : '|' term '|'            #absoluteAtom
            | '(' term ')'            #parenthesesAtom
            | NUM                     #numberAtom
            | ID                      #variableAtom
            ;

ID          : ALPHA (ALPHA | DIGIT)* ;
NUM         : DIGIT+ ('.' DIGIT*)? | '.' DIGIT+ ;
fragment
DIGIT       : [0-9] ;
fragment
ALPHA       : [a-zA-Z_] ;
NL          : '\r'? '\n' ;
WS          : [ \t]+ -> skip  ;
