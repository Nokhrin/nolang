grammar StaticTyped;

prog : (NEWLINE* decl NEWLINE*)* EOF ;

decl : funcDecl
     | varDecl
     | stat
     ;

stat : assign
     | ifStat
     | whileStat
     | breakStat
     | continueStat
     | returnStat
     | block
     | expr
     ;

funcDecl : 'def' funcSignature ;
funcSignature : type ID '(' funcParameters? ')' block ;
funcParameters : funcParameter (',' funcParameter)* ;
funcParameter: type ID ;
varDecl: type ID ('=' expr)? ;
block: '{' NEWLINE* (stat NEWLINE*)* '}' ;

type: INT_TYPE | FLOAT_TYPE | BOOL_TYPE | VOID_TYPE ;

returnStat : 'return' expr? ;
ifStat : 'if' expr 'then' NEWLINE* stat ('else' NEWLINE* stat)? ;
whileStat : 'while' expr 'do' NEWLINE* stat ;
breakStat : 'break' ;
continueStat : 'continue' ;

call : ID '(' arguments? ')' ;
arguments : expr (',' expr)* ;

expr : assign ;
assign : ID '=' assign
       | ternary
       ;
ternary: or ('?' ternary ':' ternary)? ;
or: and ('OR' and)* ;
and: comparison ('AND' comparison)* ;
comparison: addSub (('==' | '!=' | '>' | '<' | '>=' | '<=') addSub)? ;
addSub:  mulDiv (('+' | '-') mulDiv)* ;
mulDiv:  unary (('*' | '/') unary)* ;
unary : 'NOT' unary      #not
      | '-' unary  #neg
      | '+' unary  #pos
      | atom       #prime
      ;

atom : FLOAT                #float
     | INT                 #int
     | BOOL                 #boolValue
     | call             #funcCall
     | ID                 #id
     | '(' expr ')'       #paren
     ;

INT_TYPE : 'int' ;
FLOAT_TYPE : 'float' ;
BOOL_TYPE : 'boolValue' ;
VOID_TYPE : 'void' ;
BOOL : 'true' | 'false' ;
ID    : ALPHA ('_' | ALPHA | DIGIT)* ;
FLOAT : DIGIT+ '.' DIGIT* ([Ee] ('+' | '-')? DIGIT+)?
      | DIGIT+ [Ee] ('+' | '-')? DIGIT+
      | '.' DIGIT+ ;
INT : DIGIT+ ;
fragment ALPHA : [a-zA-Z] ;
fragment DIGIT : [0-9] ;
NEWLINE : '\r'? '\n' ;
COMMENT : '//' ~[\r\n]* -> skip ;
WS : [ \t]+ -> skip ;
