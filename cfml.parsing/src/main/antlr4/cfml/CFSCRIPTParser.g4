/*
 [The "BSD licence"]
 Copyright (c) 2013 Tom Everett
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

parser grammar  CFSCRIPTParser;

options { tokenVocab=CFSCRIPTLexer; }


//Note: need case insensitive stream: http://www.antlr.org/wiki/pages/viewpage.action?pageId=1782

	
////--- cfscript grammar rules

scriptBlock
  : componentDeclaration
  | ( element )* endOfScriptBlock
  ; 

componentDeclaration
  : COMPONENT componentAttribute* componentGuts //-> ( COMPDECL componentAttribute* componentGuts)
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
  : (accessType)? (typeSpec)? lc=FUNCTION identifier LEFTPAREN (parameterList)? RIGHTPAREN functionAttribute* compoundStatement //-> ^( FUNCDECL[$lc] (functionAccessType)? (functionReturnType)? ^(FUNCTION_NAME identifier) (parameterList)? functionAttribute* compoundStatement )
  ;

/*functionAccessType
  //: (PUBLIC | PRIVATE | REMOTE | PACKAGE) (functionReturnType|identifier)
  : (accessType functionReturnType? FUNCTION identifier) //=> accessType -> ^(FUNCTION_ACCESS accessType) 
//  : ((PUBLIC | PRIVATE | REMOTE | PACKAGE) functionReturnType? FUNCTION identifier) => lc=(PUBLIC | PRIVATE | REMOTE | PACKAGE -> ^(FUNCTION_ACCESS[$lc])
  ;

functionReturnType
  : (typeSpec FUNCTION) //=> typeSpec -> ^( FUNCTION_RETURNTYPE typeSpec )
  ;
  */

accessType
	:PUBLIC | PRIVATE | REMOTE | PACKAGE
	;

typeSpec
  : type
  | identifier ( DOT ( identifier | reservedWord ) )*
  | STRING_LITERAL
  ;
  
parameterList
  : parameter ( COMMA parameter)*
  |
  ;
  
parameter
  : (REQUIRED)? (parameterType)? identifier ( EQUALSOP baseExpression )? parameterAttribute* //-> ^(FUNCTION_PARAMETER (REQUIRED)? (parameterType)? identifier (EQUALSOP baseExpression)? parameterAttribute*)
  ;
  
parameterType
  : typeSpec //-> ^( PARAMETER_TYPE typeSpec )
  ;

componentAttribute
  : identifier (COLON identifier)? op=EQUALSOP baseExpression //-> ^(COMPONENT_ATTRIBUTE identifier (COLON identifier)? baseExpression)
  ;
//i=identifier EQUALSOP^ v=baseExpression
  
functionAttribute
  : identifier op=EQUALSOP baseExpression //-> ^(FUNCTION_ATTRIBUTE[$op] identifier baseExpression)
  ;
  
parameterAttribute
  : identifier EQUALSOP baseExpression //-> ^(PARAMETER_ATTRIBUTE identifier baseExpression)
  | identifier
  ;
  
compoundStatement
  : LEFTCURLYBRACKET ( statement )* RIGHTCURLYBRACKET
  ;
  
componentGuts
  : LEFTCURLYBRACKET ( element )* RIGHTCURLYBRACKET
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
  |   localAssignmentExpression SEMICOLON
  |   assignmentExpression SEMICOLON
  |   SEMICOLON // empty statement
  ;
   
condition
  : LEFTPAREN baseExpression RIGHTPAREN
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
  : FOR LEFTPAREN ( localAssignmentExpression | assignmentExpression )? SEMICOLON ( assignmentExpression )? SEMICOLON  ( assignmentExpression )? RIGHTPAREN statement
  | FOR LEFTPAREN forInKey IN assignmentExpression RIGHTPAREN statement
  ;
  
forInKey
  : VAR? identifier ( DOT ( identifier | reservedWord ) )*
  ;

tryCatchStatement
  : TRY statement ( catchCondition )* finallyStatement?
  ;
  
catchCondition
  : CATCH LEFTPAREN typeSpec identifier RIGHTPAREN compoundStatement
  ;

finallyStatement
  : FINALLY compoundStatement
  ;
  
constantExpression
  : LEFTPAREN constantExpression RIGHTPAREN
  | MINUS ( INTEGER_LITERAL | FLOATING_POINT_LITERAL  )
  | INTEGER_LITERAL
  | FLOATING_POINT_LITERAL
  | STRING_LITERAL
  | BOOLEAN_LITERAL
//  | NULL
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
    | 
    ( DEFAULT COLON ( statement )* ) 
  ;

tagOperatorStatement
  //: INCLUDE^ compoundStatement SEMICOLON  (poundSignReader kills this :-/)
  : includeStatement
  | importStatement
  | abortStatement
  | throwStatement
  | RETHROW SEMICOLON //-> ^(RETHROWSTATEMENT)
  | exitStatement
  | paramStatement
  | propertyStatement
  | lockStatement
  | threadStatement
  | transactionStatement
  | cfmlfunctionStatement
  ; 

// component  

includeStatement
  : INCLUDE baseExpression* SEMICOLON  //-> ^(INCLUDE  baseExpression* ) 
  ;

importStatement
  : IMPORT componentPath (DOT '*')? SEMICOLON 
  ;

transactionStatement
  : lc=TRANSACTION (paramStatementAttributes)* (compoundStatement)? //-> ^(TRANSACTIONSTATEMENT[$lc] (paramStatementAttributes)* (compoundStatement)?)
  ;
  
cfmlfunctionStatement
  : cfmlFunction (param)* (compoundStatement)?//-> ^(CFMLFUNCTIONSTATEMENT cfmlFunction (param)* (compoundStatement)?)
  ;
  
cfmlFunction
  : SAVECONTENT
  | HTTP 
  | FILE 
  | PROPERTY
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
  : lc=SAVECONTENT p=paramStatementAttributes cs=compoundStatement -> ^(CFMLFUNCTIONSTATEMENT[$lc] paramStatementAttributes compoundStatement)
  ;
*/

lockStatement
  : lc=LOCK p=paramStatementAttributes cs=compoundStatement //-> ^(LOCKSTATEMENT[$lc] paramStatementAttributes compoundStatement)
  ;

threadStatement
  : lc=THREAD p=paramStatementAttributes (compoundStatement)? //-> ^(THREADSTATEMENT[$lc] paramStatementAttributes (compoundStatement)?)
  ;

abortStatement
  : lc=ABORT SEMICOLON //-> ^(ABORTSTATEMENT[$lc])
  | lc=ABORT memberExpression SEMICOLON //-> ^(ABORTSTATEMENT[$lc] memberExpression)
  ;

throwStatement
  : lc=THROW SEMICOLON //-> ^(THROWSTATEMENT[$lc])
  | lc=THROW memberExpression SEMICOLON //-> ^(THROWSTATEMENT[$lc] memberExpression)
  ;

exitStatement
  : lc=EXIT SEMICOLON //-> ^(EXITSTATEMENT[$lc])
  | lc=EXIT memberExpression SEMICOLON //-> ^(EXITSTATEMENT[$lc] memberExpression)
  ;

paramStatement
  : lc=PARAM paramStatementAttributes  //-> ^(PARAMSTATEMENT[$lc] paramStatementAttributes)
  ;
  
propertyStatement
  : lc=PROPERTY paramStatementAttributes  //-> ^(PROPERTYSTATEMENT[$lc] paramStatementAttributes)
  ;
  
paramStatementAttributes
  : ( param )+
  ;
  
param
  : i=identifier EQUALSOP v=baseExpression
  ;


//--- expression engine grammar rules (a subset of the cfscript rules)
  
expression 
	: localAssignmentExpression EOF
	|	assignmentExpression EOF
	;
	
localAssignmentExpression 
	:	VAR identifier ( EQUALSOP baseExpression )? //-> ^( VARLOCAL identifier ( EQUALSOP baseExpression )? ) 
	;

assignmentExpression 
  : baseExpression ( ( EQUALSOP | PLUSEQUALS | MINUSEQUALS | STAREQUALS | SLASHEQUALS | MODEQUALS | CONCATEQUALS ) baseExpression )?
  ;

baseExpression
	:	ternary
	| impliedExpression
	| equivalentExpression
	| xorExpression
	| orExpression
	| andExpression
	| notExpression
	| equalityExpression
	| concatenationExpression
	| additiveExpression
	| modExpression
	| intDivisionExpression
	| multiplicativeExpression
	| powerOfExpression
	| unaryExpression
	;
	
impliedExpression
	:	equivalentExpression ( IMP equivalentExpression )
	;

ternary
   : equivalentExpression QUESTIONMARK baseExpression COLON baseExpression //-> ^(TERNARY equivalentExpression localAssignmentExpression localAssignmentExpression)
   ;

equivalentExpression
	:	xorExpression ( EQV xorExpression )
	;

xorExpression
	:	orExpression ( XOR orExpression )
	;
	
orExpression
	:	andExpression ( ( OR | OROPERATOR ) andExpression )
	;
	
andExpression
	:	notExpression ( ( AND | ANDOPERATOR ) notExpression )
	;
	
notExpression
	:	( NOT | NOTOP ) equalityExpression 
	;

equalityExpression
    : concatenationExpression
      ( equalityOperator1 concatenationExpression )
    ;

equalityOperator1
    :
    //IS //-> ^(EQ)
    //|   EQUALSEQUALSOP //-> ^(EQ)
       EQ //-> ^(EQ)
    |   LT //-> ^(LT)
    //|   LESSTHAN //-> ^(LT)
    |   LTE //-> ^(LTE)
    //|   LESSTHANEQUALS //-> ^(LTE)
    |   GT //-> ^(GT)
    |   GTE //-> ^(GTE)
    |   NEQ //-> ^(NEQ)
    //|   NOTEQUALS //-> ^(NEQ)
    //|   EQUAL //-> ^(EQ)
    //|   EQUALS //-> ^(EQ)
    |   CONTAINS //-> ^(CONTAINS)
    ;
    
/*equalityOperator2
    :   LESS THAN //-> ^(LT)
    |   GREATER THAN //-> ^(GT)
    //|   NOT EQUAL  //-> ^(NEQ)
    //|   IS NOT //-> ^(NEQ)
    ;
 
equalityOperator3 
    :   lc=DOES NOT CONTAIN //-> ^(DOESNOTCONTAIN[$lc])
    ;

equalityOperator5
    :   LESS THAN OR EQUAL TO //-> ^(LTE)
    |   GREATER THAN OR EQUAL TO //-> ^(GTE)
    ; */
    
concatenationExpression
	:	additiveExpression ( CONCAT additiveExpression )
	;
	
additiveExpression
	:	modExpression ( (PLUS|MINUS) modExpression )
	;

modExpression
	:	intDivisionExpression  ( MOD intDivisionExpression )
	;
	
intDivisionExpression
	:	multiplicativeExpression ( BSLASH multiplicativeExpression )
	;

multiplicativeExpression
	:	powerOfExpression ( (STAR|SLASH) powerOfExpression )
	;
	
powerOfExpression
	:	unaryExpression ( POWER unaryExpression )
	;
	
unaryExpression
	: MINUS memberExpression //-> ^(MINUS memberExpression)
	| PLUS memberExpression //-> ^(PLUS memberExpression)
	| MINUSMINUS memberExpression //-> ^(MINUSMINUS memberExpression) 
	| PLUSPLUS memberExpression //-> ^(PLUSPLUS memberExpression)
	| newComponentExpression (DOT primaryExpressionIRW (LEFTPAREN argumentList ')')*)*
  | memberExpression MINUSMINUS //-> ^(POSTMINUSMINUS memberExpression)
  | memberExpression PLUSPLUS //-> ^(POSTPLUSPLUS memberExpression)
  | memberExpression 
  | primaryExpression
	;
	
memberExpression
	:	POUND_SIGN memberExpressionB POUND_SIGN
	| memberExpressionB
	;
	
memberExpressionB
  : ( primaryExpression //-> primaryExpression 
  ) // set return tree to just primary
  ( 
  : DOT primaryExpressionIRW LEFTPAREN argumentList ')' //-> ^(JAVAMETHODCALL $memberExpressionB primaryExpressionIRW argumentList )
    |  LEFTPAREN argumentList RIGHTPAREN //-> ^(FUNCTIONCALL $memberExpressionB argumentList)
    | LEFTBRACKET baseExpression RIGHTBRACKET //-> ^(LEFTBRACKET $memberExpressionB baseExpression)
    | DOT primaryExpressionIRW //-> ^(DOT $memberExpressionB primaryExpressionIRW)
  )+
  ;
  


memberExpressionSuffix
  : indexSuffix
  | propertyReferenceSuffix
  ;

propertyReferenceSuffix
  : DOT LT* identifier
  ;

indexSuffix
  : LEFTBRACKET  LT* primaryExpression  LT* ']' 
  ; 
  
primaryExpressionIRW
	:	primaryExpression
	| reservedWord
	;
	
reservedWord
  : CONTAINS //| IS | EQUAL | GE | LE | EQUALS  
  | EQ | NEQ | GT | LT | GTE
  | LTE | NOT | AND
  | OR | XOR | EQV | IMP | MOD
//  | NULL 
  | cfscriptKeywords 
  ;

argumentList
  : argument (COMMA argument)*
  | //-> ^(EMPTYARGS)
  ;

argument
  : ( identifier COLON baseExpression //-> ^( COLON identifier baseExpression ) 
  )
  | ( identifier EQUALSOP baseExpression //-> ^( COLON identifier baseExpression ) 
  )
  | baseExpression 
  ;

identifier
	:	SCOPE? (COMPONENT
	| IDENTIFIER
  //| DOES 
  | CONTAIN
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
  | //{!scriptMode}?//=> 
  	cfscriptKeywords )
	;

type
  : NUMERIC
  | STRING
  | BOOLEAN
  | COMPONENT
  | ANY
  | ARRAY
  | STRUCT
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
  | FINALLY
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
//	|	NULL
	| '(' LT* assignmentExpression LT* ')'
	|	identifier
	;

implicitArray
  : lc=LEFTBRACKET implicitArrayElements? RIGHTBRACKET //-> ^(IMPLICITARRAY[$lc] implicitArrayElements?) 
  ;
  
implicitArrayElements
  : baseExpression ( COMMA baseExpression )*
  ;
  
implicitStruct
  : lc=LEFTCURLYBRACKET implicitStructElements? RIGHTCURLYBRACKET //-> ^(IMPLICITSTRUCT[$lc] implicitStructElements?)
  ;
  
implicitStructElements
  : implicitStructExpression ( COMMA implicitStructExpression )*
  ;

implicitStructExpression
  : implicitStructKeyExpression ( COLON | EQUALSOP ) baseExpression 
  ;
  
implicitStructKeyExpression
  : identifier ( DOT ( identifier | reservedWord ) )*
  | additiveExpression ( CONCAT additiveExpression )*
  | STRING_LITERAL
  ;

newComponentExpression
  : NEW componentPath LEFTPAREN argumentList ')'
  ;
  
componentPath
  : STRING_LITERAL
  | identifier ( DOT identifier )*
  ;