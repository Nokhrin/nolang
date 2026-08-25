grammar DynamicTyped;

program     : NL* (declaration NL*)* EOF ;
declaration : 'def' funcSignature               #funcDecl
            | returnType ID '=' expression      #typedVarDecl
            | ID '=' expression                 #untypedVarDecl
            | statement                         #statementDecl
            ;

funcSignature : returnType ID '(' parameters ')' block    #typedFuncWithParams
              | returnType ID '(' ')' block               #typedFuncNoParams
              | ID '(' parameters ')' block         #untypedFuncWithParams
              | ID '(' ')' block                    #untypedFuncNoParams
              ;
arguments  : expression (',' expression)* ;
parameters : parameter (',' parameter)* ;
parameter  : parameterType ID               #typedParameter
           | ID                             #untypedParameter
           ;

parameterType : INT_TYPE
              | REAL_TYPE
              | BOOL_TYPE
              ;

returnType : INT_TYPE
           | REAL_TYPE
           | BOOL_TYPE
           | VOID_TYPE
           ;

statement : expression
          | breakStatement
          | continueStatement
          | whileStatement
          | ifStatement
          | return
          | block
          ;

ifStatement : 'if' expression 'then' NL* statement ('else' NL* statement)   #ifElseStat
            | 'if' expression 'then' NL* statement                          #ifStat
            ;

breakStatement : 'break' ;
continueStatement : 'continue' ;
whileStatement : 'while' expression 'do' NL* statement ;
block: '{' NL* (statement NL*)* '}' ;
return : 'return' expression        #returnValue
       | 'return'                   #returnVoid
       ;

expression  : assignment ;
assignment  : ID '=' assignment     #assignVar
            | ternary               #assignExpr
            ;
ternary     : logicalOr '?' ternary ':' ternary    #ternaryExpr
            | logicalOr                            #orExpr
            ;
logicalOr   : logicalAnd ('OR' logicalAnd)* ;
logicalAnd  : comparison ('AND' comparison)*       #andExpr
            ;
comparison  : term ('==' | '!=' |
                 '>' | '<' | '>=' | '<=') term     #comparisonExpr
            | term                                 #additiveExpr
            ;

term    :  factor (('+' | '-') factor)*  #multiplicativeExpr
        ;
factor  :  unary (('*' | '/') unary)*
        ;
unary   : 'NOT' unary               #unaryNotExpression
        | '+' unary                 #unaryPlusExpression
        | '-' unary                 #unaryMinusExpression
        | atom '^' unary            #powerExpression
        | atom '!'                  #factorialExpression
        | atom                      #atomExpression
        ;

atom        : '|' assignment '|'    #absoluteAtom
            | '(' ternary ')'       #parenthesesAtom
            | ID '(' arguments ')'  #callWithArgsAtom
            | ID '(' ')'            #callNoArgsAtom
            | FLOAT                 #floatAtom
            | INT                   #intAtom
            | BOOL                  #boolAtom
            | VOID                  #voidAtom
            | ID                    #variableAtom
            ;

INT_TYPE : 'int' ;
REAL_TYPE : 'float' ;
BOOL_TYPE : 'bool' ;
VOID_TYPE : 'void' ;
BOOL : 'true' | 'false' ;
VOID : 'void_value' ;
ID    : ALPHA ('_' | ALPHA | DIGIT)* ;
FLOAT : DIGIT+ '.' DIGIT* ([Ee] ('+' | '-')? DIGIT+)?
      | DIGIT+ [Ee] ('+' | '-')? DIGIT+
      | '.' DIGIT+ ;
INT : DIGIT+ ;
fragment
ALPHA : [a-zA-Z] ;
fragment
DIGIT : [0-9] ;
NL : '\r'? '\n' ;
COMMENT : '//' ~[\r\n]* -> skip ;
WS : [ \t]+ -> skip ;
