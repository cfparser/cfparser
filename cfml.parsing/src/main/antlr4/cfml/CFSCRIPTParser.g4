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
  : (REQUIRED)? (parameterType)? identifier ( EQUALSOP (baseExpression | ternary) )? parameterAttribute* //-> ^(FUNCTION_PARAMETER (REQUIRED)? (parameterType)? identifier (EQUALSOP baseExpression)? parameterAttribute*)
  ;
  
parameterType
  : typeSpec //-> ^( PARAMETER_TYPE typeSpec )
  ;

componentAttribute
  : identifier (COLON identifier)? op=EQUALSOP (baseExpression | ternary) //-> ^(COMPONENT_ATTRIBUTE identifier (COLON identifier)? baseExpression)
  ;
//i=identifier EQUALSOP^ v=baseExpression
  
functionAttribute
  : identifier op=EQUALSOP (baseExpression | ternary) //-> ^(FUNCTION_ATTRIBUTE[$op] identifier baseExpression)
  ;
  
parameterAttribute
  : identifier EQUALSOP (baseExpression | ternary) //-> ^(PARAMETER_ATTRIBUTE identifier baseExpression)
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
  |   ternary SEMICOLON
  |   baseExpression SEMICOLON
  |   SEMICOLON // empty statement
  ;
   
condition
  : LEFTPAREN (baseExpression | compareExpression) RIGHTPAREN
  ;
  
returnStatement
  : RETURN SEMICOLON
  | RETURN baseExpression SEMICOLON
  | RETURN ternary SEMICOLON
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
  : FOR LEFTPAREN ( localAssignmentExpression | assignmentExpression )? SEMICOLON ( assignmentExpression )? SEMICOLON  ( baseExpression )? RIGHTPAREN statement
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
  : i=identifier EQUALSOP (baseExpression | ternary)
  ;


//--- expression engine grammar rules (a subset of the cfscript rules)
  
expression 
	: localAssignmentExpression EOF
	|	assignmentExpression EOF
	|   ternary EOF
	|	baseExpression EOF
	;
	
localAssignmentExpression 
	:	VAR identifier ( EQUALSOP (baseExpression | ternary) )? //-> ^( VARLOCAL identifier ( EQUALSOP baseExpression )? ) 
	;

assignmentExpression 
  : (baseExpression|ternary)
     ( ( EQUALSOP | PLUSEQUALS | MINUSEQUALS | STAREQUALS | SLASHEQUALS | MODEQUALS | CONCATEQUALS )  
     	(baseExpression|ternary)
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
	| equalityExpression
	;	
	
ternary
   : (compareExpression | unaryExpression) QUESTIONMARK (baseExpression | ternary) COLON (baseExpression | ternary) //-> ^(TERNARY equivalentExpression localAssignmentExpression localAssignmentExpression)
   ;

impliedExpression
	:	baseExpression ( IMP baseExpression )
	;

equivalentExpression
	:	baseExpression ( EQV baseExpression )
	;

xorExpression
	:	baseExpression ( XOR baseExpression )
	;
	
orExpression
	:	baseExpression ( ( OR | OROPERATOR ) baseExpression )
	;
	
andExpression
	:	baseExpression ( ( AND | ANDOPERATOR ) baseExpression )
	;
	
notExpression
	:	( NOT | NOTOP ) baseExpression 
	;

equalityExpression
    : baseExpression
      ( equalityOperator1 baseExpression )
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
	:	unaryExpression ( CONCAT (baseExpression | ternary) )
	;
	
additiveExpression
	:	unaryExpression ( (PLUS|MINUS) (baseExpression | ternary) )
	;

modExpression
	:	unaryExpression  ( MOD (baseExpression | ternary) )
	;
	
intDivisionExpression
	:	unaryExpression ( BSLASH (baseExpression | ternary) )
	;

multiplicativeExpression
	:	unaryExpression ( (STAR|SLASH) (baseExpression | ternary) )
	;
	
powerOfExpression
	:	unaryExpression ( POWER (baseExpression | ternary) )
	;
	
unaryExpression
	: MINUS (baseExpression | ternary) //-> ^(MINUS memberExpression)
	| PLUS (baseExpression | ternary) //-> ^(PLUS memberExpression)
	| MINUSMINUS (baseExpression | ternary) //-> ^(MINUSMINUS memberExpression) 
	| PLUSPLUS (baseExpression | ternary) //-> ^(PLUSPLUS memberExpression)
	| identifier (DOT primaryExpressionIRW (LEFTPAREN argumentList RIGHTPAREN)*)*
  | identifier MINUSMINUS //-> ^(POSTMINUSMINUS memberExpression)
  | identifier PLUSPLUS //-> ^(POSTPLUSPLUS memberExpression)
  | primaryExpression
  | parentheticalExpression
//  | functionCall
  | memberExpression
  ;
	

	
memberExpression
	:	POUND_SIGN (memberExpressionB | functionCall) POUND_SIGN
	| (memberExpressionB | functionCall | newComponentExpression)
	;
	
memberExpressionB
  : ( //primaryExpression
    identifier 
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
  
arrayMemberExpression
	:LEFTBRACKET (baseExpression | ternary) RIGHTBRACKET 
	;
  
functionCall
	:(identifier | multipartIdentifier) LEFTPAREN argumentList RIGHTPAREN
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
	:	STRING_LITERAL
	|	BOOLEAN_LITERAL
	| FLOATING_POINT_LITERAL
	|	INTEGER_LITERAL
	| implicitArray
	| implicitStruct
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
  : ( identifier COLON (baseExpression | ternary) //-> ^( COLON identifier baseExpression ) 
  )
  | ( identifier EQUALSOP (baseExpression | ternary) //-> ^( COLON identifier baseExpression ) 
  )
  | baseExpression 
  ;

multipartIdentifier
	:
		identifier (DOT identifier)+;

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
	|	identifier
	;
	
parentheticalExpression
	: LEFTPAREN LT* ternary LT* RIGHTPAREN
	| LEFTPAREN LT* baseExpression LT* RIGHTPAREN
;	

implicitArray
  : lc=LEFTBRACKET implicitArrayElements? RIGHTBRACKET //-> ^(IMPLICITARRAY[$lc] implicitArrayElements?) 
  ;
  
implicitArrayElements
  : (baseExpression | ternary) ( COMMA (baseExpression | ternary) )*
  ;
  
implicitStruct
  : lc=LEFTCURLYBRACKET implicitStructElements? RIGHTCURLYBRACKET //-> ^(IMPLICITSTRUCT[$lc] implicitStructElements?)
  ;
  
implicitStructElements
  : implicitStructExpression ( COMMA implicitStructExpression )*
  ;

implicitStructExpression
  : implicitStructKeyExpression ( COLON | EQUALSOP ) (baseExpression | ternary) 
  ;
  
implicitStructKeyExpression
  : identifier ( DOT ( identifier | reservedWord ) )*
  | additiveExpression ( CONCAT additiveExpression )*
  | STRING_LITERAL
  ;

newComponentExpression
  : NEW componentPath LEFTPAREN argumentList RIGHTPAREN
  ;
  
componentPath
  : STRING_LITERAL
  | identifier ( DOT identifier )*
  ;