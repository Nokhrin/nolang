grammar DynamicTyped;

prog : (NEWLINE* stat NEWLINE*)* EOF ;
stat : assignStat
     | ifStat
     | whileStat
     | breakStat
     | continueStat
     | returnStat
     | funcDef
     | block
     | expr
     ;

funcDef : 'def' funcSignature ;
funcSignature : type? ID '(' formalParameters? ')' block ;
formalParameters : type? ID (',' type? ID)* ;

block: '{' NEWLINE* (stat NEWLINE*)* '}' ;
returnStat : 'return' expr? ;

assignStat : type? ID '=' expr ;

type : INT_TYPE
     | FLOAT_TYPE
     | BOOL_TYPE
     | VOID_TYPE
     ;

ifStat : 'if' expr 'then' NEWLINE* stat ('else' NEWLINE* stat)? ;
whileStat : 'while' expr 'do' NEWLINE* stat ;
breakStat : 'break' ;
continueStat : 'continue' ;

callExpr : ID '(' arguments? ')' ;
arguments : expr (',' expr)* ;

expr: ternary ;
ternary: or ('?' ternary ':' ternary)? ;
or: and ('OR' and)* ;
and: comp ('AND' comp)* ;
comp: addSub (('==' | '!=' | '>' | '<' | '>=' | '<=') addSub)? ;
addSub:  mulDiv (('+' | '-') mulDiv)* ;
mulDiv:  unary (('*' | '/') unary)* ;
unary : 'NOT' unary      #not
      | '-' unary  #neg
      | '+' unary  #pos
      | atom       #prime
      ;

atom : FLOAT                #float
     | INT                 #int
     | BOOL                 #bool
     | VOID                 #void
     | callExpr             #funcCall
     | ID                 #id
     | '(' expr ')'       #paren
     ;

INT_TYPE : 'int' ;
FLOAT_TYPE : 'float' ;
BOOL_TYPE : 'bool' ;
VOID_TYPE : 'void' ;
BOOL : 'true' | 'false' ;
VOID : 'void_value' ;
ID    : ALPHA ('_' | ALPHA | DIGIT)* ;
FLOAT : DIGIT+ '.' DIGIT* ([Ee] ('+' | '-')? DIGIT+)?
      | DIGIT+ [Ee] ('+' | '-')? DIGIT+
      | '.' DIGIT+ ;
INT : DIGIT+ ;
fragment ALPHA : [a-zA-Z] ;
fragment DIGIT : [0-9] ;
NEWLINE : '\r'? '\n' ;
COMMENT : '//' ~[\r\n]* -> skip ;
WS : [ \t\n]+ -> skip ;