/* 
 *  Copyright (C) 2000 - 2010 TagServlet Ltd
 *  This file is part of Open BlueDragon (OpenBD) CFML Server Engine.
 *  
 *  OpenBD is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  Free Software Foundation,version 3.
 *  
 *  OpenBD is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with OpenBD.  If not, see http://www.gnu.org/licenses/
 *  
 *  Additional permission under GNU GPL version 3 section 7
 *  
 *  If you modify this Program, or any covered work, by linking or combining 
 *  it with any of the JARS listed in the README.txt (or a modified version of 
 *  (that library), containing parts covered by the terms of that JAR, the 
 *  licensors of this Program grant you additional permission to convey the 
 *  resulting work. 
 *  README.txt @ http://www.openbluedragon.org/license/README.txt
 *  
 *  http://www.openbluedragon.org/
 */

grammar CFScript;

@parser::header { 
} 


@lexer::header { 
}
 
@parser::members { 

}


@lexer::members {
  
}

// Alter code generation so catch-clauses get replace with
// this action.
@parser::rulecatch {
}


//Note: need case insensitive stream: http://www.antlr.org/wiki/pages/viewpage.action?pageId=1782

WS	:	(' '|'\t'|('\r'?'\n'))+ ->skip;

LINE_COMMENT :
            '//'
            ( ~('\n'|'\r') )*
            ( '\n'|'\r'('\n')? )?
             ;

ML_COMMENT
    :   '/*' (.|[\n\r])*? '*/';

BOOLEAN_LITERAL
	:	T R U E
	|	F A L S E 
	;

STRING_LITERAL
	: '"' DoubleStringCharacter* '"'
	| '\'' SingleStringCharacter* '\''
	;
 
fragment DoubleStringCharacter
	: ~('"')
	| '""'	
	;

fragment SingleStringCharacter
	: ~('\'')
	| '\'\''	
	;

fragment LETTER	
	: '\u0024'
	| '\u0041'..'\u005a'
	| '\u005f'
	| '\u0061'..'\u007a'
	| '\u00c0'..'\u00d6'
	| '\u00d8'..'\u00f6'
	| '\u00f8'..'\u00ff'
	| '\u0100'..'\u1fff'
	| '\u3040'..'\u318f'
	| '\u3300'..'\u337f'
	| '\u3400'..'\u3d2d'
	| '\u4e00'..'\u9fff'
	| '\uf900'..'\ufaff';


fragment DIGIT 	
	: '\u0030'..'\u0039'
	| '\u0660'..'\u0669'
	| '\u06f0'..'\u06f9'
	| '\u0966'..'\u096f'
	| '\u09e6'..'\u09ef'
	| '\u0a66'..'\u0a6f'
	| '\u0ae6'..'\u0aef'
	| '\u0b66'..'\u0b6f'
	| '\u0be7'..'\u0bef'
	| '\u0c66'..'\u0c6f'
	| '\u0ce6'..'\u0cef'
	| '\u0d66'..'\u0d6f'
	| '\u0e50'..'\u0e59'
	| '\u0ed0'..'\u0ed9'
	| '\u1040'..'\u1049';

// define all the operators/reserved words before the identifier


// Case-Insensitive Characters
A: [Aa];
B: [Bb];
C: [Cc];
D: [Dd];
E: [Ee];
F: [Ff];
G: [Gg];
H: [Hh];
I: [Ii];
J: [Jj];
K: [Kk];
L: [Ll];
M: [Mm];
N: [Nn];
O: [Oo];
P: [Pp];
Q: [Qq];
R: [Rr];
S: [Ss];
T: [Tt];
U: [Uu];
V: [Vv];
W: [Ww];
X: [Xx];
Y: [Yy];
Z: [Zz];
HASH: '#';

NULL: N U L L;

// Operators
CONTAINS:	C O N T A I N S;
CONTAIN: C O N T A I N;
DOES: D O E S;
IS:	I S;
GT: G T;
GE: G E;
GTE: G T E;
LTE: L T E;
LT: L T;
LE: L E;
EQ: E Q;
EQUAL: E Q U A L;
EQUALS: E Q U A L S;
NEQ: N E Q;
LESS: L E S S;
THAN: T H A N;
GREATER: G R E A T E R;
OR: O R;
TO: T O;
IMP: I M P;
EQV: E Q V;
XOR: X O R;
AND: A N D;
NOT: N O T;
MOD: M O D;
VAR: V A R;
NEW: N E W;

// cfscript
IF: I F;
ELSE: E L S E;
BREAK: B R E A K;
CONTINUE: C O N T I N U E;
FUNCTION: F U N C T I O N;
RETURN: R E T U R N;
WHILE: W H I L E;
DO: D O;
FOR: F O R;
IN: I N;
TRY: T R Y;
CATCH: C A T C H;
SWITCH: S W I T C H;
CASE: C A S E;
DEFAULT: D E F A U L T;
FINALLY: F I N A L L Y;

SCRIPTOPEN: '<' C F S C R I P T '>';
SCRIPTCLOSE: '</' C F S C R I P T '>'; 

// operators
DOT: '.';
STAR: '*';
SLASH: '/';
BSLASH: '\\';
POWER: '^';
PLUS: '+';
PLUSPLUS: '++';
MINUS: '-';
MINUSMINUS: '--';
MODOPERATOR: '%';
CONCAT: '&';
EQUALSEQUALSOP: '==';
EQUALSOP: '=';
PLUSEQUALS: '+=';
MINUSEQUALS: '-=';
STAREQUALS: '*=';
SLASHEQUALS: '/=';
MODEQUALS: '%=';
CONCATEQUALS: '&=';
COLON: ':';
NOTOP: '!'; 
SEMICOLON: ';';
OROPERATOR: '||';
ANDOPERATOR: '&&';
LEFTBRACKET: '[';
RIGHTBRACKET: ']';
LEFTPAREN: '(';
RIGHTPAREN: ')';
LEFTCURLYBRACKET: '{';
RIGHTCURLYBRACKET: '}';
QUESTIONMARK: '?';

// tag operators
INCLUDE: I N C L U D E;
IMPORT: I M P O R T;
ABORT: A B O R T;
THROW: T H R O W;
RETHROW: R E T H R O W;
EXIT: E X I T;
PARAM: P A R A M;
PROPERTY: P R O P E R T Y;
LOCK: L O C K;
THREAD: T H R E A D;
TRANSACTION: T R A N S A C T I O N;

// cfmlfunction (tags you can call from script)
LOCATION: L O C A T I O N;
SAVECONTENT: S A V E C O N T E N T;
HTTP: H T T P;
FILE: F I L E;
DIRECTORY: D I R E C T O R Y;
LOOP: L O O P; 
SETTING: S E T T I N G;
QUERY: Q U E R Y;

// function related
PRIVATE: P R I V A T E;
PUBLIC: P U B L I C;
REMOTE: R E M O T E;
PACKAGE: P A C K A G E;
REQUIRED: R E Q U I R E D;
COMPONENT: C O M P O N E N T;

SCOPE
    :   T H I S DOT
    |   L O C A L DOT
    |   V A R I A B L E S DOT
    ;

IDENTIFIER 
    : HASH SCOPE? LETTER (LETTER|DIGIT)* HASH
    | SCOPE? LETTER (LETTER|DIGIT)*
    ;
	
INTEGER_LITERAL
  : DecimalDigit+
  ;


fragment DecimalDigit
  : ('0'..'9')
  ;

FLOATING_POINT_LITERAL
  : DecimalDigit+ '.' DecimalDigit* ExponentPart?
  | '.' DecimalDigit+ ExponentPart?
  | DecimalDigit+ ExponentPart?
  ;

fragment ExponentPart
  : ('e'|'E') ('+'|'-')? DecimalDigit+
  ;
 
//--- cfscript grammar rules



comment
    : LINE_COMMENT | ML_COMMENT;

scriptBlock
  : SCRIPTOPEN
  | componentDeclaration
  | ( element )* endOfScriptBlock
  ; 


componentDeclaration
  : COMPONENT componentAttribute* componentBlock
  ;


endOfScriptBlock
  : SCRIPTCLOSE 
  | EOF
  ;
  

element
  : functionDeclaration
  | statement
  ;


functionDeclaration
  : (functionAccessType)? (functionReturnType)? lc=FUNCTION identifier LEFTPAREN (parameterList)? RIGHTPAREN functionAttribute* compoundStatement
  ;


functionAccessType
  : (accessType? functionReturnType? FUNCTION identifier)
  ;


functionReturnType
  : (typeSpec FUNCTION)
  ;


accessType
  :PUBLIC | PRIVATE | REMOTE | PACKAGE
  ;


typeSpec
  : identifier ( DOT ( identifier | reservedWord ) )*
  | STRING_LITERAL
  ;

functionCallStatement
    : IDENTIFIER LEFTPAREN RIGHTPAREN 
    | IDENTIFIER LEFTPAREN parameterList RIGHTPAREN
    ;
  

parameterList
  : parameter ( COMMA parameter )*
  ;
  

parameter
  : REQUIRED parameterType identifier EQUALSOP impliesExpression
  | parameterType identifier EQUALSOP impliesExpression
  | identifier EQUALSOP impliesExpression
  | STRING_LITERAL
  | identifier
  ;


parameterType
  : typeSpec
  ;


componentAttribute
  : identifier (COLON identifier)? op=EQUALSOP impliesExpression
  ;


functionAttribute
  : identifier op=EQUALSOP impliesExpression
  ;
  

compoundStatement
  : LEFTCURLYBRACKET ( statement )* RIGHTCURLYBRACKET
  ;


componentBlock
  : LEFTCURLYBRACKET (comment)* ( element )* RIGHTCURLYBRACKET
  ;
  

statement
  :   tryCatchStatement
  |   ifStatement
  |   whileStatement
  |   doWhileStatement
  |   forStatement
  |   switchStatement
  |   CONTINUE SEMICOLON
  |   BREAK SEMICOLON
  |   returnStatement
  |   tagOperatorStatement
  |   compoundStatement 
  |   localAssignmentExpression SEMICOLON?
  |   SEMICOLON // empty statement
  ;
   

condition
  : LEFTPAREN localAssignmentExpression RIGHTPAREN
  ;
  

returnStatement
  : RETURN SEMICOLON
  | RETURN assignmentExpression SEMICOLON
  ;
  

ifStatement
  : IF condition statement ( ELSE statement )?
  ;


whileStatement
  : WHILE condition statement
  ;
 

doWhileStatement
  : DO statement WHILE condition SEMICOLON
  ;
  

forStatement
  : FOR LEFTPAREN VAR? ( assignmentExpression )? SEMICOLON ( assignmentExpression )? SEMICOLON  ( assignmentExpression )? RIGHTPAREN statement
  | FOR LEFTPAREN forInKey IN assignmentExpression RIGHTPAREN statement
  ;
  

forInKey
  : VAR? identifier ( DOT ( identifier | reservedWord ) )*
  ;


tryCatchStatement
  : TRY statement ( catchCondition )* finallyStatement?
  ;
  

catchCondition
  : CATCH LEFTPAREN exceptionType identifier RIGHTPAREN compoundStatement
  ;


finallyStatement
  : FINALLY compoundStatement
  ;


exceptionType
  : identifier ( DOT ( identifier | reservedWord ) )*
  | STRING_LITERAL
  ;
  

constantExpression
  : LEFTPAREN constantExpression RIGHTPAREN
  | MINUS ( INTEGER_LITERAL | FLOATING_POINT_LITERAL  )
  | INTEGER_LITERAL
  | FLOATING_POINT_LITERAL
  | STRING_LITERAL
  | BOOLEAN_LITERAL
  | NULL
  ;
  

switchStatement
  : SWITCH condition LEFTCURLYBRACKET
    ( 
      caseStatement    
    )* 
    
    RIGHTCURLYBRACKET
  ;


caseStatement
  : ( CASE constantExpression COLON ( statement )* ) 
  | ( DEFAULT COLON ( statement )* ) 
  ;


tagOperatorStatement
  : includeStatement
  | IMPORT componentPath SEMICOLON
  | abortStatement
  | throwStatement
  | RETHROW SEMICOLON (RETHROWSTATEMENT)?
  | exitStatement
  | paramStatement
  | propertyStatement
  | lockStatement
  | threadStatement
  | transactionStatement
  | cfmlfunctionStatement
  ;


includeStatement
  : INCLUDE impliesExpression* SEMICOLON
  ;


transactionStatement
  : lc=TRANSACTION p=paramStatementAttributes (compoundStatement)?
  ;
  

cfmlfunctionStatement
  : cfmlFunction (param)* (compoundStatement)?
  ;
  
cfmlFunction
  : LOCATION
  | SAVECONTENT
  | HTTP 
  | FILE 
  | DIRECTORY
  | LOOP 
  | SETTING
  | QUERY
  ;

/*

cfmlfunctionStatement
  : savecontentStatement
  ;

savecontentStatement
  : lc=SAVECONTENT p=paramStatementAttributes cs=compoundStatement  
  ;
*/

lockStatement
  : lc=LOCK p=paramStatementAttributes cs=compoundStatement  
  ;

threadStatement
  : lc=THREAD p=paramStatementAttributes (compoundStatement)?  
  ;

abortStatement
  : lc=ABORT SEMICOLON  
  | lc=ABORT memberExpression SEMICOLON  
  ;

throwStatement
  : lc=THROW SEMICOLON  
  | lc=THROW memberExpression SEMICOLON  
  ;

exitStatement
  : lc=EXIT SEMICOLON  
  | lc=EXIT memberExpression SEMICOLON  
  ;

paramStatement
  : lc=PARAM paramStatementAttributes   
  ;
  
propertyStatement
  : lc=PROPERTY paramStatementAttributes   
  ;
  
paramStatementAttributes
  : ( param )+
  ;
  
param
  : i=identifier EQUALSOP  v=impliesExpression
  ;


//--- expression engine grammar rules (a subset of the cfscript rules)
  
expression 
	: localAssignmentExpression EOF 
	;
	
localAssignmentExpression 
	:	VAR identifier ( EQUALSOP impliesExpression )?
	|	assignmentExpression
	;

assignmentExpression 
  : impliesExpression ( ( EQUALSOP | PLUSEQUALS | MINUSEQUALS | STAREQUALS | SLASHEQUALS | MODEQUALS | CONCATEQUALS )  impliesExpression )?
  ;

impliesExpression
	:	ternary
	| equivalentExpression ( IMP  equivalentExpression )*
    | functionCallStatement
	;

ternary
//   : equivalentExpression QUESTIONMARK localAssignmentExpression COLON localAssignmentExpression
   : equivalentExpression QUESTIONMARK localAssignmentExpression COLON localAssignmentExpression
   ;

equivalentExpression
	:	xorExpression ( EQV  xorExpression )*
	;

xorExpression
	:	orExpression ( XOR  orExpression )*
	;
	
orExpression
	:	andExpression ( ( OR | OROPERATOR )  andExpression )*
	;
	
andExpression
	:	notExpression ( ( AND | ANDOPERATOR )  notExpression )*
	;
	
notExpression
	:	( NOT  | NOTOP  )? equalityExpression 
	;

equalityExpression
    : concatenationExpression
      ( ( equalityOperator5  | equalityOperator3  |  equalityOperator2  | equalityOperator1  ) concatenationExpression )* 
    ;

equalityOperator1
    : 	IS  
    |   EQUALSEQUALSOP  
    |   LT  
    |   '<'  
    |   LTE  
    |   '<='  
    |   LE  
    |   GT  
    |   '>'  
    |   GTE  
    |   '>='  
    |   GE  
    |   EQ  
    |   NEQ  
    |   '!='  
    |   EQUAL  
    |   EQUALS  
    |   CONTAINS  
    ;
    
equalityOperator2
    :   LESS THAN  
    |   GREATER THAN  
    |   NOT EQUAL   
    |   IS NOT  
    ;

equalityOperator3
    :   lc=DOES NOT CONTAIN  
    ;

equalityOperator5
    :   LESS THAN OR EQUAL TO  
    |   GREATER THAN OR EQUAL TO  
    ;
    
concatenationExpression
	:	additiveExpression ( CONCAT  additiveExpression )*
	;
	
additiveExpression
	:	modExpression ( (PLUS |MINUS ) modExpression )*
	;

modExpression
	:	intDivisionExpression  ( (MOD|MODOPERATOR)  intDivisionExpression )* 
	;
	
intDivisionExpression
	:	multiplicativeExpression ( BSLASH  multiplicativeExpression )*
	;

multiplicativeExpression
	:	powerOfExpression ( (STAR |SLASH ) powerOfExpression )*
	;
	
powerOfExpression
	:	unaryExpression ( POWER  unaryExpression )*
	;
	
unaryExpression
	: MINUS memberExpression  
	| PLUS memberExpression  
	| MINUSMINUS memberExpression   
	| PLUSPLUS memberExpression  
	| newComponentExpression (DOT primaryExpressionIRW (LEFTPAREN argumentList ')')*)*
  | memberExpression MINUSMINUS  
  | memberExpression PLUSPLUS  
  | memberExpression 
	;
	
memberExpression
	:	'#'  memberExpressionB '#' 
	| memberExpressionB
	;
	
memberExpressionB
  : primaryExpression   // set return tree to just primary
    (DOT primaryExpressionIRW LEFTPAREN argumentList RIGHTPAREN
  | LEFTPAREN argumentList RIGHTPAREN  
  | LEFTBRACKET impliesExpression RIGHTBRACKET  
  | DOT primaryExpressionIRW  
  )*
  ;
  


memberExpressionSuffix
  : indexSuffix
  | propertyReferenceSuffix
  ;

propertyReferenceSuffix
  : DOT LT * identifier
  ;

indexSuffix
  : LEFTBRACKET  LT * primaryExpression  LT * ']'  
  ; 
  
primaryExpressionIRW
	:	primaryExpression
	| reservedWord
	;
	
	
reservedWord
  : CONTAINS | IS | EQUAL 
  | EQ | NEQ | GT | LT | GTE
  | GE | LTE | LE | NOT | AND
  | OR | XOR | EQV | IMP | MOD
  | NULL | EQUALS
  | cfscriptKeywords 
  ;

argumentList
  : argument (COMMA argument)*
  |  
  ;

argument
  : identifier COLON impliesExpression
  | identifier EQUALSOP impliesExpression
  | impliesExpression 
  ;

identifier
  : IDENTIFIER
  | DOES 
  | CONTAIN
  | GREATER 
  | THAN 
  | LESS 
  | VAR
  | TO
  | DEFAULT // default is a cfscript keyword that's always allowed as a var name
  | INCLUDE
  | NEW
  | ABORT
  | THROW
  | RETHROW
  | PARAM
  | EXIT
  | THREAD
  | LOCK
  | TRANSACTION
  | PUBLIC
  | PRIVATE
  | REMOTE
  | PACKAGE
  | REQUIRED
  | cfmlFunction
  ;

cfscriptKeywords
  : IF
  | ELSE
  | BREAK
  | CONTINUE
  | FUNCTION
  | RETURN
  | WHILE
  | DO
  | FOR
  | IN
  | TRY
  | CATCH
  | SWITCH
  | CASE
  | DEFAULT
  | IMPORT
  ;
  
primaryExpression
	:	STRING_LITERAL
	|	BOOLEAN_LITERAL
	| FLOATING_POINT_LITERAL
	|	INTEGER_LITERAL
	| implicitArray
	| implicitStruct
	|	NULL
	| '('  LT * assignmentExpression LT * ')' 
	|	identifier
	;


implicitArray
  : lc=LEFTBRACKET implicitArrayElements? RIGHTBRACKET   
  ;
  
implicitArrayElements
  : impliesExpression ( COMMA impliesExpression )*
  ;
  
implicitStruct
  : lc=LEFTCURLYBRACKET implicitStructElements? RIGHTCURLYBRACKET  
  ;
  
implicitStructElements
  : implicitStructExpression ( COMMA implicitStructExpression )*
  ;

implicitStructExpression
  : implicitStructKeyExpression ( COLON | EQUALSOP )  impliesExpression 
  ;
  
implicitStructKeyExpression
  : identifier ( DOT ( identifier | reservedWord ) )*
  | STRING_LITERAL
  ;

newComponentExpression
  : NEW  componentPath LEFTPAREN argumentList ')' 
  ;
  
componentPath
  : STRING_LITERAL
  | identifier ( DOT identifier )*
  ;
