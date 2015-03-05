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
  : accessType? typeSpec? FUNCTION identifier 
  	LEFTPAREN parameterList? RIGHTPAREN 
  	functionAttribute* body=compoundStatement 
  ;
anonymousFunctionDeclaration
  : accessType? typeSpec? FUNCTION //identifier? 
  	LEFTPAREN parameterList? RIGHTPAREN 
  	functionAttribute* body=compoundStatement 
  ;

accessType
	:PUBLIC | PRIVATE | REMOTE | PACKAGE
	;

typeSpec
  : type
  | multipartIdentifier
  | stringLiteral
  ;
  
stringLiteral
  :  STRING_LITERAL;
  
 
parameterList
  : parameter ( COMMA parameter)*
  |
  ;
  
parameter
  : (REQUIRED)? (parameterType)? identifier ( EQUALSOP startExpression )? parameterAttribute* //-> ^(FUNCTION_PARAMETER (REQUIRED)? (parameterType)? identifier (EQUALSOP baseExpression)? parameterAttribute*)
  ;
  
parameterType
  : typeSpec //-> ^( PARAMETER_TYPE typeSpec )
  ;

componentAttribute
  : (prefix=identifier COLON)? id=identifier op=EQUALSOP startExpression //-> ^(COMPONENT_ATTRIBUTE identifier (COLON identifier)? baseExpression)
  ;
//i=identifier EQUALSOP^ v=baseExpression
   
functionAttribute
  : identifier op=EQUALSOP startExpression //-> ^(FUNCTION_ATTRIBUTE[$op] identifier baseExpression)
  ;
  
parameterAttribute
  : identifier EQUALSOP startExpression //-> ^(PARAMETER_ATTRIBUTE identifier baseExpression)
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
  |   continueStatement SEMICOLON
  |   breakStatement SEMICOLON
  |   returnStatement
  |   tagOperatorStatement
  |   compoundStatement 
  |   localAssignmentExpression SEMICOLON
  |   assignmentExpression SEMICOLON
  |   startExpression SEMICOLON
  |   SEMICOLON // empty statement
  ;
  
breakStatement 
  :BREAK ;
  
continueStatement 
  :CONTINUE ;  
   
condition
  : LEFTPAREN (baseExpression | compareExpression) RIGHTPAREN
  ;
  
returnStatement
  : RETURN SEMICOLON
  | RETURN startExpression SEMICOLON
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
  : FOR LEFTPAREN ( localAssignmentExpression | initExpression=assignmentExpression )? SEMICOLON 
  	  ( condExpression=startExpression )? SEMICOLON  
  	  ( incrExpression=startExpression | incrExpression2=assignmentExpression )? RIGHTPAREN statement
  | FOR LEFTPAREN forInKey IN inExpr=startExpression RIGHTPAREN statement
  ;
  
startExpression:
  ternary | baseExpression | compareExpression;
  
forInKey
  : VAR? multipartIdentifier
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
  : ( CASE constantExpression COLON statement*) 
    | 
    ( DEFAULT COLON statement* ) 
  ;

tagOperatorStatement
  //: INCLUDE^ compoundStatement SEMICOLON  (poundSignReader kills this :-/)
  : includeStatement
  | importStatement
  | abortStatement
  | throwStatement
  | rethrowStatment //-> ^(RETHROWSTATEMENT)
  | exitStatement
  | paramStatement
  | propertyStatement
  | lockStatement
  | threadStatement
  | transactionStatement
  | cfmlfunctionStatement
  ; 
  
rethrowStatment:
  lc=RETHROW SEMICOLON ;

// component  

includeStatement
  : lc=INCLUDE baseExpression SEMICOLON  //-> ^(INCLUDE  baseExpression* ) 
  ;

importStatement
  : lc=IMPORT componentPath (DOT all='*')? SEMICOLON 
  ;

transactionStatement
  : lc=TRANSACTION (paramStatementAttributes)? (compoundStatement)? //-> ^(TRANSACTIONSTATEMENT[$lc] (paramStatementAttributes)* (compoundStatement)?)
  ;
  
cfmlfunctionStatement
  : cfmlFunction (paramStatementAttributes)? (compoundStatement)?//-> ^(CFMLFUNCTIONSTATEMENT cfmlFunction (param)* (compoundStatement)?)
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
  : i=identifier EQUALSOP startExpression
  ;


//--- expression engine grammar rules (a subset of the cfscript rules)
  
expression 
	: localAssignmentExpression EOF
	|	assignmentExpression EOF
	|   startExpression EOF
	;
	
localAssignmentExpression 
	:	VAR identifier ( EQUALSOP startExpression )? //-> ^( VARLOCAL identifier ( EQUALSOP baseExpression )? ) 
	;

assignmentExpression 
  : left=startExpression
     ( ( EQUALSOP | PLUSEQUALS | MINUSEQUALS | STAREQUALS | SLASHEQUALS | MODEQUALS | CONCATEQUALS )  
     	right = startExpression
     )
  ;

baseExpression
	:	
/* 	| impliedExpression
	| equivalentExpression
	| xorExpression
	| orExpression
	| andExpression
	| notExpression
	| equalityExpression*/
	 concatenationExpression
	| additiveExpression
	| modExpression
	| intDivisionExpression
	| multiplicativeExpression
	| powerOfExpression
	| unaryExpression
	;
	
compareExpression
	: impliedExpression
	| equivalentExpression
	| xorExpression
	| orExpression
	| andExpression
	| notExpression
	| notNotExpression
	| equalityExpression
	;	
	
ternary
   : (compareExpression | unaryExpression) QUESTIONMARK startExpression COLON startExpression //-> ^(TERNARY equivalentExpression localAssignmentExpression localAssignmentExpression)
   ;

impliedExpression
	:	baseExpression IMP startExpression
	;

equivalentExpression
	:	baseExpression EQV startExpression 
	;

xorExpression
	:	baseExpression XOR startExpression 
	;
	
orExpression
	:	baseExpression  ( OR | OROPERATOR ) startExpression 
	;
	
andExpression
	:	baseExpression  ( AND | ANDOPERATOR ) startExpression 
	;
	
notExpression
	:	( NOT | NOTOP ) startExpression 
	;
	
notNotExpression
	:	NOTNOTOP  startExpression 
	;	

equalityExpression
    : baseExpression
      ( equalityOperator1 startExpression )
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
	:	unaryExpression ( CONCAT startExpression )
	;
	
additiveExpression
	:	unaryExpression ( (PLUS|MINUS) startExpression )
	;

modExpression
	:	unaryExpression  ( MOD startExpression )
	;
	
intDivisionExpression
	:	unaryExpression ( BSLASH startExpression )
	;

multiplicativeExpression
	:	unaryExpression ( (STAR|SLASH) startExpression )
	;
	
powerOfExpression
	:	unaryExpression ( POWER startExpression )
	;
	
unaryExpression
	: MINUS startExpression //-> ^(MINUS memberExpression)
	| PLUS startExpression //-> ^(PLUS memberExpression)
	| MINUSMINUS startExpression //-> ^(MINUSMINUS memberExpression) 
	| PLUSPLUS startExpression //-> ^(PLUSPLUS memberExpression)
	| identifier (DOT primaryExpressionIRW (LEFTPAREN argumentList RIGHTPAREN)*)*
  | identifier MINUSMINUS //-> ^(POSTMINUSMINUS memberExpression)
  | identifier PLUSPLUS //-> ^(POSTPLUSPLUS memberExpression)
  | primaryExpression
  | parentheticalExpression
//  | functionCall
  | memberExpression
  ;
	

	
memberExpression
	:	innerExpression
	| (functionCall | newComponentExpression| memberExpressionB)
	;
	
innerExpression:
	POUND_SIGN baseExpression POUND_SIGN;
	
memberExpressionB
  : ( //primaryExpression
    firstidentifier=identifier 
  	| parentheticalExpression//-> primaryExpression 
  	| newComponentExpression
  ) // set return tree to just primary
  ( 
   DOT javaCallMemberExpression //-> ^(JAVAMETHODCALL $memberExpressionB primaryExpressionIRW argumentList )
   | DOT functionCall
    | parentheticalMemberExpression //-> ^(FUNCTIONCALL $memberExpressionB argumentList)
    | arrayMemberExpression//-> ^(LEFTBRACKET $memberExpressionB baseExpression)
    | DOT primaryExpressionIRW //-> ^(DOT $memberExpressionB primaryExpressionIRW)
    | DOT identifier
  )+
  ;
  
identifierOrReservedWord:
identifier | reservedWord;

  
arrayMemberExpression
	:LEFTBRACKET startExpression RIGHTBRACKET 
	;
  
functionCall
	:multipartIdentifier LEFTPAREN argumentList RIGHTPAREN
	;
	  
parentheticalMemberExpression
	:LEFTPAREN argumentList RIGHTPAREN 
	;
	
javaCallMemberExpression
	:primaryExpressionIRW LEFTPAREN argumentList RIGHTBRACKET 
	;	

memberExpressionSuffix
  : indexSuffix
  | propertyReferenceSuffix
  ;

propertyReferenceSuffix
  : DOT LT* identifier
  ;

indexSuffix
  : LEFTBRACKET  LT* (primaryExpression | parentheticalExpression) LT* RIGHTBRACKET 
  ; 
  
primaryExpressionIRW
	:	literalExpression
	| implicitArray
	| implicitStruct
	| reservedWord
	;
	
literalExpression
	:	STRING_LITERAL
	|	BOOLEAN_LITERAL
	| FLOATING_POINT_LITERAL
	|	INTEGER_LITERAL;
	
	
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
  : ( name=identifier COLON startExpression //-> ^( COLON identifier baseExpression ) 
  )
  | ( name=identifier EQUALSOP startExpression //-> ^( COLON identifier baseExpression ) 
  )
  | startExpression 
  | anonymousFunctionDeclaration
  ;

multipartIdentifier
	:
		identifier (DOT identifierOrReservedWord)*;

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
	:	literalExpression
	| implicitArray
	| implicitStruct
//	|	NULL
	|	identifier
	;
	
parentheticalExpression
	: LEFTPAREN LT* startExpression LT* RIGHTPAREN
;	

implicitArray
  : lc=LEFTBRACKET implicitArrayElements? RIGHTBRACKET //-> ^(IMPLICITARRAY[$lc] implicitArrayElements?) 
  ;
  
implicitArrayElements
  : startExpression ( COMMA startExpression )*
  ;
  
implicitStruct
  : lc=LEFTCURLYBRACKET implicitStructElements? RIGHTCURLYBRACKET //-> ^(IMPLICITSTRUCT[$lc] implicitStructElements?)
  ;
  
implicitStructElements
  : implicitStructExpression ( COMMA implicitStructExpression )*
  ;

implicitStructExpression
  : implicitStructKeyExpression ( COLON | EQUALSOP ) startExpression 
  ;
  
implicitStructKeyExpression
  : multipartIdentifier
  | additiveExpression ( CONCAT additiveExpression )*
  | stringLiteral
  ;

newComponentExpression
  : NEW componentPath LEFTPAREN argumentList RIGHTPAREN
  ;
  
componentPath
  : stringLiteral
  | identifier
  | multipartIdentifier
  ;