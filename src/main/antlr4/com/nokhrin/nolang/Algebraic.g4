grammar Algebraic;

program     : NL* statement (NL+ statement)* NL* EOF     #programWithStatements
            | NL* EOF                                    #emptyProgram
            ;
statement   : assignment
            ;
assignment  : ID '=' term             #assignStatement
            | term                    #termStatement
            ;
term        : factor (addOp factor)*
            ;
factor      : unary (mulOp unary)*
            ;
unary       : unaryOp unary           #unaryExpression
            | postfix '^' unary       #powerExpression
            | postfix                 #postfixExpression
            ;
postfix     : atom postfixOp*
            ;
atom        : '|' term '|'            #absoluteAtom
            | '(' term ')'            #parenthesesAtom
            | NUM                     #numberAtom
            | ID '(' arguments ')'    #funcCallAtom
            | ID                      #variableAtom
            ;
arguments   : term (',' term)*
            ;

mulOp       : '*' | '/' ;
addOp       : '+' | '-' ;
unaryOp     : '+' | '-' ;
postfixOp   : '!' | '%' ;

ID          : ALPHA (ALPHA | DIGIT)* ;
NUM         : DIGIT+ ('.' DIGIT*)? | '.' DIGIT+ ;
fragment
DIGIT       : [0-9] ;
fragment
ALPHA       : [a-zA-Z_] ;
NL          : '\r'? '\n' ;
WS          : [ \t]+ -> skip  ;
